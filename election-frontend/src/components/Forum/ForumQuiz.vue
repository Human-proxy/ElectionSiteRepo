<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue';
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import Post from "./Post.vue"
import PostSkeleton from "@/components/LayoutComponent/PostSkeleton.vue";
import { usePosts } from '@/composables/usePosts';

const selectedTag = ref("");

const {
  posts,
  loading,
  error,
  isEmpty,
  currentPage,
  totalPages,
  readPost,
  readPostsByTag,
} = usePosts();

const props = defineProps({
  result: Object
});
console.log("ForumQuiz received:", props.result);

watch(
    () => props.result,
    (propsResult) => {
      if (propsResult?.partyName) {
        selectedTag.value = propsResult.partyName;
        readPostsByTag(selectedTag.value);
      }
    },
    { immediate: true }
);


</script>

<template>
  <layout-component>
    <div class="containerForSearchAndPost">
    </div>
    <div v-if="loading">
      <PostSkeleton v-for="n in 10" :key="n" />
    </div>
    <div v-else-if="error" class="error" role="alert">
      <div aria-live="assertive">error posts niet geladen!</div>
    </div>
    <div v-else-if="isEmpty" class="noPosts">
      <p>Geen berichten gevonden...</p>
    </div>
    <!--this creates a post for every post-->
    <div v-else>
      <post v-for="post in posts " :key="post.id" :post="post" />
    </div>
    <div class="pagination" v-if="totalPages > 1">
      <button
          v-for="n in totalPages" :key="n" @click="readPostsByTag(selectedTag,n - 1)" :class="{ active: n - 1 === currentPage }"> {{ n }}
      </button>
    </div>

  </layout-component>
</template>

<style scoped>
.error {
  color: #730000;
  font-weight: bold;
  background-color: #ffaaaa;
  padding: 0.5rem;
  outline: 2px solid red;
  width: 20vw;
  border-radius: 0.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  margin-top: 10vh;
}
.noPosts{
  color: #000000;
  font-weight: bold;
  background-color: #f6f6f6;
  padding: 0.5rem;
  outline: 2px solid #6c6c6c;
  width: 20vw;
  border-radius: 0.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  margin-top: 10vh;
}
.pagination {
  display: flex;
  justify-content: center;
  gap: 0.5rem;

}
.pagination button {
  border: 1px solid #ccc;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  background: #f3f4f6;
  cursor: pointer;
}
.pagination button.active {
  background: #55698e;
  color: white;
}
.pagination button:hover {
  scale: 1.1;
}
.pagination button:hover:not(.active) {
  background: #d3d6da;
  scale: 1.1;
}

.post {
  background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
  color: white;
  font-weight: bold;
  padding: 10px 20px;
  margin: 5px;
  border-radius: 10px;
  align-items: center;
}
.post:hover {
  background: #4d4dbf;
}

.search {
  outline: #1f2937 solid 1px;
  width: 30vw;
  border-radius: 10px;
  margin: 5px;
  padding: 10px 20px;
}
.containerForSearchAndPost {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 2vh;
}

@media (max-width: 640px) {
  .post {
    background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
    color: white;
    font-weight: bold;
    margin: 5px;
    border-radius: 10px;
    align-items: center;
    bottom: 1rem;
    z-index: 1;
    position: fixed;
  }
  .post:hover {
    background: #4d4dbf;
  }

  .search {
    outline: #1f2937 solid 1px;
    width: 70vw;
    height: 7vh;
    border-radius: 10px;
    margin: 5px;
  }
  .containerForSearchAndPost {
    display: flex;
    flex-direction: row;
    justify-content: center;
    margin-top: 2vh;
  }
}
</style>