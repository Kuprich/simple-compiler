<script setup lang="ts">
import type { SourceCode } from '@/types/compiler';
import { ref } from 'vue'

const op = ref()
const filename = ref<string>('')
const existingFiles = ref<SourceCode[]>()

defineExpose({
  show(event: Event, files: SourceCode[]) {
    op.value?.show(event)
    existingFiles.value = files
  },
  hide: () => op.value?.hide(),
  toggle: (event: Event) => op.value?.toggle(event),
})

const emit = defineEmits<{
  save: [name: string]
}>()

function saveClick() {
  emit('save', filename.value)
  filename.value = ''
  op.value?.hide()
}

function checkFileName(): boolean {
  console.log(existingFiles)
  if (filename.value.length < 1) return false

  const fileExists = existingFiles.value?.some((file) => file.filename === filename.value)
  if (fileExists) return false

  return true
}
</script>
<template>
  <Popover ref="op" dismissable class="po">
    <div class="flex flex-col gap-4">
      <div>
        <span class="font-medium block mb-2">New file</span>
        <InputGroup>
          <InputText v-model="filename" />
          <Button
            icon="pi pi-check"
            class="p-0"
            size="small"
            @click="saveClick"
            :disabled="!checkFileName()"
          ></Button>
        </InputGroup>
      </div>
    </div>
  </Popover>
</template>
