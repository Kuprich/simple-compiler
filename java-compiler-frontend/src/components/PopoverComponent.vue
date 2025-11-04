<script setup lang="ts">
import { ref } from 'vue';

const op = ref()

defineExpose({
  show: (event: Event) => op.value?.show(event),
  hide: () => op.value?.hide(),
  toggle: (event: Event) => op.value?.toggle(event),
})

const emit = defineEmits<{
  save: [name: string]
}>()

const name = ref<string>('')

function saveClick(){
  emit('save', name.value)
  name.value = ''
  op.value?.hide()
}

</script>
<template>
  <Popover ref="op" dismissable class="po">
    <div class="flex flex-col gap-4">
      <div>
        <span class="font-medium block mb-2">New file</span>
        <InputGroup>
          <InputText v-model="name"/>
          <Button icon="pi pi-check" class="p-0" size="small" @click="saveClick" :disabled="name.length < 1"></Button>
        </InputGroup>
      </div>
    </div>
  </Popover>
</template>
