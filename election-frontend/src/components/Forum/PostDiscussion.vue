<script setup lang="ts">
import {onMounted, ref} from 'vue';
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import {useRouter} from "vue-router";
import {useTags} from "@/composables/useTags";
import Tag from "@/components/Forum/Tag.vue";
import Post from "@/components/Forum/Post.vue";

// ref so it checks for automatic changes
const title = ref('');
const content = ref('');
const router = useRouter()
const unselectedTags = ref<Tag[]>([]);
const selectedTags = ref<Tag[]>([]);
const {
    tags,
    error,
    readTags
} = useTags();

// @ts-ignore
const createPost = async () => {
  try {
    const token = localStorage.getItem("auth.token");
    if (!token) {
      alert("You must be logged in to post.");
      return;
    }

    try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/posts`, {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify({
        title: title.value,
        content: content.value,
        tags: selectedTags.value.map(t => ({ id: t.id }))
      }),
    });

    //empty it for new post
    title.value = '';
    content.value = '';
    const json = await res.json();
    console.log(json);
    router.push(`/detail/${json.id}`);

  } catch (err) {
    // Handle post creation error
  }
} catch (err) {
    // Handle authentication error
  }
};


const onTagClick = (tag: Tag) => {
console.log(tag);
selectedTags.value.push(tag);
unselectedTags.value.splice(unselectedTags.value.indexOf(tag), 1);
    return;
  }
const onTagClickSelected = (tag: Tag) => {
  console.log(tag);
  unselectedTags.value.push(tag);
  selectedTags.value.splice(selectedTags.value.indexOf(tag), 1);
  return;
}
onMounted(async () => {
  await readTags();
  unselectedTags.value = [...tags.value];
});
</script>

<template>
  <layout-component>
    <div class="forumPost">
      <div class="container">
        <h1>Maak post</h1>
      </div>
      <form @submit.prevent="createPost">
        <input v-model="title" placeholder="Titel" required />
        <p>Catogorieën</p>
        <div class="containerTags">
          <Tag v-for="tag in unselectedTags" :key="tag.id" :tag="tag" @select="onTagClick" />
        </div>
        <div class="containerTags">
          <Tag v-for="tag in selectedTags" :key="tag.id" :tag="tag" @select="onTagClickSelected" />
        </div>
        <textarea class="textArea" v-model="content" placeholder="tekst" required></textarea>

        <div class="container2">
          <button class="cancel">Cancel</button>
          <button class="post" type="submit">Plaats post</button>

        </div>
      </form>
    </div>
  </layout-component>
</template>

<style scoped>

input, textarea {
  width: 100%;
  margin-bottom: 15px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #fafafa;
}
input:focus, textarea:focus {
  border-color: #953bf6;
  box-shadow: 0 0 0 3px rgba(106, 59, 246, 0.3);
  outline: none;
}
.textArea {
  width: 100%;

}
.containerTags {
  max-height: 10vh;
  padding: 10px;
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fafafa;
  overflow-y: auto;
  max-width: 30vw;
  min-width: 30vw;
}

.forumPost {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;


}
.forumPost form {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  resize: vertical;
}

.post {
  background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
  color: white;
  font-weight: bold;
  padding: 5px 10px;
  margin: 5px;
  border-radius: 10px;
  align-items: center;
}
.post:hover {
  background: #4d4dbf;
}
.cancel {
  background: white;
  font-weight: bold;
  outline: #1f2937 solid 1px;
  padding: 5px 10px;
  margin: 5px;
  border-radius: 10px;
  align-items: center;

}
.cancel:hover {
  background: #bd3636;
  color: white;
}
.container {
  text-align: center;
  padding: 20px;
  font-weight: bold;
  font-size: 20px;
}
.container2 {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>