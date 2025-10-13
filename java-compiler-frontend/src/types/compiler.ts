export interface PipelineSteep {
  title: string
  status: 'process' | 'success' | 'error'
  params?: unknown
  resultMessage(): string
}

export interface CompilerResponse {
  logs: string
  debugSteeps: PipelineSteep[]
}

export interface RunCodeParams {
  filename: string
  code: string
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
