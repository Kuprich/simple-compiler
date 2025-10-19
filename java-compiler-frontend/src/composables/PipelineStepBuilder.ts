import type { ApiRequest, PipelineStep } from '@/types/compiler'
import { reactive, type Reactive } from 'vue'


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

interface CleanupBody {
  codeDir: string
  containerId: string
}

export class PipelineStepBuilder {

  static JSON_HEADERS = { 'Content-Type': 'application/json' }
  static HOST = 'http://localhost:8080/api/compiler'

  static buildSaveStep(body: SaveBody): Reactive<PipelineStep> {
    const saveRequest: ApiRequest<SaveBody> = {
      method: 'POST',
      url: '/save',
      headers: this.JSON_HEADERS,
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

  static buildPullStep(): Reactive<PipelineStep>{
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

  static buildPrepareStep (body: PrepareBody): Reactive<PipelineStep> {
    const prepareRequest: ApiRequest<PrepareBody> = {
      method: 'POST',
      url: '/prepare',
      headers: this.JSON_HEADERS,
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

  static buildExecuteStep (body: ExecuteBody): Reactive<PipelineStep> {
    const executeRequest: ApiRequest<ExecuteBody> = {
      method: 'POST',
      url: '/execute',
      headers: this.JSON_HEADERS,
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

  static buildCollectStep(body: CollectBody): Reactive<PipelineStep> {
    const collectRequest: ApiRequest<CollectBody> = {
      method: 'POST',
      url: '/collectLogs',
      headers: this.JSON_HEADERS,
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

  static buildCleanupStep(body: CleanupBody): Reactive<PipelineStep> {
    const cleanupRequest: ApiRequest<CleanupBody> = {
      method: 'POST',
      url: '/cleanup',
      headers: this.JSON_HEADERS,
      body: body,
    }

    const cleanupStep = reactive<PipelineStep>({
      title: 'Cleanup.',
      status: 'process',
      resultMessage: (): string => 'Success',
      request: cleanupRequest,
    })

    return cleanupStep
  }

}
