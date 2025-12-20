<script setup lang="ts">
import type { MenuItem } from 'primevue/menuitem'
import { ref } from 'vue'

const op = ref()
const currentFile = ref<string>('')

defineExpose({
  show(event: Event, filename: string) {
    currentFile.value = filename
    op.value?.show(event)
  },
  hide: () => op.value?.hide(),
})

const emit = defineEmits<{
  delete: [filename: string],
  rename: [filename: string]
}>()

function deleteClick() {
  emit('delete', currentFile.value)
  op.value?.hide()
}
function renameClick() {
  emit('rename', currentFile.value)
  op.value?.hide()
}

const items: MenuItem[] = [
  {
    label: 'Delete',
    command: () => deleteClick(),
  },
  {
    label: 'Rename',
    command: () => renameClick()
  },
]
</script>

<template>
  <Popover ref="op" dismissable class="right-popover border-0">
    <div class="flex flex-col gap-4">
      <Menu :model="items" class="rigtht-menu-popover"/>
    </div>
  </Popover>
</template>

<style>
.right-popover .p-popover-content {
  padding: 0 !important;
}
.rigtht-menu-popover{
  border: none!important;
}

</style>
