<template>
  <router-link v-if="!disabled" :to="to" :class="['nav-link', { selected }]">
    <component :is="icon" class="icon" />
    {{ label }}
  </router-link>
  <span v-else :class="['nav-link', 'disabled', { selected }]">
    <component :is="icon" class="icon" />
    {{ label }}
  </span>
</template>
<script setup>
import { defineProps } from 'vue';
const props = defineProps({
  icon: {
    type: [Object, Function],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  selected: {
    type: Boolean,
    default: false
  },
  to: {
    type: String,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
});
</script>
<style>
.nav-link {
  font-size: 14px;
  margin: 0 10px;
  transition: background-color 0.3s;
  padding: 5px 10px;
  border-radius: 10px;

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.nav-link:hover {
  background: #d3d3d33d;
}

.selected {
  background: #3b82f6;
  color: white;
}

.selected:hover {
  background: #5d94ea;
}

.disabled {
  color: #aaa;
  background: #f3f3f3;
  cursor: not-allowed;
    opacity: 0.7;
}
</style>