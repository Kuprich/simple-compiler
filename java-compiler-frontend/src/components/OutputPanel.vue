<script setup lang="ts">
import type { CompilerResponse } from '@/types/compiler';

defineProps<{ compilerResponse: CompilerResponse; isCompiling: boolean }>()
</script>

<template>
  <div v-if="isCompiling" class="flex justify-center items-center h-full">
    <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i>
  </div>

  <div v-else class="outputWrapper h-full flex overflow-auto">
    <div class="flex flex-1 p-2">
      <pre class="text-sm">{{ compilerResponse.logs }}</pre>
    </div>
  </div>
</template>

<style scoped></style>
