<script setup>
import {ref, onMounted, computed, onBeforeUnmount, watch} from 'vue';
import { useRoute } from 'vue-router';
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import {AdminUserService} from '@/services/adminUserService';
import AdminSidePanel from '../AdminSidePanel.vue';
import UserCard from './UserCard.vue';
import ConfirmModal from '@/components/common/ConfirmModal.vue'

const route = useRoute();
const users = ref([]);
const loading = ref(false);
const error = ref(null);
const activePanel = ref('users');
const filterQuery = ref('');

// delete confirm modal state
const showConfirm = ref(false);
const pendingDeleteId = ref(null);

function onRequestDelete(id) {
  pendingDeleteId.value = id;
  showConfirm.value = true;
}

async function confirmDelete() {
  try {
    await AdminUserService.delete(pendingDeleteId.value);
    users.value = users.value.filter(u => String(u.id) !== String(pendingDeleteId.value));
    showConfirm.value = false;
    pendingDeleteId.value = null;
  } catch (e) {
    alert('Verwijderen mislukt: ' + (e?.response?.data?.message || e.message));
  }
}

function cancelDelete() {
  showConfirm.value = false;
  pendingDeleteId.value = null;
}

// set active based on current route path
function setActiveFromRoute() {
  const p = (route.path || '').toLowerCase();
  if (p.startsWith('/admin/forums')) activePanel.value = 'forums';
  else activePanel.value = 'users';
}

// computed filtered list based on search query: checks username, email and role keywords
const filteredUsers = computed(() => {
  const q = (filterQuery.value || '').toLowerCase();
  if (!q) return users.value || [];

  // map common Dutch role words to backend role names
  const roleMap = {
    'beheerder': 'ADMIN',
    'admin': 'ADMIN',
    'gebruiker': 'USER',
    'user': 'USER'
  };

  // if the query exactly matches a role keyword, filter by role
  if (roleMap[q]) {
    const wanted = roleMap[q];
    return users.value.filter(u => Array.isArray(u.roles) && u.roles.includes(wanted));
  }

  // otherwise do substring match on username/email and also check roles for partial matches
  return (users.value || []).filter(u => {
    const username = (u.username || '').toLowerCase();
    const email = (u.email || '').toLowerCase();
    const roles = (Array.isArray(u.roles) ? u.roles.join(' ').toLowerCase() : '');
    return username.includes(q) || email.includes(q) || roles.includes(q);
  })
});

// pagination state
const currentPage = ref(1);
const pageSize = ref(12);

// derived pagination values
const totalPages = computed(() => {
  const total = filteredUsers.value.length;
  return total ? Math.ceil(total / pageSize.value) : 1;
});
const pageStart = computed(() => filteredUsers.value.length ? (currentPage.value - 1) * pageSize.value + 1 : 0);
const pageEnd = computed(() => Math.min(filteredUsers.value.length, currentPage.value * pageSize.value));
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredUsers.value.slice(start, end);
});

// keep current page valid when data or filters change
watch([filteredUsers, pageSize], () => {
  const max = totalPages.value || 1;
  if (currentPage.value > max) currentPage.value = max;
  if (currentPage.value < 1) currentPage.value = 1;
});

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}
function prevPage() {
  if (currentPage.value > 1) currentPage.value--;
}

async function loadUsers() {
  loading.value = true;
  error.value = null;
  try {
    users.value = await AdminUserService.list();
  } catch (e) {
    error.value = e?.response?.data?.message || e.message;
  } finally {
    loading.value = false;
  }
}

function onNavigate(to) {
  activePanel.value = to;
  // Future: load other data when switching to forums, etc.
}

function onSearch(q) {
  filterQuery.value = (q || '').trim();
}

function onBack() {
  // Side panel already navigates to /home; keep this in case we want to do extra cleanup later
}

onMounted(() => {
  loadUsers();
  setActiveFromRoute();
});

// Listen to admin-search/admin-back dispatched from top navbar
function handleGlobalAdminSearch(e) {
  try {
    const q = e?.detail ?? '';
    onSearch(q);
  } catch (err) {
    console.error('admin-search handler error', err);
  }
}

function handleGlobalAdminBack() {
  onBack();
}

onMounted(() => {
  window.addEventListener('admin-search', handleGlobalAdminSearch);
  window.addEventListener('admin-back', handleGlobalAdminBack);
});

onBeforeUnmount(() => {
  window.removeEventListener('admin-search', handleGlobalAdminSearch);
  window.removeEventListener('admin-back', handleGlobalAdminBack);
});

// watch route changes to update the active panel highlight
watch(() => route.path, () => {
  setActiveFromRoute();
});
</script>

<template>
  <layout-component :admin-page="true">
    <main class="w-full min-h-screen bg-blue-50 !flex">
      <AdminSidePanel :active="activePanel" @navigate="onNavigate" @search="onSearch" @back="onBack"/>
      <div class="spacer-div md:w-80 h-screen"></div>
      <div class="admin-body-container flex justify-center w-full">
        <div class="users-container flex flex-wrap items-start content-start !mt-10 w-[640px] xl:w-[960px]">
          <h1 class="w-full !font-bold text-xl mb-2">Gebruikers</h1>

          <!-- top pagination bar -->
          <div class="w-full mb-4 flex items-center justify-between" v-if="filteredUsers.length">
            <div class="text-sm text-gray-600">Tonen {{ pageStart }}–{{ pageEnd }} van {{ filteredUsers.length }}</div>
            <div class="flex items-center gap-2">
              <label class="text-sm text-gray-600">Per pagina</label>
              <select v-model.number="pageSize" class="border rounded p-1 text-sm">
                <option :value="10">10</option>
                <option :value="12">12</option>
                <option :value="20">20</option>
                <option :value="50">50</option>
              </select>
            </div>
          </div>

          <div v-if="loading" class="text-gray-600">Laden...</div>
          <div v-else-if="error" class="text-red-600">Fout: {{ error }}</div>
          <div v-else-if="!filteredUsers.length" class="text-gray-600">Geen gebruikers gevonden.</div>

          <UserCard v-for="user in paginatedUsers" :key="user.id" :user="user" @delete="onRequestDelete"/>

          <!-- bottom pager -->
          <div v-if="filteredUsers.length" class="w-full mt-4 flex items-center justify-between">
            <button @click="prevPage" :disabled="currentPage === 1" class="px-3 py-2 rounded border"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }">Vorige</button>
            <div class="text-sm text-gray-600">Pagina {{ currentPage }} van {{ totalPages }}</div>
            <button @click="nextPage" :disabled="currentPage === totalPages || !filteredUsers.length" class="px-3 py-2 rounded border"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages || !filteredUsers.length }">Volgende</button>
          </div>
        </div>
      </div>

      <!-- Confirm delete modal -->
      <ConfirmModal
        :open="showConfirm"
        title="Gebruiker verwijderen"
        message="Weet je zeker dat je deze gebruiker wilt verwijderen? Dit kan niet ongedaan worden gemaakt."
        confirmText="Verwijderen"
        cancelText="Annuleren"
        @confirm="confirmDelete"
        @cancel="cancelDelete"
      />
    </main>
  </layout-component>
</template>

<style>

@media (max-width: 980px) {
  .users-container {
    width: 300px;
  }
}

</style>