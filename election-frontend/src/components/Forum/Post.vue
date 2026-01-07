<script setup lang="ts">

  import { useRouter } from 'vue-router'
  import chatImg from '@/assets/chat.png'
  import likeImg from '@/assets/thumbs-up.png'
  import {onMounted, ref} from "vue";
  import { useCommentCount} from "@/composables/useCommentCount";
  import {useAvatar} from "@/composables/useAvatar";
  import Tag from "@/components/Forum/Tag.vue";
  import IconWithInfoComponent from "@/components/IconWithInfoComponent.vue";

  export interface PostAuthor {
    id: number
    username: string
    profileImageUrl: string
    deletedAt: string | null
  }
  export interface Tag {
    id: number
    tagName: string
  }
  export interface Post{
    id: number
    title: string
    content: string
    author: PostAuthor | null
    tags: Tag[]
    created: string
  }

  // props to pass the data from the parent component into this component
  const props = defineProps<{
    post: Post;
  }>()
  const router = useRouter()
  function onclickDetail() {
    router.push(`/detail/${props.post.id}`);
  }

  const {
    numberOfComments,
      commentTotal
  } = useCommentCount();
  const {
    pfp,
      avatarUrl,
  } = useAvatar();
  onMounted(() => {
    commentTotal(props.post.id)
    avatarUrl(props.post.author)

  })
</script>

<template>
  <div class="pl-5 pr-5 pt-3 pb-3 mb-5 mt-5 outline-1 sm:max-w-[70vw] mx-auto sm:max-h-[50vh] min-h-[25vh] overflow-auto break-words rounded-[10px] shadow-[0_5px_10px_rgba(0,0,0,0.3)] hover:bg-gray-100
         transform transition-all duration-200 outline-gray-300
         hover:scale-[1.01] cursor-pointer max-w-[90vw] max-h-[35]" @click="onclickDetail" >
    <div class="post">
    <div class="Likes">
<!--      <img-->
<!--          :src="likeImg"-->
<!--          class="likeimg"-->
<!--      />-->
<!--      <p>0</p>-->
    </div>
    <div class="container">
    <div class="user">
      <img
          :src="pfp"
          alt="avatar"
          class="w-10 h-10 rounded-full border border-gray-300 object-cover"
      />

      <span class="spanWithIcon" v-if="props.post.author.deletedAt">
        <div class="username">(Deleted User)</div>
        <icon-with-info-component title="" context="User is deleted"></icon-with-info-component>
      </span>
      <span v-else-if="props.post.author">
        <div class="username">{{ props.post.author.username }}</div>
      </span>
      <span class="spanWithIcon" v-else>
          <div class="username">(Deleted User)</div>
        <icon-with-info-component title="" context="User is deleted"></icon-with-info-component>
      </span>
    </div>
    <p class="title">{{props.post.title}}</p>
      <div class="tags">
      <Tag v-for="tag in props.post.tags" :key="tag.id" :tag="tag" />
      </div>
    <p class="text">{{props.post.content}}</p>
    <div class="post-footer">
      <div class="reactions">
      <img
          :src="chatImg"
          class="w-5 h-5 pr-1"
      />
      <p>{{numberOfComments}} reacties</p>
      </div>
      <p class="text-date">
        {{ new Date(props.post.created).toLocaleString('nl-NL', {
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
  </div>

</template>

<style scoped>
.spanWithIcon {
  display: flex;
  align-items: center;
}
.tags{
  display: flex;
  max-height: 30px;
  overflow: hidden;
  flex-wrap: wrap;
  gap: 6px;
}

.title {
  font-size: 2rem;
}
.user {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.username {
  padding: 0 0.5rem;
  font-weight: bold;
}
.reactions {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0 0.5rem 0 0;
}
.post-footer {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.container {
  display: flex;
  flex-direction: column;
}
.post {
  display: flex;
  flex-direction: row;
}
.likeimg {
  width: 2rem;
  margin-right: 0.5rem;
}
.Likes {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0 3rem 0 0;
  font-weight: bold;
  font-size: 1.2rem;
}

</style>