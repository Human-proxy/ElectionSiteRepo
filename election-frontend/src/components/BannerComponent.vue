<template>
  <div id="home-banner">
    <div class="navigation-buttons">
      <button @click="increment">
        Click Me! count = {{ count }}
      </button>
      <router-link to="/results" class="nav-button">
        View 2023 Election Results
      </router-link>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
const count = ref(0);
function increment() {
  count.value++;
}
</script>
<style>
#home-banner {
  display: flex;
  justify-content: center;
}
.navigation-buttons {
  display: flex;
  gap: 20px;
  align-items: center;
}
#home-banner button, .nav-button {
  color: white;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 20px;
  background: green;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}
#home-banner button:hover, .nav-button:hover {
  background: darkgreen;
}
.nav-button {
  background: blue;
}
.nav-button:hover {
  background: darkblue;
}
</style>