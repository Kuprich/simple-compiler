import type {
  ApiRequest,
  ApiResponse,
  CompilerResponse,
  PipelineStep,
  RunCodeParams,
} from '@/types/compiler'
import { ref, type Reactive } from 'vue'
import axios from 'axios'
import { PipelineStepBuilder } from './PipelineStepBuilder'

export function useCompileAndRun() {
  const HOST = 'http://localhost:8080/api/compiler'

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
    let _cleaup_needed = false

    compilerResponse.value.logs = ''
    compilerResponse.value.debugSteps = []

    try {
      // 1. Save source code

      const saveStep = PipelineStepBuilder.buildSaveStep({
        files: params.files
      })
      _codeDir = (await performStep(saveStep, signal)) as string
      if (saveStep.status !== 'success') return

      _cleaup_needed = true

      // 2. pull docker image

      const pullStep = PipelineStepBuilder.buildPullStep()
      await performStep(pullStep, signal)

      if (saveStep.status !== 'success') return

      // 3. prepare docker image and run

      const prepareStep = PipelineStepBuilder.buildPrepareStep({ codeDir: _codeDir })
      _containerId = (await performStep(prepareStep, signal)) as string

      if (saveStep.status !== 'success') return

      // 4. execute container with timeout
      const executeStep = PipelineStepBuilder.buildExecuteStep({ containerId: _containerId })
      await performStep(executeStep, signal)

      if (saveStep.status !== 'success') return

      // 5. Collect logs
      const collectStep = PipelineStepBuilder.buildCollectStep({ containerId: _containerId })
      compilerResponse.value.logs = (await performStep(collectStep, signal)) as string
    } catch {
      return
    } finally {
      isCompiling.value = false
      if (_cleaup_needed) {
        const cleanupSteep = PipelineStepBuilder.buildCleanupStep({
          codeDir: _codeDir,
          containerId: _containerId,
        })
        await performStep(cleanupSteep)
      }
    }
  }

  const performStep = async (
    step: Reactive<PipelineStep>,
    signal?: AbortSignal,
  ): Promise<string | void> => {
    if (signal?.aborted) return

    compilerResponse.value.debugSteps.push(step)

    try {
      const response = await fetch(step.request, signal)

      if (response.success) {
        step.status = 'success'
        step.params = response.data
        return response.data
      } else {
        step.status = 'error'
        step.resultMessage = (): string => `Error: ${response.error}`
      }
    } catch (ex) {
      step.status = 'error'
      step.resultMessage = (): string => `Error: ${(ex as Error).message}`
    }
  }

  const fetch = async <T>(request: ApiRequest<T>, signal?: AbortSignal): Promise<ApiResponse> => {
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

      compilerResponse.value.debugSteps = []
      compilerResponse.value.logs = 'Cancelled by user...'

      abortController.value.abort()

      isCompiling.value = false
    }
  }

  return { compilerResponse, isCompiling, runCode, stopExecution }
}
