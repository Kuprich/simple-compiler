<script lang="ts" setup></script>

<template>
  <ConfirmDialog group="headless">
    <template #container="{ message, acceptCallback, rejectCallback }">
      <div class="flex flex-col items-center p-8 bg-surface-0 dark:bg-surface-900 rounded">
        <div
          class="rounded-full bg-primary text-primary-contrast inline-flex justify-center items-center h-24 w-24 -mt-20"
        >
          <i class="pi pi-question !text-4xl"></i>
        </div>
        <span class="font-bold text-2xl block mb-2 mt-0">{{ message.header }}</span>
        <p class="mb-0">{{ message.message }}</p>
        <div class="flex items-center gap-2 mt-6">
          <Button label="Delete" @click="acceptCallback" class="w-32" severity="danger" variant="outlined" size="small"></Button>
          <Button label="Cancel" variant="outlined" @click="rejectCallback" class="w-32" size="small"></Button>
        </div>
      </div>
    </template>
  </ConfirmDialog>
</template>
