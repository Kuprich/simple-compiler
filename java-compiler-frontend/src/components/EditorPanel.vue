<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import CodeEditor from './CodeEditor.vue'
import { PrimeIcons } from '@primevue/core/api'
import { useTheme } from '@/composables/useTheme'
import NewFilePopover from './NewFilePopover.vue'
import RightClickPopover from './RightClickPopover.vue'
import type { SourceCode } from '@/types/compiler'

defineProps<{ isCompiling: boolean }>()

defineEmits<{
  run: [tabs: SourceCode[]]
  stop: []
}>()

const mainCode = `public class Main {
  public static void main(String[] args) {
    Foo foo = new Foo("World");
    for (int i = 0; i < 3; i++) {
      foo.sayHello();
    }
  }
}`

const fooCode = `public class Foo {
  private final String name;

  public Foo(String name) {
    this.name = name;
  }

  public void sayHello() {
    System.out.println("Hello, " + name + "!");
  }
}`

const files = ref<SourceCode[]>([
  {
    filename: 'Main.java',
    code: mainCode,
  },
  {
    filename: 'Foo.java',
    code: fooCode,
  },
])

const { isDarkTheme, toggleTheme } = useTheme()
const isCreateMode = ref<boolean>(true)
const renameTarget = ref<string>('')

const lastTabElement = ref<HTMLElement | null>(null)

const themeIcon = computed(() => {
  return isDarkTheme.value ? PrimeIcons.MOON : PrimeIcons.SUN
})

const newFilePopoverRef = ref()
const rightClickPopoverRef = ref()

function openNewFilePopover(event: Event) {
  isCreateMode.value = true
  rightClickPopoverRef.value?.hide()

  newFilePopoverRef.value?.show(event, {
    title: 'New file',
    takenNames: files.value.map(f => f.filename),
  })
}

function openRenameFilePopover(filename: string) {
  isCreateMode.value = false
  renameTarget.value = filename
  rightClickPopoverRef.value?.hide()

  nextTick(() => {
    newFilePopoverRef.value?.showFromElement(lastTabElement.value, {
      title: 'Rename file',
      initialName: filename,
      takenNames: files.value
        .map(f => f.filename)
        .filter(n => n !== filename),
    })
  })
}

function openRightClickPopover(event: Event, filename: string) {
  newFilePopoverRef.value?.hide()

  lastTabElement.value = event.currentTarget as HTMLElement

  rightClickPopoverRef.value?.show(event, filename)
}

function deleteFile(filename: string) {
  const index = files.value.findIndex((file) => file.filename === filename)
  if (index !== -1) {
    files.value.splice(index, 1)
  }
}

function onConfirm(filename: string) {
  if (isCreateMode.value) {
    files.value.push({ filename, code: '' })
  } else {
    const file = files.value.find(f => f.filename === renameTarget.value)
    if (file) file.filename = filename
  }
}

</script>

<template>
  <Tabs :value="0" class="tabs">
    <TabList>
      <div class="my-tabs">
        <div>
          <Tab
            v-for="(tab, i) in files"
            :key="i"
            :value="i"
            @contextmenu.prevent="openRightClickPopover($event, tab.filename)"
          >
            {{ tab.filename }}
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
            @click="$emit('run', files)"
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
      <TabPanel v-for="(tab, i) in files" :value="i" :key="i">
        <CodeEditor v-model="tab.code" />
      </TabPanel>
    </TabPanels>
  </Tabs>

  <NewFilePopover ref="newFilePopoverRef" @confirm="onConfirm" />
  <RightClickPopover ref="rightClickPopoverRef" @delete="deleteFile" @rename="openRenameFilePopover" />
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
