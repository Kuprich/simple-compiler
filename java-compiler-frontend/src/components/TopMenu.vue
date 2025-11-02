<script setup lang="ts">
import type { MenuItem } from 'primevue/menuitem'


const items: MenuItem[] = [
  {
    label: "Theme",
    items: [
      {
        label: "Light"
      },
      {
        label: "Dark"
      }
    ]
  }
]
</script>

<template>
  <Menubar :model="items">
    <template #item="{ item, props, hasSubmenu }">
      <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
        <a v-ripple :href="href" v-bind="props.action" @click="navigate">
          <span :class="item.icon" />
          <span>{{ item.label }}</span>
        </a>
      </router-link>
      <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">
        <span :class="item.icon" />
        <span>{{ item.label }}</span>
        <span v-if="hasSubmenu" class="pi pi-fw pi-angle-down" />
      </a>
    </template>
  </Menubar>
</template>

<style scoped>
.p-menubar-submenu{
  z-index: 2;
}
</style>
