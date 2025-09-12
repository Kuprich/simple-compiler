<template>
  <div class="compiler-container">
    <CompilerHeader />

    <PanelsLayout>
      <template #left>
        <CodeEditorPanel v-model:code="code" v-model:filename="filename" @run="handleRunCode" />
      </template>

      <template #right>
        <ResultPanel :result="result" :loading="loading" />
      </template>
    </PanelsLayout>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CompilerHeader from './components/CompilerHeader.vue'
import CodeEditorPanel from './components/CodeEditorPanel.vue'
import ResultPanel from './components/ResultPanel.vue'
import { useCompiler } from './composables/useCompiler'
import PanelsLayout from './components/layout/PanelsLayout.vue'

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
    code: code.value,
  })
}
</script>

<style scoped>
.compiler-container {
  padding: 20px;
  margin: 0 auto;
}
</style>
