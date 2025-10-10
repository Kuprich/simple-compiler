import type { CompilerResponse, PipelineSteep, RunCodeParams } from '@/types/compiler'
import { reactive, ref } from 'vue'
import axios from 'axios'

export function useCompileAndRun() {
  const HOST = 'http://localhost:8080/api/compiler'
  const JSON_HEADERS = { 'Content-Type': 'application/json' }

  interface ApiRequest<T = unknown> {
    url: string
    method: 'GET' | 'POST'
    headers?: Record<string, string>
    body?: T
  }

  interface SaveBody {
    filename: string
    code: string
  }

  interface PrepareBody {
    codeDir: string
  }

  interface ExecuteBody {
    containerId: string
  }

  interface CollectLogsBody {
    containerId: string
  }

  interface ApiRespose {
    success: boolean
    error: string
    data: string
  }

  const compilerResponse = ref<CompilerResponse>({
    logs: '',
    debugSteeps: [],
  })

  const isCompiling = ref(false)

  async function runCode(params: RunCodeParams) {
    isCompiling.value = false

    let _codeDir = ''
    let _containerId = ''

    compilerResponse.value.logs = ''
    compilerResponse.value.debugSteeps = []

    try {
      // 1. save source code

      const saveRequest: ApiRequest<SaveBody> = {
        method: 'POST',
        url: '/save',
        headers: JSON_HEADERS,
        body: {
          code: params.code,
          filename: params.filename,
        },
      }

      const saveSteep = reactive<PipelineSteep>({
        title: 'Saving source code.',
      })

      compilerResponse.value.debugSteeps.push(saveSteep)
      const saveResponse = await fetch(saveRequest)
      saveSteep.succes = saveResponse.success

      if (saveResponse.success) {
        _codeDir = saveResponse.data
        saveSteep.resultMessage = `Source code saved to temp directory: ${_codeDir}`
      } else {
        saveSteep.resultMessage = `Error: ${saveResponse.error}`
        return
      }

      // 2. pull docker image
      const pullRequest: ApiRequest = {
        method: 'GET',
        url: '/pullImage',
      }

      const pullSteep = reactive<PipelineSteep>({
        title: 'Pulling docker image.',
      })

      compilerResponse.value.debugSteeps.push(pullSteep)
      const pullResponse = await fetch(pullRequest)
      pullSteep.succes = pullResponse.success

      if (pullResponse.success) {
        pullSteep.resultMessage = 'Image pulled successefuly: (TODO: print image name and version)'
      } else {
        pullSteep.resultMessage = `Error: ${pullResponse.error}`
        return
      }

      // 3. prepare docker image and run

      const prepareRequest: ApiRequest<PrepareBody> = {
        method: 'POST',
        url: '/prepare',
        headers: JSON_HEADERS,
        body: {
          codeDir: _codeDir,
        },
      }

      const prepareSteep = reactive<PipelineSteep>({
        title: 'Preparing image and run.',
      })

      compilerResponse.value.debugSteeps.push(prepareSteep)
      const prepareResponse = await fetch(prepareRequest)
      prepareSteep.succes = prepareResponse.success

      if (prepareResponse.success) {
        _containerId = prepareResponse.data
        prepareSteep.resultMessage = `Container is running. Container id: ${_containerId}`
      } else {
        prepareSteep.resultMessage = `Error: ${prepareResponse.error}`
        return
      }

      // 4. execute container with timeout
      const executeRequest: ApiRequest<ExecuteBody> = {
        method: 'POST',
        url: '/execute',
        headers: JSON_HEADERS,
        body: {
          containerId: _containerId,
        },
      }

      const executeSteep = reactive<PipelineSteep>({
        title: 'Execute container with timeout (Todo: get execution timeout).',
      })

      compilerResponse.value.debugSteeps.push(executeSteep)

      const executeResponse = await fetch(executeRequest)

      executeSteep.succes = executeResponse.success

      if (executeResponse.success) {
        executeSteep.resultMessage = 'Success'
      } else {
        executeSteep.resultMessage = `Error: ${executeResponse.error}`
        return
      }

      // 5. Collect logs
      const collectLogsRequest: ApiRequest<CollectLogsBody> = {
        method: 'POST',
        url: '/collectLogs',
        headers: JSON_HEADERS,
        body: {
          containerId: _containerId,
        },
      }

      const collectSteep = reactive<PipelineSteep>({
        title: 'Collect logs.',
      })

      compilerResponse.value.debugSteeps.push(collectSteep)
      const collectLogsResponse = await fetch(collectLogsRequest)
      collectSteep.succes = collectLogsResponse.success
      if (collectLogsResponse.success) {
        collectSteep.resultMessage = 'Success.'
        compilerResponse.value.logs = collectLogsResponse.data
      } else {
        collectSteep.resultMessage = `Error: ${collectLogsResponse.error}`
        return
      }
    } catch (ex) {
      compilerResponse.value.logs += ex instanceof Error ? ex.message : 'Unknown Error\n'
    }
  }

  const fetch = async <T>(request: ApiRequest<T>): Promise<ApiRespose> => {
    try {
      const requestHandlers = {
        GET: () => axios.get(`${HOST}${request.url}`, { headers: request.headers }),
        POST: () => axios.post(`${HOST}${request.url}`, request.body, { headers: request.headers }),
      }

      const response = await requestHandlers[request.method]()
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  return { compilerResponse, isCompiling, runCode }
}
