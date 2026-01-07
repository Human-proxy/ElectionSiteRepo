<script setup>
import IconProfile from "@/components/icons/IconProfile.vue";
import IconBook from "@/components/icons/IconBook.vue";
import { defineProps, defineEmits, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({ active: { type: String, default: 'users' } })
const emit = defineEmits(['navigate','search','back'])
const router = useRouter()
const route = useRoute()

// compute active based on the current route, fallback to prop
const activeKey = computed(() => {
  const p = (route.path || '').toLowerCase();
  if (p.startsWith('/admin/forums')) return 'forums';
  if (p.startsWith('/admin/users')) return 'users';
  return props.active || 'users';
})

function isActive(panel) {
  return activeKey.value === panel;
}

// navigate both by event and by updating the router path so the URL reflects the panel state
function go(to) {
  emit('navigate', to);
  if (to === 'users') {
    router.push('/admin/users');
  } else if (to === 'forums') {
    router.push('/admin/forums');
  }
}
</script>

<template>
  <input id="admin-panel-toggle" type="checkbox" class="peer hidden" />
  <label for="admin-panel-toggle"
         class="fixed top-16 left-0 z-20 md:hidden bg-white p-2 rounded shadow cursor-pointer
                transition-transform duration-300
                peer-checked:translate-x-[20rem]">
    <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
    </svg>
  </label>
  <div class="side-panel bg-white w-80 fixed -left-80 md:left-0 z-10 top-0 h-screen shadow-md flex flex-col items-center p-4 transition-all duration-300 peer-checked:left-0">
    <h1 class="!font-bold text-2xl !mx-auto !mt-10">Admin paneel</h1>

    <div class="side-panel-buttons flex flex-col items-center !mt-20 space-y-4 w-full">
      <button type="button" @click="go('users')" :class="['w-[80%] h-12 !my-1 !font-bold rounded-lg flex items-center justify-center gap-2', isActive('users') ? '!bg-blue-500 text-white' : '!text-gray-600 hover:!bg-blue-200']">
        <IconProfile />
        Users
      </button>
      <button type="button" @click="go('forums')" :class="['w-[80%] h-12 !my-1 !font-bold rounded-lg flex items-center justify-center gap-2', isActive('forums') ? '!bg-blue-500 text-white' : '!text-gray-600 hover:!bg-blue-200']">
        <IconBook />
        Forums
      </button>
    </div>

  </div>
</template>

<style scoped>
</style>
