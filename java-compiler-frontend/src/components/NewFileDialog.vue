<script setup lang="ts">
import { ref } from 'vue';


const visible = defineModel<boolean>('visible')

const name = ref<string>('')

const emit = defineEmits<{
  save: [name: string]
}>()

function saveClick(){
  visible.value = false
  emit('save', name.value)
  name.value = ''
}

</script>

<template>
  <Dialog modal v-model:visible="visible" header="New File" :style="{ width: '25rem' }" pt:mask:class="backdrop-blur-sm" >
    <span class="text-surface-500 dark:text-surface-400 block"></span>
    <div class="flex items-center gap-4 mb-8">
      <label for="email" class="font-semibold w-24">Name</label>
      <InputText id="email" class="flex-auto" autocomplete="off" v-model="name"/>
    </div>
    <div class="flex justify-end gap-2">
      <Button type="button" label="Cancel" severity="secondary" @click="visible = false"></Button>
      <Button type="button" label="Save" @click="saveClick"></Button>
    </div>
  </Dialog>
</template>
