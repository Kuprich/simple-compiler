import type { CompilerResponse, RunCodeParams } from '@/types/compiler'
import { extractClassName } from '@/utils/javaParser'
import { ref } from 'vue'

export function useCompiler() {
  const result = ref<string>('')
  const loading = ref<boolean>(false)

  async function runCode(params: RunCodeParams): Promise<void> {
    loading.value = true
    result.value = ''

    try {

      const validation = extractClassName(params.code)
      if (validation.error) {
        result.value = `Ошибка: ${validation.error}`
        loading.value = false
        return
      }

      params.filename = validation.filename

      const response = await fetch('http://localhost:8080/api/compiler/run', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
      })

      if (!response.ok) {
        throw new Error(`Ошибка сервера: ${response.status}`)
      }

      const data: CompilerResponse = await response.json()

      if (data.logs) {
        result.value = data.logs
      } else if (data.success == false) {
        result.value = `Ошибка: ${data.containerId}`
      } else {
        result.value = 'Компиляция завершена успешно (нет вывода)'
      }
    } catch (ex: unknown) {
      console.error('Ошибка:', ex)
      if (ex instanceof Error) {
        result.value = `Ошибка: ${ex.message}`
      } else {
        result.value = 'Неизвестная ошибка'
      }
    } finally {
      loading.value = false
    }
  }

  return {
    result,
    loading,
    runCode,
  }
}
