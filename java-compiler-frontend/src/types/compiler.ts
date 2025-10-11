export interface PipelineSteep {
  title: string
  succes?: boolean
  resultMessage?: string
}

export interface PipelineSteep_v2 {
  title: string
  status: 'process' | 'success' | 'error'
  params?: unknown
  resultMessage(): string
}

export interface CompilerResponse {
  logs: string
  debugSteeps: PipelineSteep[]
  debugSteeps_v2: PipelineSteep_v2[]
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
