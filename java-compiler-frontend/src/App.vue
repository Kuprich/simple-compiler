<template>
  <div class="compiler-container">
    <CompilerHeader />

    <div class="panels-container">
      <!-- Левая панель с редактором кода -->
      <CodeEditorPanel v-model:code="code" v-model:filename="filename" @run="handleRunCode" />

      <!-- Правая панель с результатом -->
      <ResultPanel :result="result" :loading="loading" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CompilerHeader from './components/CompilerHeader.vue'
import CodeEditorPanel from './components/CodeEditorPanel.vue'
import ResultPanel from './components/ResultPanel.vue'
import { useCompiler } from './composables/useCompiler'

// Данные компонента
const filename = ref<string>('Hello.java')
const code = ref<string>(`public class Hello {
  public static void main(String[] args) {
    System.out.println("Hello from Java!");
  }
}`)

// composable для логики компилятора
const { result, loading, runCode } = useCompiler()

// ф-я запуска кода
const handleRunCode = async () => {
  await runCode({
    filename: filename.value,
    code: code.value
  })
}
</script>

<style scoped>
.compiler-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
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
</style>
