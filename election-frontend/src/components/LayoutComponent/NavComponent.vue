<script setup>
import {useRoute, useRouter} from 'vue-router';
import {ref, computed, onMounted, onBeforeUnmount, nextTick} from 'vue';
import {isLoggedIn, getUser, clearAuth, getToken} from '@/utils/auth';
import IconLogin from "@/components/icons/IconLogin.vue";
import IconRegister from "@/components/icons/IconRegister.vue";
import IconHome from "@/components/icons/IconHome.vue";
import IconDocumentation from "@/components/icons/IconDocumentation.vue";
import IconBook from "@/components/icons/IconBook.vue";
import NavLinkComponent from "@/components/LayoutComponent/NavLinkComponent.vue";
import IconCommunity from "@/components/icons/IconCommunity.vue";
import IconProfile from "@/components/icons/IconProfile.vue";
import IconCompare from "@/components/icons/IconCompare.vue";
import {ProfileService} from "@/services/profileService.js";
import QuizPage from "@/components/Quiz/QuizPage.vue";
import IconQuestion from "@/components/icons/IconQuestion.vue";
import IconNews from "@/components/icons/IconNews.vue";

// accept an optional prop from LayoutComponent to explicitly enable admin mode
const props = defineProps({ isAdminPage: { type: Boolean, default: undefined } });

// make navLinks reactive
const navLinks = ref([
  {icon: IconHome, label: "Home", to: "/home", disabled: false},
  {icon: IconBook, label: "Hoe werkt het?", to: "/leer", disabled: false},
  {icon: IconCommunity, label: "Forum", to: "/forum", disabled: false},
  {icon: IconDocumentation, label: "Verkiezingsresultaten", to: "/results", disabled: false},
  {icon: IconCompare, label: "Vergelijk", to: "/vergelijk", disabled: false},
  {icon: IconLogin, label: "Inloggen", to: "/inloggen", disabled: false},
  {icon: IconQuestion, label: "Quiz", to: "/quiz", disabled: false },
  {icon: IconNews, label: "Nieuws", to: "/nieuws", disabled: false},
]);

const checkAdminStatus = async () => {
  try {
    const token = getToken();
    if (!token) return; // Don't call API if no token
    
    const profile = await ProfileService.getProfile(token);
    const isAdmin = !!profile?.roles?.[0] && profile.roles[0] === 'ADMIN';
    const exists = navLinks.value.some(l => l.label === 'Beheerder');

    if (isAdmin && !exists) {
      navLinks.value.push({icon: IconRegister, label: "Beheerder", to: "/beheerder", disabled: false});
    } else if (!isAdmin && exists) {
      navLinks.value = navLinks.value.filter(l => l.label !== 'Beheerder');
    }
  } catch (e) {
    console.error("Error checking admin status:", e);
  }
}


const route = useRoute();
const router = useRouter();
// final admin flag: prefer explicit prop, otherwise fall back to route.meta.adminPage
const finalIsAdmin = computed(() => (typeof props.isAdminPage === 'boolean') ? props.isAdminPage : Boolean(route.meta?.adminPage));
const isAuth = ref(isLoggedIn());
const user = ref(getUser());

const displayUsername = computed(() => {
  if (!user.value?.username) return '';
  const maxLength = 12;
  return user.value.username.length > maxLength
      ? user.value.username.substring(0, maxLength) + '...'
      : user.value.username;
});

const isDropdownOpen = ref(false);
const profileButton = ref(null);

function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value;
  if (isDropdownOpen.value) {
    nextTick(() => {
      const button = profileButton.value;
      const dropdown = document.querySelector('.dropdown-menu');
      if (button && dropdown) {
        const rect = button.getBoundingClientRect();
        dropdown.style.position = 'fixed';
        dropdown.style.top = `${rect.bottom + 8}px`;
        dropdown.style.right = `${window.innerWidth - rect.right}px`;
      }
    });
  }
}

function closeDropdown(event) {
  const dropdown = document.querySelector('.dropdown-menu');
  const button = profileButton.value;
  if (dropdown && !dropdown.contains(event.target) &&
      button && !button.contains(event.target)) {
    isDropdownOpen.value = false;
  }
}

function syncAuth() {
  isAuth.value = isLoggedIn();
  user.value = getUser();
  if (isAuth.value) checkAdminStatus(); // re-check admin when auth changes
}

onMounted(() => {
  checkAdminStatus();
  window.addEventListener('auth-changed', syncAuth);
  window.addEventListener('click', closeDropdown);
  syncAuth();
});
onBeforeUnmount(() => {
  window.removeEventListener('auth-changed', syncAuth);
  window.removeEventListener('click', closeDropdown);
});

function logout() {
  clearAuth();
  syncAuth();
  isDropdownOpen.value = false;
  router.push('/inloggen');
}

function goToProfile() {
  isDropdownOpen.value = false;
  router.push('/profiel');
}

// use reactive navLinks inside computed
const visibleLinks = computed(() => {
  // If this is an admin page, hide all nav links (we still show the profile button separately)
  if (finalIsAdmin.value) return [];
  if (!isAuth.value) return navLinks.value;
  return navLinks.value.filter(l => l.label !== 'Inloggen');
});

// --- Admin top-nav search/back wiring ---
const adminSearchQuery = ref('');
let adminSearchTimeout = null;

// dynamic placeholder depending on current admin section
const adminPlaceholder = computed(() => {
  const p = (route.path || '').toLowerCase();
  if (p.startsWith('/admin/forums') || p.startsWith('/beheerder/forums')) {
    return 'Zoek post, inhoud of auteur';
  }
  return 'Zoek gebruiker of rol (beheerder / gebruiker)';
});

function emitAdminSearch(q) {
  // debounce few ms to avoid spamming on fast typing
  if (adminSearchTimeout) clearTimeout(adminSearchTimeout);
  adminSearchTimeout = setTimeout(() => {
    window.dispatchEvent(new CustomEvent('admin-search', {detail: q}));
  }, 150);
}

function onAdminInput(e) {
  adminSearchQuery.value = e.target.value;
  emitAdminSearch(adminSearchQuery.value.trim());
}

function onAdminBack() {
  window.dispatchEvent(new Event('admin-back'));
  router.push('/home');
}
</script>

<template>
  <nav :class="{ 'admin-page': finalIsAdmin }">
    <div class="logo">
      <span class="logo-icon">NL</span>
      <span class="logo-name">StemWijs</span>
    </div>

    <!-- When on admin page show a compact search + back button in the top navbar -->
    <div v-if="finalIsAdmin" class="admin-top-controls !ml-40"
         style="margin-left:12px; display:flex; gap:8px; align-items:center;">
      <input :value="adminSearchQuery" @input="onAdminInput" :placeholder="adminPlaceholder"
             style="padding:8px 10px; border-radius:8px; border:1px solid #e5e7eb;"/>
      <NavLinkComponent
          :icon="IconHome"
          label="Terug naar site"
          to="/home"
          :selected="false"
          :disabled="false"
          @click="onAdminBack"
      />
    </div>

    <NavLinkComponent
        v-for="nav in visibleLinks"
        :key="nav.to"
        :icon="nav.icon"
        :label="nav.label"
        :to="nav.to"
        :selected="route.path === nav.to"
        :disabled="nav.disabled"
    />

    <!--  profile dropdown (only when logged in) -->
    <div v-if="isAuth" class="profile-dropdown">
      <button class="profile-button" @click.stop="toggleDropdown" ref="profileButton">
        <!-- Show profile image if available, otherwise show icon -->
        <img
            v-if="user?.profileImageUrl"
            :src="user.profileImageUrl"
            alt="Profile"
            class="profile-avatar"
        />
        <IconProfile v-else/>
        <span class="profile-name" v-if="displayUsername">{{ displayUsername }}</span>
      </button>
    </div>
  </nav>

  <!-- Dropdown menu outside nav to prevent overflow clipping -->
  <Teleport to="body">
    <div v-if="isAuth && isDropdownOpen" class="dropdown-menu">
      <button @click="goToProfile" class="dropdown-item">
        <IconProfile/>
        <span>Mijn Profiel</span>
      </button>
      <button @click="logout" class="dropdown-item logout">
        <span>Log uit</span>
      </button>
    </div>
  </Teleport>
</template>
<style>
nav {
  z-index: 10;
  background: white;
  position: fixed;
  width: 100%;
  top: 0;
  left: 0;
  font-family: "Segoe UI", sans-serif, Arial;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  height: 65px;
  border-bottom: 2px solid #dbdbdb;
  padding: 0 20px;
  gap: 8px;
  overflow-x: auto;
  overflow-y: visible;
}

/* Hide scrollbar but keep functionality */
nav::-webkit-scrollbar {
  display: none;
}

nav {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.logo {
  margin-right: 12px;
  flex-shrink: 0;
}

/* Profile dropdown styles */
.profile-dropdown {
  position: relative;
  margin-left: auto;
  padding-right: 0;
  flex-shrink: 0;
  z-index: 1001;
}

.profile-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  padding: 8px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.profile-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid #e5e7eb;
}

.profile-button:hover {
  background: #e5e7eb;
  border-color: #d1d5db;
}

.profile-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  display: none;
}

@media (min-width: 640px) {
  .profile-name {
    display: inline;
  }
}

/* Dropdown menu (teleported to body, positioned via JS) */
.dropdown-menu {
  position: fixed;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  min-width: 180px;
  overflow: hidden;
  z-index: 10000;
}

.dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: white;
  border: 0;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background 0.2s;
  text-align: left;
}

.dropdown-item:hover {
  background: #f3f4f6;
}

.dropdown-item.logout {
  color: #ef4444;
  border-top: 1px solid #e5e7eb;
}

.dropdown-item.logout:hover {
  background: #fef2f2;
}

/* When showing an admin page we want a minimal chrome: no bottom border or shadow */
.admin-page {
  border-bottom: none !important;
  box-shadow: none !important;
  background: #fff;
}

/* small top-admin controls to the left of nav items */
.admin-top-controls input {
  min-width: 260px;
}
</style>