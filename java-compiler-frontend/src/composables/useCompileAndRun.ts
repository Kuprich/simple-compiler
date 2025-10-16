import type {
  ApiRequest,
  ApiResponse,
  CompilerResponse,
  PipelineStep,
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
    debugSteps: [],
  })

  const isCompiling = ref(false)
  const abortController = ref<AbortController>()

  async function runCode(params: RunCodeParams) {
    abortController.value = new AbortController()
    const signal = abortController.value.signal

    isCompiling.value = true

    let _codeDir = ''
    let _containerId = ''

    compilerResponse.value.logs = ''
    compilerResponse.value.debugSteps = []

    try {
      // 1. Save source code

      const saveBody: SaveBody = {
        code: params.code,
        filename: params.filename,
      }

      const saveStep = buildSaveStep(saveBody)
      _codeDir = (await performStep(saveStep, signal)) as string

      if (saveStep.status === 'error') return

      // 2. pull docker image

      const pullStep = buildPullStep()
      await performStep(pullStep, signal)

      if (pullStep.status === 'error') return

      // 3. prepare docker image and run

      const prepareStep = buildPrepareStep({ codeDir: _codeDir })
      _containerId = (await performStep(prepareStep, signal)) as string

      if (prepareStep.status === 'error') return

      // 4. execute container with timeout
      const executeStep = buildExecuteStep({ containerId: _containerId })
      await performStep(executeStep, signal)

      if (executeStep.status === 'error') return

      // 5. Collect logs
      const collectStep = buildCollectStep({ containerId: _containerId })
      compilerResponse.value.logs = (await performStep(collectStep, signal)) as string
    } catch {}
    isCompiling.value = false
  }

  const performStep = async (
    step: Reactive<PipelineStep>,
    signal: AbortSignal,
  ): Promise<string | void> => {
    compilerResponse.value.debugSteps.push(step)

    if (signal.aborted) {
      step.resultMessage = (): string => 'Stoppped by user'
      return
    }

    try {
    } catch (ex) {
      if (axios.isCancel(ex)) {
        step.resultMessage = (): string => 'Stoppped by user'
        return
      } else {
        step.resultMessage = (): string => `Error: ${(ex as Error).message}`
        return
      }
    }

    const response = await fetch(step.request, signal)

    if (response.success) {
      step.status = 'success'
      step.params = response.data
      return response.data
    } else {
      step.status = 'error'
      step.resultMessage = (): string => `Error: ${response.error}`
    }
  }

  const fetch = async <T>(request: ApiRequest<T>, signal: AbortSignal): Promise<ApiResponse> => {
    try {
      const requestHandlers = {
        GET: () => axios.get(`${HOST}${request.url}`, { headers: request.headers, signal }),
        POST: () =>
          axios.post(`${HOST}${request.url}`, request.body, { headers: request.headers, signal }),
      }

      const response = await requestHandlers[request.method]()
      return await response.data
    } catch (ex) {
      throw ex
    }
  }

  const stopExecution = () => {
    if (abortController.value) {
      abortController.value.abort()

      compilerResponse.value.debugSteps = []
      compilerResponse.value.logs = 'Cancelled by user...'

      isCompiling.value = false
    }
  }

  const buildSaveStep = (body: SaveBody): Reactive<PipelineStep> => {
    const saveRequest: ApiRequest<SaveBody> = {
      method: 'POST',
      url: '/save',
      headers: JSON_HEADERS,
      body: body,
    }

    const saveStep = reactive<PipelineStep>({
      title: 'Saving source code.',
      status: 'process',
      resultMessage: (): string => `Source code saved to temp directory: ${saveStep.params}`,
      request: saveRequest,
    })

    return saveStep
  }

  const buildPullStep = (): Reactive<PipelineStep> => {
    const pullRequest: ApiRequest = {
      method: 'GET',
      url: '/pullImage',
    }

    const pullStep = reactive<PipelineStep>({
      title: 'Pulling docker image.',
      status: 'process',
      resultMessage: (): string =>
        'Image pulled successefuly: (TODO: print image name and version)',
      request: pullRequest,
    })

    return pullStep
  }

  const buildPrepareStep = (body: PrepareBody): Reactive<PipelineStep> => {
    const prepareRequest: ApiRequest<PrepareBody> = {
      method: 'POST',
      url: '/prepare',
      headers: JSON_HEADERS,
      body: body,
    }

    const prepareStep = reactive<PipelineStep>({
      title: 'Preparing image and run.',
      status: 'process',
      resultMessage: (): string => `Container is running. Container id: ${prepareStep.params}`,
      request: prepareRequest,
    })

    return prepareStep
  }

  const buildExecuteStep = (body: ExecuteBody): Reactive<PipelineStep> => {
    const executeRequest: ApiRequest<ExecuteBody> = {
      method: 'POST',
      url: '/execute',
      headers: JSON_HEADERS,
      body: body,
    }

    const executeStep = reactive<PipelineStep>({
      title: 'Execute container with timeout (Todo: get execution timeout).',
      status: 'process',
      resultMessage: (): string => 'Success',
      request: executeRequest,
    })

    return executeStep
  }

  const buildCollectStep = (body: CollectBody): Reactive<PipelineStep> => {
    const collectRequest: ApiRequest<CollectBody> = {
      method: 'POST',
      url: '/collectLogs',
      headers: JSON_HEADERS,
      body: body,
    }

    const collectStep = reactive<PipelineStep>({
      title: 'Collect logs.',
      status: 'process',
      resultMessage: (): string => 'Success',
      request: collectRequest,
    })

    return collectStep
  }

  return { compilerResponse, isCompiling, runCode, stopExecution }
}
