<script setup lang="ts">
import type { CompilerResponse } from '@/types/compiler'
import Accordion from 'primevue/accordion'

defineProps<{ compilerResponse: CompilerResponse; isCompiling: boolean }>()
</script>

<template>
  <div class="outputWrapper h-full flex overflow-auto">
    <Accordion :value="['1','0']" multiple>
      <AccordionPanel value="0">
        <AccordionHeader>Build info</AccordionHeader>
        <AccordionContent>
          <div
            v-for="(steep, index) in compilerResponse.debugSteps"
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
              <span v-if="steep.status != 'process'">
                {{ steep.resultMessage() }}
              </span>
            </p>
          </div>
        </AccordionContent>
      </AccordionPanel>

      <AccordionPanel value="1">
        <AccordionHeader>Output</AccordionHeader>
        <AccordionContent>
           <pre>{{ compilerResponse.logs }}</pre>
        </AccordionContent>
      </AccordionPanel>
    </Accordion>
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
.p-accordion{
  display: flex;
  flex-direction: column;
  flex: 1;
  --p-accordion-header-padding: 0.7rem 0.7rem;
}
</style>
