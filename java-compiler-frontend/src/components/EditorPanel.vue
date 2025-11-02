<script setup lang="ts">
import { ref } from 'vue'
import CodeEditor from './CodeEditor.vue'
import { PrimeIcons } from '@primevue/core/api'

defineProps<{ isCompiling: boolean }>()

const runIcon = PrimeIcons.PLAY
const stopIcon = PrimeIcons.STOP
const sunIcon = PrimeIcons.SUN

const code = ref(`public class Main {
    public static void main(String[] args) {
      for (int i = 0; i < 5; i++)
        System.out.println("Hello, World!");
    }
}`)

const className = 'Main.java'

defineEmits<{
  run: [code: string, className: string]
  stop: []
}>()

const isDarkTheme = ref<boolean>(false)

function toggleTheme() {

  isDarkTheme.value = !isDarkTheme.value

  if (isDarkTheme.value) {
    document.documentElement.classList.add('my-app-dark')
  } else {
    document.documentElement.classList.remove('my-app-dark')
  }
}

</script>

<template>
  <Tabs value="0" class="tabs">
    <TabList>
      <div class="my-tabs">
        <div>
          <Tab value="0">Main.java</Tab>
        </div>
        <div class="controls">
          <Button
            :icon="runIcon"
            :disabled="isCompiling"
            severity="success"
            variant="text"
            @click="$emit('run', code, className)"
          />
          <Button
            :icon="stopIcon"
            :disabled="!isCompiling"
            severity="danger"
            variant="text"
            @click="$emit('stop')"
          />
           <Button
            :icon="sunIcon"
            severity="primary"
            variant="text"
            @click="toggleTheme"
          />
        </div>
      </div>
    </TabList>
    <TabPanels>
      <TabPanel value="0">
        <CodeEditor v-model="code" :isDarkTheme="isDarkTheme"/>
      </TabPanel>
    </TabPanels>
  </Tabs>
</template>

<style scoped>
.tabs {
  --p-tabs-tabpanel-padding: 0;
  --p-tabs-tab-font-weight: 400;
  --p-tabs-tab-padding: 0.3rem 1.125rem;
}
.my-tabs {
  display: flex;
  flex: 1;
  justify-content: space-between;
  align-items: center;
}

.my-tabs .controls {
  --p-button-padding-x: 0;
  --p-button-padding-y: 0.2rem;
}
</style>
