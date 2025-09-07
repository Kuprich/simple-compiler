<template>
  <textarea
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
    :placeholder="placeholder"
    :class="['base-textarea', { disabled: disabled }]"
    :disabled="disabled"
    spellcheck="false"
  ></textarea>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
  placeholder?: string
  disabled?: boolean
}>()

defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<style scoped>
.base-textarea {
  width: 100%;
  height: 100%;
  padding: 15px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: none;
  outline: none;
  background-color: #f6f8fa;
  transition: border-color 0.2s;
}

.base-textarea:focus {
  border-color: #42b883;
}

.base-textarea.disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>
