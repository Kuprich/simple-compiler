import type { CompilerResponse, RunCodeParams } from '@/types/compiler'
import { ref } from 'vue'
import axios from 'axios'

export function useCompileAndRun() {

  interface ApiRespose {
    success: boolean
    error: string
    data: string
  }

  interface SaveRequest {
    filename: string
    code: string
  }

  interface PrepareRequest {
    codeDir: string
  }

  interface ExecuteRequest {
    containerId: string
  }

  interface CollectLogsRequest {
    containerId: string
  }

  const compilerResponse = ref<CompilerResponse>({
    success: false,
    logs: '',
    containerId: '',
  })

  const isCompiling = ref(false)

  async function runCode(params: RunCodeParams) {
    isCompiling.value = false

    let _codeDir = ''
    let _containerId = ''

    try {

      // 1. save source code
      const saveRequest: SaveRequest = {
        code: params.code,
        filename: params.filename
      }

      compilerResponse.value.logs = '1. Saving source code. '

      const saveResponse = await saveSourceCode(saveRequest)
      if (saveResponse.success) {
        _codeDir = saveResponse.data
        compilerResponse.value.logs += `Source code saved to temp directory: ${_codeDir}\n`
      }
      else compilerResponse.value.logs += `Error: ${saveResponse.error}\n`

      // 2. pull docker image
      compilerResponse.value.logs += '2. Pulling docker image. '
      const pullResponse = await pullImage()
      if (pullResponse.success)
        compilerResponse.value.logs += 'Image pulled successefuly: (TODO: print image name and version)\n'
      else
        compilerResponse.value.logs += `Error: ${pullResponse.error}\n`

      // 3. prepare docker image and run
      const prepareRequest: PrepareRequest = {
        codeDir: saveResponse.data
      }
      compilerResponse.value.logs += '3. Preparing image and run. '
      const prepareResponse = await prepareImageAndRun(prepareRequest)
      if (prepareResponse.success){
        _containerId = prepareResponse.data
        compilerResponse.value.logs += `Container is running. Container id: ${_containerId}\n`
      }
      else
        compilerResponse.value.logs += `Error: ${prepareResponse.error}\n`

      // 4. execute container with timeout
      const executeRequest: ExecuteRequest = {
        containerId: _containerId
      }
      compilerResponse.value.logs += '4. Execute container with timeout (Todo: get execution timeout). '
      const executeResponse = await executeWithTimeout(executeRequest)
      if (executeResponse.success){
        compilerResponse.value.logs += 'Success\n'
      }
      else
        compilerResponse.value.logs += `Error: ${executeResponse.error}\n`

      // 5. Collect logs
      const collectLogsRequest: CollectLogsRequest = {
        containerId: _containerId
      }
      compilerResponse.value.logs += '5. Collect logs. '
      const collectLogsResponse = await collectLogs(collectLogsRequest)
      if (collectLogsResponse.success)
        compilerResponse.value.logs += `Success\n\n${collectLogsResponse.data}\n`
      else
        compilerResponse.value.logs += `Error: ${collectLogsResponse.error}\n`

    } catch (ex) {
      compilerResponse.value.logs += ex instanceof Error ? ex.message : "Unknown Error\n"
    }
  }

  const saveSourceCode = async (requestBody: RunCodeParams): Promise<ApiRespose> => {
    try {
      const response = await axios.post('http://localhost:8080/api/compiler/save', requestBody, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  const pullImage = async (): Promise<ApiRespose> => {
    try {
      const response = await axios.get('http://localhost:8080/api/compiler/pullImage', {})
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  const prepareImageAndRun = async (requestBody: PrepareRequest): Promise<ApiRespose> => {
    try {
      const response = await axios.post('http://localhost:8080/api/compiler/prepare', requestBody, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  const executeWithTimeout = async (requestBody: ExecuteRequest): Promise<ApiRespose> => {
    try {
      const response = await axios.post('http://localhost:8080/api/compiler/execute', requestBody, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  const collectLogs = async (requestBody: CollectLogsRequest): Promise<ApiRespose> => {
    try {
      const response = await axios.post('http://localhost:8080/api/compiler/collectLogs', requestBody, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  return { compilerResponse, isCompiling, runCode }
}
