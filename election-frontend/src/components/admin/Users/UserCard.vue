<script setup>
import IconTrash from '@/components/icons/IconTrash.vue'
import { defineProps, defineEmits } from 'vue'

const props = defineProps({ user: { type: Object, required: true } })
const emit = defineEmits(['delete'])

function onDelete() {
  emit('delete', props.user.id)
}

// small computed-like helper without adding full composition API import
const roleLabel = (props.user.roles && props.user.roles[0])
    ? (props.user.roles[0] === 'USER' ? 'Gebruiker' : 'Beheerder')
    : 'n.v.t.'
</script>

<template>
  <div class="user-container relative rounded-md !m-4 !p-6 bg-white shadow-md w-72">
    <div class="!bg-blue-500 absolute top-0 left-0 rounded-t-md w-full h-2"></div>
    <p class="font-semibold">{{ user.username }}</p>
    <p class="text-sm text-gray-600">{{ user.email }}</p>
    <p class="text-xs mt-1">Rol: {{ roleLabel }}</p>
    <div class="acties !mt-4 flex justify-end">
      <button @click="onDelete" class="!bg-red-500 text-white !p-2 !font-bold rounded-md !hover:bg-red-600 flex items-center gap-1">
        <IconTrash />
        Verwijder
      </button>
    </div>
  </div>
</template>

<style scoped>
.user-container {
  /* Ensure the card does not stretch inside a flex row */
  align-self: flex-start;
}
</style>
