<script setup lang="ts">

export interface Tag{
  id: number
  tagName: string;
}

const tags = defineProps<{
  tag: Tag;
}>()

const generateHash = (string) => {
  let hash = 0;
  for (const char of string) {
    hash = (hash << 5) - hash + char.charCodeAt(0);
    hash |= 0;
  }
  let hex = (hash >>> 0).toString(16);
  hex = hex.slice(0, 6).padStart(6, "0");
  const R = parseInt(hex.slice(0, 2), 16);
  const G = parseInt(hex.slice(2, 4), 16);
  const B = parseInt(hex.slice(4, 6), 16);
  // https://www.w3.org/TR/WCAG20
  const lightLevel = 0.2126 * R + 0.7152 * G + 0.0722 * B
  if (lightLevel < 60) {
    return "#58f34a";
  }
  return "#" + hex;
};
const emit = defineEmits(["select"]);
const onclickTag = () => {
  emit("select",tags.tag);
}

</script>

<template>
<button class="tag" :style="{ backgroundColor: generateHash(tags.tag.tagName) }" @click="onclickTag">
    {{ tags.tag.tagName }}
  </button>
</template>

<style scoped>

.tag {
  color: black;
  font-size: smaller;
  padding-left: 8px;
  padding-right: 8px;
  margin: 2px;
  border-radius: 20px;
  max-height: 20px;
text-align: center;
}
.tag:hover {
  scale: 1.02;
}

</style>