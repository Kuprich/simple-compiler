<template>
  <Tabs value="0" class="tabs">
    <TabList>
      <div class="my-tabs">
        <div>
          <Tab value="0" class="text-sm">Main.java</Tab>
          <Tab value="1" class="text-sm">Test.java</Tab>
        </div>
        <div class="controls">
          <Button
            :icon="runIcon"
            severity="success"
            variant="text"
            @click="$emit('run', code, className)"
          />
        </div>
      </div>
    </TabList>
    <TabPanels>
      <TabPanel value="0">
        <CodeEditor v-model="code" />
      </TabPanel>
    </TabPanels>
  </Tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CodeEditor from './CodeEditor.vue'
import { PrimeIcons } from '@primevue/core/api'

const runIcon = PrimeIcons.PLAY

const code = ref(`public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`)

const className = 'Main.java'

defineEmits<{
  run: [code: string, className: string]
}>()
</script>

<style scoped>
.tabs {
  --p-tabs-tabpanel-padding: 0;
  --p-tabs-tab-font-weight: 400;
  --p-tabs-tab-padding: 0.6rem 1.125rem;
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
