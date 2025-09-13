<template>
  <Codemirror
    v-model="codeValue"
    :style="editorStyle"
    placeholder="Input your code here..."
    :autofocus="true"
    :indent-with-tab="true"
    :tab-size="4"
    :extensions="extensions"
  />
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { java } from '@codemirror/lang-java'
import { Codemirror } from 'vue-codemirror';

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const codeValue = ref(props.modelValue)

// Extention for CodeMirror
const extensions = [java()]

// Editor Style
const editorStyle = computed(() => ({
  height: '100%',
  width: '100%',
  fontSize: '14px',
  fontFamily: "'Monaco', 'Menlo', 'Ubuntu Mono', monospace"
}))


watch(codeValue, (newValue) => {
  emit('update:modelValue', newValue)
})
</script>


