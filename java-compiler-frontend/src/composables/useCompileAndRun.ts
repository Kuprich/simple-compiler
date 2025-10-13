import type {
  ApiRequest,
  ApiResponse,
  CompilerResponse,
  PipelineSteep,
  RunCodeParams,
} from '@/types/compiler'
import { reactive, ref, type Reactive } from 'vue'
import axios from 'axios'

export function useCompileAndRun() {
  const HOST = 'http://localhost:8080/api/compiler'
  const JSON_HEADERS = { 'Content-Type': 'application/json' }

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

  interface CollectBody {
    containerId: string
  }

  const compilerResponse = ref<CompilerResponse>({
    logs: '',
    debugSteeps: []
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
        status: 'process',
        resultMessage: (): string => `Source code saved to temp directory: ${saveSteep.params}`,
      })

      _codeDir = (await performSteep(saveSteep, saveRequest)) as string

      if (saveSteep.status === 'error') return


      // 2. pull docker image
      const pullRequest: ApiRequest = {
        method: 'GET',
        url: '/pullImage',
      }

      const pullSteep = reactive<PipelineSteep>({
        title: 'Pulling docker image.',
        status: 'process',
        resultMessage: (): string => 'Image pulled successefuly: (TODO: print image name and version)'
      })

      await performSteep(pullSteep, pullRequest)

      if (pullSteep.status === 'error') return

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
        status: 'process',
        resultMessage: (): string => `Container is running. Container id: ${prepareSteep.params}`
      })

      _containerId = await performSteep(prepareSteep, prepareRequest) as string

      if (prepareSteep.status === 'error') return

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
        status: 'process',
        resultMessage: (): string => 'Success'
      })

      await performSteep(executeSteep, executeRequest)

      if (executeSteep.status === 'error') return

      // 5. Collect logs
      const collectRequest: ApiRequest<CollectBody> = {
        method: 'POST',
        url: '/collectLogs',
        headers: JSON_HEADERS,
        body: {
          containerId: _containerId,
        },
      }

      const collectSteep = reactive<PipelineSteep>({
        title: 'Collect logs.',
        status: 'process',
        resultMessage: (): string => 'Success'
      })

      compilerResponse.value.logs = await performSteep(collectSteep, collectRequest) as string

    } catch (ex) {
      compilerResponse.value.logs += ex instanceof Error ? ex.message : 'Unknown Error\n'
    }
  }

  const performSteep = async <T>(
    steep: Reactive<PipelineSteep>,
    request: ApiRequest<T>,
  ): Promise<string | void> => {
    compilerResponse.value.debugSteeps.push(steep)
    const response = await fetch(request)
    if (response.success) {
      steep.status = 'success'
      steep.params = response.data
      return response.data
    } else {
      steep.status = 'error'
      steep.resultMessage = ():string => `Error: ${response.error}`
    }
  }

  const fetch = async <T>(request: ApiRequest<T>): Promise<ApiResponse> => {
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
