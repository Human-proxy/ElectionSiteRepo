<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue';
import { usePageTracking } from '@/composables/usePageTracking';

// Enable page tracking across all routes
const { trackCurrentPage } = usePageTracking();

// Track initial page on mount
onMounted(() => {
  trackCurrentPage();
});
</script>