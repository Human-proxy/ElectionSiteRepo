<template>
  <div class="w-full sm:w-1/1 mt-5 p-2">
    <div class="cursor-pointer !p-4 bg-white relative rounded shadow hover:shadow-lg transition" @click="$emit('open', post.id)">
      <div class="!bg-blue-500 absolute top-0 left-0 rounded-t w-full h-1.5"></div>
      <div class="flex items-center gap-3 mb-2">
        <div class="font-bold text-lg break-words">{{ post.title }}</div>
        <button
          v-if="showDelete"
          class="ml-auto text-xs px-2 py-1 rounded border text-white flex !font-bold !bg-red-600 hover:!bg-red-700"
          @click.stop="$emit('delete', post.id)"
        >
          <slot name="delete-icon" />
          <span class="!ml-1">Verwijder</span>
        </button>
      </div>
      <div class="text-sm text-gray-700 break-words mb-3">{{ truncated }}</div>
      <div class="text-xs text-gray-500 flex items-center justify-between">
        <div>
          <span v-if="post.author">{{ post.author.username }}</span>
          <span v-else>Anonymous</span>
        </div>
        <div>{{ formattedDate }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  post: { type: Object, required: true },
  truncateAt: { type: Number, default: 220 },
  showDelete: { type: Boolean, default: true }
})

const truncated = computed(() => {
  const text = props.post?.content || ''
  const n = props.truncateAt
  return text.length > n ? text.slice(0, n) + '...' : text
})

const formattedDate = computed(() => {
  const dt = props.post?.created
  try {
    return dt ? new Date(dt).toLocaleDateString('nl-NL', { day: '2-digit', month: 'short', year: 'numeric' }) : ''
  } catch {
    return dt || ''
  }
})
</script>

