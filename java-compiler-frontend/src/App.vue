<script setup lang="ts">
import { ref } from 'vue'
import EditorPanel from './components/EditorPanel.vue'
import OutputPanel from './components/OutputPanel.vue'
import { useCompileAndRun } from './composables/useCompileAndRun'

const outputValue = ref('')

const { result, isCompiling, runCode } = useCompileAndRun()

const handleRun = async (code: string, filename: string) => {
  outputValue.value = ''
  await runCode({ code, filename })
  outputValue.value = result.value
}
</script>

<template>
  <div class="wrapper">
    <Splitter layout="vertical" class="splitter">
      <SplitterPanel class="pane1" :size="75" :minSize="25">
        <EditorPanel @run="handleRun" />
      </SplitterPanel>
      <SplitterPanel :size="25" :minSize="25">
        <OutputPanel :outputValue :isCompiling />
      </SplitterPanel>
    </Splitter>
  </div>
</template>

<style scoped>
.wrapper {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
.splitter {
  display: flex;
  flex: 1;
  border: none;
}
</style>
