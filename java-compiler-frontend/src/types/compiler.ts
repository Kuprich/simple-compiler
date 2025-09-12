export interface CompilerResponse {
  success?: boolean
  logs?: string
  containerId?: string
}

export interface RunCodeParams {
  filename: string
  code: string
}

