<script setup lang="ts">
import infoImg from '@/assets/info.png'
import {nextTick, onMounted, ref} from "vue";

defineProps({
  context: String,
  title: String
})
const upHere = ref(false)
const tooltipRef = ref<HTMLElement | null>(null)
const tooltipPosition = ref<'right' | 'left'>('right')

const checkTooltip = async (event: MouseEvent) => {
  upHere.value = true
  await nextTick()
if (tooltipRef.value) {
  const position = tooltipRef.value.getBoundingClientRect()
  const vw = window.innerWidth
  if (position.right > vw) {
    tooltipPosition.value = 'left'
  }
}

}
const reset = () => {
  tooltipPosition.value = 'right'
}
onMounted(() => {
  window.addEventListener('resize', reset)
})
</script>

<template>
<div>
  <div v-if="upHere" @mouseleave="upHere = false">
    <div
        ref="tooltipRef"
        class="context"
        :class="tooltipPosition">
      <div class="title">{{title}}</div>
      <div>{{context}}</div>
    </div>
  </div>
<img class="icon"
    @mouseover="checkTooltip"
     @mouseleave="upHere = false"
    :src="infoImg"
    alt="Information icon"
/>
</div>
</template>

<style scoped>
.icon {
  min-width: 20px;
  max-width: 20px;
  height: 20px;
  margin-left: 3px;
}
.context {
  background-color: #e5ffff;
  width: max-content;
  max-width: 300px;
  border: 1px solid #e5e7eb;
  padding: 8px 12px;
  border-radius: 6px;
  position: absolute;
  z-index: 10;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  pointer-events: none;
  text-align: left;
  color: black;
  font-weight: normal;
  font-size: medium;
  text-transform: none;
  letter-spacing: normal;
}
.context.right {
  transform: translateX(30px) translateY(-50%);
}
.context.right::after {
  content: "";
  position: absolute;
  top: 50%;
  right: 100%;
  margin-left: -10px;
  border-width: 10px;
  border-style: solid;
  border-color: transparent #e5ffff transparent transparent;
  --drop-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}
.context.left {
  transform: translateX(-100%) translateY(-50%);
}
.context.left {
  transform: translateX(-100%) translateY(-50%);
}
.context.left::after {
  content: "";
  position: absolute;
  top: 50%;
  left: 100%;
  border-width: 10px;
  border-style: solid;
  border-color: transparent transparent transparent #e5ffff;
}

.title {
  font-weight: bold;
  text-align: center;
}
</style>