<script setup lang="ts">
import { computed, ref } from 'vue'

const op = ref()

const name = ref('')
const initialName = ref('')
const takenNames = ref<string[]>([])
const title = ref('')

defineExpose({
  show(
    event: Event,
    options: {
      title: string
      initialName?: string
      takenNames: string[]
    }
  ) {
    title.value = options.title
    name.value = options.initialName ?? ''
    initialName.value = options.initialName ?? ''
    takenNames.value = options.takenNames
    op.value?.show(event)
  },

  showFromElement(
    el: HTMLElement | null,
    options: {
      title: string
      initialName?: string
      takenNames: string[]
    }
  ) {
    if (!el) return

    title.value = options.title
    name.value = options.initialName ?? ''
    initialName.value = options.initialName ?? ''
    takenNames.value = options.takenNames

    op.value?.show({ currentTarget: el })
  },

  hide: () => op.value?.hide(),
})

const emit = defineEmits<{
  confirm: [name: string]
}>()

const isValid = computed(() => {
  if (!name.value) return false
  if (name.value === initialName.value) return false
  return !takenNames.value.includes(name.value)
})

function confirmClick() {
  emit('confirm', name.value)
  name.value = ''
  initialName.value = ''
  op.value?.hide()
}
</script>

<template>
  <Popover ref="op" dismissable>
    <div class="flex flex-col gap-4">
      <span class="font-medium">{{ title }}</span>

      <InputGroup>
        <InputText v-model="name" autofocus />
        <Button
          icon="pi pi-check"
          size="small"
          :disabled="!isValid"
          @click="confirmClick"
        />
      </InputGroup>
    </div>
  </Popover>
</template>
