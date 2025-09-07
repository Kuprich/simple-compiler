<template>
  <button
    :type="type"
    :class="['base-button', { loading: loading, disabled: disabled }]"
    :disabled="disabled || loading"
    @click="$emit('click')"
  >
    <span v-if="loading" class="button-loader"></span>
    <slot v-else></slot>
  </button>
</template>

<script setup lang="ts">
defineProps<{
  type?: 'button' | 'submit' | 'reset'
  loading?: boolean
  disabled?: boolean
}>()

defineEmits<{
  click: []
}>()
</script>

<style scoped>
.base-button {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.base-button:hover:not(.disabled) {
  background-color: #359f72;
}

.base-button.loading {
  background-color: #85d0b3;
  cursor: wait;
}

.base-button.disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.button-loader {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
