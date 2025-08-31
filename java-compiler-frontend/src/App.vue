<template>
  <div class="compiler-container">
    <h1>Java Compiler</h1>

    <div class="panels-container">
      <!-- Левая панель с редактором кода -->
      <div class="editor-panel">
        <h2>Исходный код</h2>
        <textarea
          v-model="code"
          placeholder="Введите ваш Java-код здесь..."
          class="code-editor"
        ></textarea>

        <div class="controls">
          <label for="filename">Имя файла:</label>
          <input
            id="filename"
            type="text"
            v-model="filename"
            placeholder="Hello.java"
            class="filename-input"
          />
          <button @click="runCode" class="run-button">Запуск</button>
        </div>
      </div>

      <!-- Правая панель с результатом -->
      <div class="result-panel">
        <h2>Результат</h2>
        <div v-if="loading" class="loading">Компиляция...</div>
        <div v-else-if="result" class="result-output">{{ result }}</div>
        <div v-else class="placeholder">Результат выполнения появится здесь</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// Определяем интерфейс для ответа API (адаптируйте под ваш реальный ответ)
interface CompilerResponse {
  success?: boolean
  logs?: string
  containerId?: string
  // Добавьте другие поля, которые возвращает ваш API
}

// Данные компонента
const filename = ref<string>('Hello.java')
const code = ref<string>(`public class Hello {
  public static void main(String[] args) {
    System.out.println("Hello from Java!");
  }
}`)
const result = ref<string>('')
const loading = ref<boolean>(false)

// Функция для отправки кода на компиляцию
async function runCode(): Promise<void> {
  loading.value = true
  result.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/compiler/run', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        filename: filename.value,
        code: code.value,
      }),
    })

    if (!response.ok) {
      throw new Error(`Ошибка сервера: ${response.status}`)
    }

    const data: CompilerResponse = await response.json()

    // Обрабатываем разные возможные форматы ответа
    if (data.logs) {
      result.value = data.logs
    } else if (data.success == false) {
      result.value = `Ошибка: ${data.containerId}`
    } else {
      result.value = 'Компиляция завершена успешно (нет вывода)'
    }
  } catch (error: unknown) {
    console.error('Ошибка:', error)
    if (error instanceof Error) {
      result.value = `Ошибка: ${error.message}`
    } else {
      result.value = 'Неизвестная ошибка'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.compiler-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.panels-container {
  display: flex;
  gap: 20px;
  height: 70vh;
}

@media (max-width: 768px) {
  .panels-container {
    flex-direction: column;
    height: auto;
  }
}

.editor-panel,
.result-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.editor-panel {
  background-color: #f6f8fa;
}

.result-panel {
  background-color: #f9f9f9;
}

h2 {
  background-color: #2c3e50;
  color: white;
  margin: 0;
  padding: 15px;
  font-size: 18px;
}

.code-editor {
  flex: 1;
  padding: 15px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  border: none;
  resize: none;
  outline: none;
  background-color: #f6f8fa;
}

.controls {
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
}

.filename-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  flex: 1;
}

.run-button {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.run-button:hover {
  background-color: #359f72;
}

.result-output,
.placeholder,
.loading {
  flex: 1;
  padding: 15px;
  white-space: pre-wrap;
  font-family: monospace;
  overflow: auto;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-style: italic;
}

.placeholder {
  color: #888;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
