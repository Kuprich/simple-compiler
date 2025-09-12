<template>

  <div class="panels-layout">
    <div class="left-panel">
      <slot name="left"></slot>
    </div>

    <div class="right-panel">
      <slot name="right"></slot>
    </div>
  </div>

</template>

<script setup lang="ts"></script>

<style scoped>
.panels-layout {
  display: flex;
  gap: 20px;
  height: 70vh;
}

.left-panel,
.right-panel {
  flex: 1;
  display: flex;
  align-items: stretch;
}

@media (max-width: 768px) {
  .panels-layout {
    flex-direction: column;
    height: auto;
  }

  .left-panel,
  .right-panel {
    min-height: 300px;
  }
}
</style>
