<template>
  <div class="result-panel">
    <h2>Результат</h2>
    <div v-if="loading" class="loading">Компиляция...</div>
    <div v-else-if="result" class="result-output">{{ result }}</div>
    <div v-else class="placeholder">Результат выполнения появится здесь</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  result?: string
  loading?: boolean
}>()
</script>

<style scoped>
.result-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f9f9f9;
}

h2 {
  background-color: #2c3e50;
  color: white;
  margin: 0;
  padding: 15px;
  font-size: 18px;
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