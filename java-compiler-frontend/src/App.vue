<script setup lang="ts">
import EditorPanel from './components/EditorPanel.vue'
import OutputPanel from './components/OutputPanel.vue'
import { useCompileAndRun } from './composables/useCompileAndRun'

const { compilerResponse, isCompiling, runCode, stopExecution } = useCompileAndRun()

const handleRun = async (code: string, filename: string) => {
  await runCode({ code, filename })
}

</script>

<template>
  <div class="wrapper">
    <!-- <TopMenu /> -->
    <Splitter layout="vertical" class="splitter">
      <SplitterPanel class="pane1" :size="70" :minSize="30">
        <EditorPanel
          @run="handleRun"
          @stop="stopExecution"
          :isCompiling
        />
      </SplitterPanel>
      <SplitterPanel :size="30" :minSize="30">
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
