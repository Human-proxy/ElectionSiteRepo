<template>
  <NavComponent :isAdminPage="finalIsAdmin" />
  <div class="top-space"></div>
  <div class="layout-content">
    <slot />
  </div>
  <!-- Hide footer on admin pages (explicit prop or route.meta.adminPage) -->
  <FooterComponent v-if="!finalIsAdmin" />
</template>

<script setup>
import NavComponent from "@/components/LayoutComponent/NavComponent.vue";
import FooterComponent from "@/components/LayoutComponent/FooterComponent.vue";
import { useRoute } from 'vue-router';
import { computed } from 'vue';

const props = defineProps({ adminPage: { type: Boolean, default: undefined } });
const route = useRoute();
// If parent explicitly sets adminPage prop, honor it. Otherwise fall back to route.meta.adminPage
const finalIsAdmin = computed(() => typeof props.adminPage === 'boolean' ? props.adminPage : Boolean(route.meta?.adminPage));
console.log(finalIsAdmin.value);
</script>

<style>
svg {
  display: inline;
}
.top-space {
  height: 65px;
  width: 100%;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  background: linear-gradient(to left, #7c3aed, #ff7624);
  width: 31px;
  height: 31px;
  color: white;
  border-radius: 30%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.logo-name {
  font-size: 18px;
  font-weight: bold;
  margin-left: 12px;
  margin-right: 20px;
}
</style>