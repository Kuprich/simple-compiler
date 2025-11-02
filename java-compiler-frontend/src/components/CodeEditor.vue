<script setup lang="ts">
import { computed } from 'vue'
import { Codemirror } from 'vue-codemirror'
import { java } from '@codemirror/lang-java'
import { oneDark } from '@codemirror/theme-one-dark'

const props = defineProps<{ modelValue: string, isDarkTheme: boolean}>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const code = computed({
  get: () => props.modelValue,
  set: (val: string) => emit('update:modelValue', val),
})

//const extensions = [java()]
const extensions = computed(() => {
  return props.isDarkTheme ? [java(), oneDark] : [java()]
})
</script>

<template>
  <Codemirror
    v-model="code"
    placeholder="Code goes here..."
    class="editor text-sm"
    :autofocus="true"
    :indent-with-tab="true"
    :tab-size="2"
    :extensions="extensions"
  />
</template>
