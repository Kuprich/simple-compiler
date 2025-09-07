<template>
  <div class="editor-panel">
    <h2>Исходный код</h2>
    <BaseTextarea :modelValue="code" placeholder="Введите ваш Java-код здесь..." class="code-editor" />

    <div class="controls">
      <label for="filename">Имя файла:</label>
      <BaseInput id="filename" :modelValue="filename" placeholder="Hello.java" class="filename-input" />
      <BaseButton @click="$emit('run')" class="run-button"> Запуск </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import BaseTextarea from './ui/BaseTextarea.vue';
import BaseInput from './ui/BaseInput.vue'
import BaseButton from './ui/BaseButton.vue'

defineProps<{
  code: string
  filename: string
}>()    

defineEmits<{
  'update:code': [value: string]
  'update:filename': [value: string]
  run: []
}>()
</script>

<style scoped>
.editor-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f6f8fa;
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
  border: none;
  border-radius: 0;
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
  flex: 1;
}
</style>
