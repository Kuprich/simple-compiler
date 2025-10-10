export interface PipelineSteep {
  title: string
  succes?: boolean
  resultMessage?: string
}


export interface CompilerResponse {
  logs: string
  debugSteeps: PipelineSteep[]
}

export interface RunCodeParams {
  filename: string
  code: string
}
