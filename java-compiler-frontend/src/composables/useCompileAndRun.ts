import type { CompilerResponse, RunCodeParams } from '@/types/compiler'
import { ref } from 'vue'

export function useCompileAndRun() {
  const result = ref<string>('')
  const isCompiling = ref(false)

  async function runCode(params: RunCodeParams) {
    isCompiling.value = true
    result.value = ''

    try {
      const response = await fetch('http://localhost:8080/api/compiler/run', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params),
      })

      if (!response.ok) {
        result.value = `ServerError: ${response.status}`
        return
      }

      const data: CompilerResponse = await response.json()

      result.value =
        data.logs ??
        (data.success === false
          ? `Error: ${data.containerId}`
          : 'Compilation completed successfully')
    } catch (ex) {
      result.value = ex instanceof Error ? `Error: ${ex.message}` : 'Unknown error'
    } finally {
      isCompiling.value = false
    }
  }

  return { result, isCompiling, runCode }
}
