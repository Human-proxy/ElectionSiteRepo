<script setup lang="ts">


import standartAvatar from '@/assets/default-avatar.jpg'

export interface CommentAuthor {
  id: number
  username: string
  profileImageUrl: string
}

export interface Comment {
  id: number
  content: string
  created: string
  author: CommentAuthor | null
}


// props to pass the data from the parent component into this component
const props = defineProps<{
  comment: Comment
}>()


function avatarUrl() {
  const author = props.comment.author
  if (!author) return standartAvatar
  if (author.profileImageUrl) return author.profileImageUrl
  else return `https://ui-avatars.com/api/?name=${encodeURIComponent(author.username)}&background=random`
}

</script>

<template>

    <div
        class="pl-5 pr-5 pt-3 pb-3 mb-3 mt-3 outline-1 sm:max-w-[70vw] mx-auto min-h-[10vh]
           overflow-auto break-words rounded-[10px] shadow-[0_5px_10px_rgba(0,0,0,0.1)]
            outline-gray-300
            cursor-default max-w-[90vw]"
    >
      <div class="comment">
        <div class="user">
          <img
              :src="avatarUrl()"
              alt="avatar"
              class="w-10 h-10 rounded-full border border-gray-300 object-cover"
          />
          <span v-if="props.comment.author">
          <div class="username">{{ props.comment.author.username }}</div>
        </span>
          <span v-else>
          <div class="username">Anonymous</div>
        </span>
        </div>

        <p class="text mt-2">{{ props.comment.content }}</p>

        <div class="comment-footer mt-2">
          <p class="text-date text-sm text-gray-500">
            {{ new Date(props.comment.created).toLocaleString('nl-NL', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
          }) }}
          </p>
        </div>
      </div>
    </div>
  </template>

<style scoped>
  .username {
    padding: 0 0.5rem;
    font-weight: bold;
  }
  .user {
    display: flex;
    flex-direction: row;
    align-items: center;
  }
  .comment-footer {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
  }
  .comment {
    display: flex;
    flex-direction: column;
  }
  .text {
    font-size: 1rem;
    white-space: pre-line;
  }
</style>