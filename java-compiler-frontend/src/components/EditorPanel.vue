<script setup lang="ts">
import { computed, ref } from 'vue'
import CodeEditor from './CodeEditor.vue'
import { PrimeIcons } from '@primevue/core/api'
import { useTheme } from '@/composables/useTheme'
import FilePopover from './FilePopover.vue'
import RightClickPopover from './RightClickPopover.vue'
import type { SourceCode } from '@/types/compiler'
import ConfirmDialogComponent from './ConfirmDialogComponent.vue'
import { useConfirmDialog } from '@/composables/useConfirmDialog'
import { DEFAULT_FILES } from '@/constants/files'
import { useFileManagement } from '@/composables/useFileManagement'
import { findTabElementByFilename } from '@/utils/tabUtils'

defineProps<{ isCompiling: boolean }>()

defineEmits<{
  run: [tabs: SourceCode[]]
  stop: []
}>()

const { isDarkTheme, toggleTheme } = useTheme()
const { confirmDelete } = useConfirmDialog()
const fileManager = useFileManagement(DEFAULT_FILES)

const { files } = fileManager

const isCreateMode = ref<boolean>(true)
const renameTarget = ref<string>('')
const lastTabElement = ref<HTMLElement | null>(null)

const FilePopoverRef = ref<InstanceType<typeof FilePopover>>()
const rightClickPopoverRef = ref<InstanceType<typeof RightClickPopover>>()

const themeIcon = computed(() => {
  return isDarkTheme.value ? PrimeIcons.MOON : PrimeIcons.SUN
})

function openNewFilePopover(event: Event) {
  isCreateMode.value = true
  rightClickPopoverRef.value?.hide()

  FilePopoverRef.value?.show(event, {
    title: 'New file',
    takenNames: fileManager.getFileNames(),
  })
}

function openRenameFilePopover(filename: string) {
  isCreateMode.value = false
  renameTarget.value = filename
  rightClickPopoverRef.value?.hide()

  setTimeout(() => {
    const tab = findTabElementByFilename(filename)

    FilePopoverRef.value?.showFromElement(tab as HTMLElement, {
      title: 'Rename file',
      initialName: filename,
      takenNames: fileManager.getFileNames().filter((n) => n !== filename),
    })
  })
}

function openRightClickPopover(event: Event, filename: string) {
  FilePopoverRef.value?.hide()

  lastTabElement.value = event.currentTarget as HTMLElement

  rightClickPopoverRef.value?.show(event, filename)
}

function openDeleteDialog(filename: string) {
  confirmDelete({
    header: 'Confirm deletion',
    message: `Are you sure you want to delete "${filename}"?`,
    acceptIcon: 'pi pi-exclamation-circle',
    onAccept: () => {
      fileManager.deleteFile(filename)
    },
    onReject: () => {
      console.log('Deletion cancelled')
    },
  })
}

function deleteFile(filename: string) {
  openDeleteDialog(filename)
}

function onConfirm(filename: string) {
  if (isCreateMode.value) {
    fileManager.addFile({ filename, code: '' })
  } else {
    fileManager.renameFile(renameTarget.value, filename)
  }
}
</script>

<template>
  <Tabs :value="0" class="tabs">
    <TabList>
      <div class="my-tabs">
        <div>
          <Tab
            v-for="(file, i) in files"
            :key="i"
            :value="i"
            @contextmenu.prevent="openRightClickPopover($event, file.filename)"
          >
            {{ file.filename }}
          </Tab>
          <Button
            class="tab-btn"
            :icon="PrimeIcons.PLUS"
            severity="secondary"
            variant="text"
            @click="openNewFilePopover($event)"
          />
        </div>
        <div class="controls">
          <Button
            :icon="PrimeIcons.PLAY"
            :disabled="isCompiling"
            severity="success"
            variant="text"
            @click="$emit('run', fileManager.files.value)"
          />
          <Button
            :icon="PrimeIcons.STOP"
            :disabled="!isCompiling"
            severity="danger"
            variant="text"
            @click="$emit('stop')"
          />
          <Button :icon="themeIcon" severity="secondary" variant="text" @click="toggleTheme" />
        </div>
      </div>
    </TabList>
    <TabPanels>
      <TabPanel v-for="(file, i) in files" :value="i" :key="i">
        <CodeEditor v-model="file.code" />
      </TabPanel>
    </TabPanels>
  </Tabs>

  <FilePopover ref="FilePopoverRef" @confirm="onConfirm" />
  <RightClickPopover
    ref="rightClickPopoverRef"
    @delete="deleteFile"
    @rename="openRenameFilePopover"
  />

  <ConfirmDialogComponent />
</template>

<style scoped lang="scss">
.my-tabs {
  display: flex;
  flex: 1;
  justify-content: space-between;
  align-items: center;
}

.my-tabs .controls button,
button.tab-btn {
  --p-button-padding-x: 0;
  --p-button-padding-y: 0.2rem;
  border-radius: 0%;
}
</style>
