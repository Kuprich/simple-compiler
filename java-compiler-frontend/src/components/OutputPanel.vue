<script setup lang="ts">
import type { CompilerResponse } from '@/types/compiler'

defineProps<{ compilerResponse: CompilerResponse; isCompiling: boolean }>()

</script>

<template>
  <div v-if="isCompiling" class="flex justify-center items-center h-full">
    <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i>
  </div>

  <div v-else class="outputWrapper h-full flex overflow-auto">
    <div class="flex flex-1 p-2 flex-col">
      <div class="mb-3">
        <div
          v-for="(steep, index) in compilerResponse.debugSteeps_v2"
          :key="index"
          class="debug-items"
        >
          <p>
            <span>
              <i
                class="pi pi-check status-icon"
                v-if="steep.status == 'success'"
                style="color: var(--p-primary-color)"
              ></i>
              <i
                class="pi pi-spin pi-spinner status-icon"
                v-if="steep.status == 'process'"
                style="color: var(--p-surface-500)"
              ></i>
              <i
                class="pi pi-times status-icon"
                v-if="steep.status == 'error'"
                style="color: var(--p-red-500)"
              ></i>
            </span>
            {{ steep.title }}
            {{ steep.resultMessage() }}
          </p>
        </div>
      </div>

      <pre>{{ compilerResponse.logs }}</pre>
    </div>
  </div>
</template>

<style scoped>
.debug-items {
  font-size: 0.9rem;
  color: var(--p-surface-500);
}
.status-icon {
  font-size: 0.7rem;
}
</style>
