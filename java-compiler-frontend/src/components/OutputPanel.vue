<script setup lang="ts">
defineProps<{ outputValue: string; isCompiling: boolean }>()
</script>

<template>
  <div v-if="isCompiling" class="flex justify-center items-center h-full">
    <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i>
  </div>

  <div v-else class="outputWrapper h-full flex overflow-auto">
    <div class="flex flex-1 p-2">
      <pre class="text-sm">{{ outputValue }}</pre>
    </div>
  </div>
</template>

<style scoped></style>
