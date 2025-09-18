<template>
  <div class="editor-panel">
    <h2 class="editor-header">
      Source code
      <div class="controls">
        <span class="header-info">Java • JDK-17.0</span>
        <BaseButton @click="$emit('run')" class="run-button">
          <FontAwesomeIcon icon="fa-solid fa-play" />
          Run</BaseButton>
      </div>
    </h2>

    <CodeEditor
      :modelValue="code"
      @update:modelValue="$emit('update:code', $event)"
      class="code-editor"
    />

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

<style>
.cm-editor {
  overflow: hidden;
}
.cm-scroller {
  overflow: auto;
}
</style>

<style scoped>

.editor-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f6f8fa;
  border-right: 1px solid #2c3e50;
}

.editor-header {
  background-color: #2c3e50;
  color: white;
  margin: 0;
  padding: 15px;
  font-size: 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: monospace;
}

.header-info {
  font-size: 14px;
  opacity: 0.8;
  padding: 0 20px;
}

.controls {
  display: flex;
  align-items: center;
}

.code-editor {
  flex: 1;
}

.filename-input {
  flex: 1;
}

</style>
