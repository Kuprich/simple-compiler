<script setup lang="ts">
import { computed, nextTick, onUnmounted, ref } from 'vue'

const op = ref()

const name = ref('')
const initialName = ref('')
const takenNames = ref<string[]>([])
const title = ref('')

// handle Enter & Esc keys
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && isValid.value) {
    event.preventDefault()
    confirmClick()
  } else if (event.key === 'Escape') {
    event.preventDefault()
    op.value?.hide()
  }
}

defineExpose({
  show(
    event: Event,
    options: {
      title: string
      initialName?: string
      takenNames: string[]
    },
  ) {
    title.value = options.title
    name.value = options.initialName ?? ''
    initialName.value = options.initialName ?? ''
    takenNames.value = options.takenNames
    op.value?.show(event)
    nextTick(() => {
      document.addEventListener('keydown', handleKeydown)

      // focus to input
      const input = document.querySelector('.p-popover-content input')
      if (input instanceof HTMLInputElement) {
        input.focus()
        input.select()
      }
    })
  },

  showFromElement(
    el: HTMLElement | null,
    options: {
      title: string
      initialName?: string
      takenNames: string[]
    },
  ) {
    if (!el) return

    title.value = options.title
    name.value = options.initialName ?? ''
    initialName.value = options.initialName ?? ''
    takenNames.value = options.takenNames

    op.value?.show({ currentTarget: el })

    nextTick(() => {
      document.addEventListener('keydown', handleKeydown)

      // show to input
      const input = document.querySelector('.p-popover-content input')
      if (input instanceof HTMLInputElement) {
        input.focus()
        input.select()
      }
    })
  },

  hide: () => {
    op.value?.hide()
    document.removeEventListener('keydown', handleKeydown)
  },
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
  document.removeEventListener('keydown', handleKeydown)
}

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <Popover ref="op" dismissable>
    <div class="flex flex-col gap-4">
      <span class="font-medium">{{ title }}</span>

      <InputGroup>
        <InputText
          v-model="name"
          autofocus
          placeholder="Input filename"
          @keyup.enter="confirmClick"
        />
        <Button
          icon="pi pi-check"
          size="small"
          :disabled="!isValid"
          @click="confirmClick"
          @keyup.enter="confirmClick"
        />
      </InputGroup>
    </div>
  </Popover>
</template>
