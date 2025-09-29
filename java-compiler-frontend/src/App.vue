<script setup lang="ts">
import EditorPanel from './components/EditorPanel.vue'
import OutputPanel from './components/OutputPanel.vue'
import { useCompileAndRun } from './composables/useCompileAndRun'

const {compilerResponse, isCompiling, runCode } = useCompileAndRun()

const handleRun = async (code: string, filename: string) => {
  await runCode({ code, filename })
}
const handleStop = () => {
  compilerResponse.value.logs = "stopped"
  isCompiling.value = false
}
</script>

<template>
  <div class="wrapper">
    <Splitter layout="vertical" class="splitter">
      <SplitterPanel class="pane1" :size="75" :minSize="25">
        <EditorPanel @run="handleRun" @stop="handleStop" :isCompiling/>
      </SplitterPanel>
      <SplitterPanel :size="25" :minSize="25">
        <OutputPanel :compilerResponse :isCompiling />
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
