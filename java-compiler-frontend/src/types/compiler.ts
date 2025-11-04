export interface PipelineStep {
  title: string
  status: 'process' | 'success' | 'error'
  params?: unknown
  resultMessage(): string
  request: ApiRequest
}

export interface CompilerResponse {
  logs: string
  debugSteps: PipelineStep[]
}

export interface RunCodeParams {
  files: SourceCode[]
}

export interface ApiRequest<T = unknown> {
  url: string
  method: 'GET' | 'POST'
  headers?: Record<string, string>
  body?: T
}

export interface ApiResponse {
  success: boolean
  error: string
  data: string
}

export interface SourceCode {
  filename: string
  code: string
}

