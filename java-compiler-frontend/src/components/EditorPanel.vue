<script setup lang="ts">
import { computed, ref } from 'vue'
import CodeEditor from './CodeEditor.vue'
import { PrimeIcons } from '@primevue/core/api'
import { useTheme } from '@/composables/useTheme'
import PopoverComponent from './PopoverComponent.vue';
import type { SourceCode } from '@/types/compiler';

defineProps<{ isCompiling: boolean }>()

defineEmits<{
  run: [tabs: SourceCode[]]
  stop: []
}>()

const mainCode = `public class Main {
    public static void main(String[] args) {
      Foo foo = new Foo();
      for (int i = 0; i < 5; i++)
        System.out.println(foo.sayHello());
    }
}`

const fooCode = `public class Foo {
  public void sayHello(string name){
    System.out.println("Hello, " + name);
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

const themeIcon = computed(() => {
  return isDarkTheme.value ? PrimeIcons.MOON : PrimeIcons.SUN
})

const popoverRef = ref()

function newFile(filename: string){
  files.value.push({
    filename: filename,
    code: ''
  })
}

</script>

<template>
  <Tabs :value="0" class="tabs">
    <TabList>
      <div class="my-tabs">
        <div>
          <Tab v-for="(tab, i) in files" :key="i" :value="i">
            {{ tab.filename }}
          </Tab>
          <Button
            class="tab-btn"
            :icon="PrimeIcons.PLUS"
            severity="secondary"
            variant="text"
            @click="popoverRef.toggle($event)"
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

  <!-- <NewFileDialog v-model:visible="isDialogOpen" @save="newFile"/> -->
  <PopoverComponent ref="popoverRef" @save="newFile"/>

</template>

<style scoped lang="scss">
.tabs {
  --p-tabs-tabpanel-padding: 0;
  --p-tabs-tab-font-weight: 400;
  --p-tabs-tab-padding: 0.25rem 1.125rem;
}
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
