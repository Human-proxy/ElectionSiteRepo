<script setup lang="ts">
import {watch} from "vue";

export interface Party{
  id: number
  partyName: string
}

const model = defineModel<boolean>({ default: true });

watch(model, (val) => {
  console.log("Checkbox changed:", val);
});
const props = defineProps<{
  party: Party;
}>()
</script>

<template>
  <div class="selection">
  <label class="switch">
    <input type="checkbox" v-model="model">
    <span class="slider"></span>
  </label>
<div class="partyName">{{props.party.partyName}}</div>
  </div>
</template>

<style scoped>
.selection {
  display: flex;
  padding: 8px;
  outline: #c6c6c6 solid 1px;
}
.partyName {
  align-items: center;
}
</style>