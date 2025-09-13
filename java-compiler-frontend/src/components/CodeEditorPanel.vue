<template>
  <div class="editor-panel">
    <h2>Source code</h2>
    <CodeEditor
      :modelValue="code"
      @update:modelValue="$emit('update:code', $event)"
      class="code-editor"
    />

    <div class="controls">

      <BaseButton @click="$emit('run')" class="run-button">
        Run
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import CodeEditor from './ui/CodeEditor.vue'
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
}

.controls {
  padding: 15px;
  display: flex;
  align-items: center;
  justify-content: right;
  gap: 10px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
}

.filename-input {
  flex: 1;
}
</style>
