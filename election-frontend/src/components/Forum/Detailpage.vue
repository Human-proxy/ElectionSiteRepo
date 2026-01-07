<script setup lang="ts">

import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import CommentComponent from "@/components/Forum/CommentComponent.vue";
import Tag from "@/components/Forum/Tag.vue";
import IconWithInfoComponent from "@/components/IconWithInfoComponent.vue";


export interface PostAuthor {
  id: number
  username: string
  profileImageUrl: string
  deletedAt: string
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

export interface CommentAuthor {
  id: number;
  username: string;
  profileImageUrl: string;
}

export interface PostComment {
  id: number;
  content: string;
  created: string;
  author: CommentAuthor | null;
}

const route = useRoute()
const post = ref<Post | null>(null);
const comments = ref<PostComment[]>([])
const loading = ref(true)

const readPost = async () => {
  try {
    const id = route.params.id
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/posts/${id}`);
    if (!res.ok) throw new Error('Failed to fetch posts');
    post.value = await res.json();
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false
  }
};


const readComments = async () => {
  try {
    console.log(post.value.id);
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/comment/find?postId=${post.value.id}`);
    if (!res.ok) throw new Error('Failed to fetch comments');
    comments.value = await res.json();
    console.log(comments.value);
  } catch (err) {
    console.error(err);
  }
}

  function avatarUrl() {
    const author = post.value?.author
    if (!author) return '/src/assets/default-avatar.jpg'
    else if (author.deletedAt) return `/src/assets/default-avatar.jpg`
    else if (author.profileImageUrl) return author.profileImageUrl
    else return `https://ui-avatars.com/api/?name=${encodeURIComponent(author.username)}&background=random`
  }

const createComment = async () => {
  try {
    const token = localStorage.getItem("auth.token");
    if (!token) {
      alert("You must be logged in to reply.");
      return;
    }

    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/comments`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify({
        post: { id: post.value?.id },
        content: content.value,
      }),
    });

    if (!res.ok) {
      const text = await res.text();
      alert("Could not post comment.");
      return;
    }

    const newComment = await res.json();
    comments.value.push(newComment);
    content.value = "";
  } catch (err) {
    console.error(err);
  }
};


const isLoggedIn = ref(false);
const content = ref("");


onMounted(async () => {
  await readPost();
  await readComments();
  const token = localStorage.getItem("auth.token");
  isLoggedIn.value = !!token;
}
)
</script>

<template>
<layout-component>
  <div v-if="post">
  <div class="pl-5 pr-5 pt-3 pb-3 mb-5 mt-5 outline-1 max-w-[70vw] mx-auto min-h-[30vh] overflow-auto break-words rounded-[10px] shadow-[0_5px_10px_rgba(0,0,0,0.3)] outline-gray-300" >
    <div class="post">
      <div class="container">
        <div class="user">
          <img
              :src="avatarUrl()"
              alt="avatar"
              class="w-10 h-10 rounded-full border border-gray-300 object-cover"
          />
<!--          <span v-if="post.author">-->
<!--        <div class="username">{{ post.author.username }}</div>-->
<!--       </span>-->
<!--          <span v-else>-->
<!--          <div class="username">Anonymous</div>-->
<!--      </span>-->
          <span class="spanWithIcon" v-if="post.author.deletedAt">
        <div class="username">(Deleted User)</div><icon-with-info-component title="" context="User is deleted"></icon-with-info-component>
      </span>
          <span v-else-if="post.author">
        <div class="username">{{ post.author.username }}</div>
        </span>
          <span class="spanWithIcon" v-else>
          <div class="username">(Deleted User)</div><icon-with-info-component title="" context="User is deleted"></icon-with-info-component>
      </span>
        </div>
        <p class="title">{{post.title}}</p>
        <div class="tags">
          <Tag v-for="tag in post.tags" :key="tag.id" :tag="tag" />
        </div>
        <p class="text">{{post.content}}</p>
        <div class="post-footer">
          </div>
          <p class="text-date">
            {{ new Date(post.created).toLocaleString('nl-NL', {
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
    <div v-if="isLoggedIn">
      <form @submit.prevent="createComment">
        <div class="newReply max-w-[70vw] min-w-[20vw] mx-auto min-h-[5vh]">
          <textarea class="reply" placeholder="tekst" v-model="content" required></textarea>
          <button class="postReply" type="submit">Reageer</button>
        </div>
      </form>
    </div>
    <CommentComponent
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
    />
  </div>


</layout-component>
</template>

<style scoped>
.spanWithIcon {
  display: flex;
  align-items: center;
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

.newReply {
  display: flex;
  justify-content:center;
  padding: 10px 20px;
  margin: 5px;
  border-radius: 10px;
  align-items: center;
 outline: #9f9f9f solid 1px;
  flex-direction: column;
}
.reply {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  color: #1f2937;
  transition: all 0.2s
}

.postReply {
  display: flex;
  align-self: flex-end;
  background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
  color: white;
  font-weight: bold;
  padding: 5px 20px;
  margin: 5px;
  border-radius: 10px;
  align-items: center;
}
.postReply:hover {
  background: #4d4dbf;
}
</style>