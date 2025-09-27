import type { CompilerResponse, RunCodeParams } from '@/types/compiler'
import { ref } from 'vue'

export function useCompileAndRun() {

  const compilerResponse = ref<CompilerResponse>({
    success: false,
    logs: '',
    containerId: '',
  })

  const isCompiling = ref(false)

  async function runCode(params: RunCodeParams) {
    isCompiling.value = true

    try {
      const response = await fetch('http://localhost:8080/api/compiler/run', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params),
      })

      if (!response.ok) {
        compilerResponse.value.logs = `ServerError: ${response.status}`
        return
      }

      const data: CompilerResponse = await response.json()

      if (data.success) {
        compilerResponse.value.logs = data.logs || 'Compilation completed successfully'
      } else {
        compilerResponse.value.logs = data.logs || `Error: ${data.containerId}`
      }

      compilerResponse.value.success = data.success
      compilerResponse.value.containerId = data.containerId

    } catch (ex) {
      compilerResponse.value.logs = ex instanceof Error ? `Error: ${ex.message}` : 'Unknown error'
    } finally {
      isCompiling.value = false
    }
  }

  return { compilerResponse, isCompiling, runCode }
}
