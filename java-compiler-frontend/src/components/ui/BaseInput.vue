<template>
  <input
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
    :placeholder="placeholder"
    :type="type"
    :class="['base-input', { disabled: disabled }]"
    :disabled="disabled"
  />
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
  placeholder?: string
  type?: string
  disabled?: boolean
}>()

defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<style scoped>
.base-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.base-input:focus {
  border-color: #42b883;
}

.base-input.disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>
