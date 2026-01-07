<template>
  <layout-component :admin-page="true">
    <main class="w-full min-h-screen bg-blue-50 !flex">
      <AdminSidePanel :active="activePanel" @navigate="onNavigate" @search="onSearch" @back="onBack"/>
      <div class="spacer-div md:w-80 h-screen"></div>
      <div class="admin-body-container flex justify-center w-full">
        <div class="posts-container flex flex-wrap items-start content-start !mt-10 w-[640px] xl:w-[960px]">
          <h1 class="w-full !font-bold text-xl mb-2">Forum posts</h1>

          <!-- top pagination / controls -->
          <div class="w-full mb-4 flex items-center justify-between" v-if="totalElements">
            <div class="text-sm text-gray-600">Tonen {{ pageStart }}–{{ pageEnd }} van {{ totalElements }}</div>
            <div class="flex items-center gap-2">
              <label class="text-sm text-gray-600">Per pagina</label>
              <select v-model.number="pageSize" class="border rounded p-1 text-sm" @change="onPageSizeChange">
                <option :value="6">6</option>
                <option :value="12">12</option>
                <option :value="20">20</option>
              </select>
            </div>
          </div>

          <div v-if="loading" class="text-gray-600">Laden...</div>
          <div v-else-if="error" class="text-red-600">Fout: {{ error }}</div>
          <div v-else-if="!posts.length" class="text-gray-600">Geen posts gevonden.</div>

          <!-- Post cards using component -->
          <PostCard
            v-for="post in posts"
            :key="post.id"
            :post="post"
            :truncate-at="220"
            :show-delete="true"
            @open="openDetail"
            @delete="onDelete"
          >
            <template #delete-icon>
              <IconTrash class="w-4 h-4" />
            </template>
          </PostCard>

          <!-- bottom pager -->
          <div v-if="totalPages > 1" class="w-full mt-4 flex items-center justify-between">
            <button @click="prevPage" :disabled="currentPage === 1" class="px-3 py-2 rounded border"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }">Vorige</button>
            <div class="text-sm text-gray-600">Pagina {{ currentPage }} van {{ totalPages }}</div>
            <button @click="nextPage" :disabled="currentPage === totalPages" class="px-3 py-2 rounded border"
                    :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }">Volgende</button>
          </div>
        </div>
      </div>
    </main>

    <!-- Confirm delete modal -->
    <ConfirmModal
      :open="showConfirm"
      title="Post verwijderen"
      message="Weet je zeker dat je deze post wilt verwijderen? Dit kan niet ongedaan worden gemaakt."
      confirmText="Verwijderen"
      cancelText="Annuleren"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </layout-component>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import LayoutComponent from '@/components/LayoutComponent/LayoutComponent.vue'
import AdminSidePanel from '@/components/admin/AdminSidePanel.vue'
import PostCard from '@/components/admin/Forums/PostCard.vue'
import { getPosts, deletePost } from '@/services/usePostsService'
import IconTrash from "@/components/icons/IconTrash.vue";
import ConfirmModal from '@/components/common/ConfirmModal.vue'

const route = useRoute();
const router = useRouter();

// server-driven pagination state
const posts = ref([]);
const loading = ref(false);
const error = ref(null);
const totalElements = ref(0);
const totalPages = ref(1);
// UI shows 1-based page index, server expects 0-based
const currentPage = ref(1);
const pageSize = ref(12);

const activePanel = ref('forums');
const filterQuery = ref(''); // client-side filter (optional)

function setActiveFromRoute() {
  const p = (route.path || '').toLowerCase();
  if (p.startsWith('/admin/forums') || p.startsWith('/beheerder/forums')) activePanel.value = 'forums';
  else activePanel.value = 'users';
}

function computePageBounds() {
  const backendPage = Math.max(0, currentPage.value - 1);
  const start = totalElements.value ? backendPage * pageSize.value + 1 : 0;
  const end = totalElements.value ? Math.min(totalElements.value, backendPage * pageSize.value + posts.value.length) : 0;
  return { start, end };
}

const pageStart = computed(() => computePageBounds().start);
const pageEnd = computed(() => computePageBounds().end);

async function loadPosts() {
  loading.value = true;
  error.value = null;
  try {
    const backendPage = Math.max(0, currentPage.value - 1);
    const data = await getPosts(backendPage, pageSize.value);
    // data is a Spring Page<Post>
    const raw = Array.isArray(data?.content) ? data.content : [];
    const q = (filterQuery.value || '').toLowerCase();
    posts.value = q
      ? raw.filter(p => {
          const title = (p?.title || '').toLowerCase();
          const content = (p?.content || '').toLowerCase();
          const author = (p?.author?.username || '').toLowerCase();
          return title.includes(q) || content.includes(q) || author.includes(q);
        })
      : raw;

    totalElements.value = Number.isFinite(data?.totalElements) ? data.totalElements : raw.length;
    totalPages.value = Number.isFinite(data?.totalPages) ? data.totalPages : 1;
  } catch (e) {
    error.value = e?.message || String(e);
    posts.value = [];
    totalElements.value = 0;
    totalPages.value = 1;
  } finally {
    loading.value = false;
  }
}

// Modal state for delete confirmation
const showConfirm = ref(false);
const pendingDeleteId = ref(null);

async function onDelete(id) {
  pendingDeleteId.value = id;
  showConfirm.value = true;
}

async function confirmDelete() {
  try {
    const wasLastOnPage = posts.value.length === 1 && currentPage.value > 1;
    if (wasLastOnPage) currentPage.value = currentPage.value - 1;

    await deletePost(pendingDeleteId.value);
    showConfirm.value = false;
    pendingDeleteId.value = null;
    await loadPosts();
  } catch (e) {
    alert('Verwijderen mislukt: ' + (e?.response?.data?.message || e.message || e));
  }
}

function cancelDelete() {
  showConfirm.value = false;
  pendingDeleteId.value = null;
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    loadPosts();
  }
}
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    loadPosts();
  }
}
function onPageSizeChange() {
  currentPage.value = 1;
  loadPosts();
}

function openDetail(id) {
  router.push(`/detail/${id}`);
}

function onNavigate(to) {
  activePanel.value = to;
}

function onSearch(q) {
  filterQuery.value = (q || '').trim();
  loadPosts();
}

function onBack() {
  if (window.history.length > 1) router.back();
  else router.push('/home');
}

onMounted(() => {
  loadPosts();
  setActiveFromRoute();
  window.addEventListener('admin-search', handleGlobalAdminSearch);
  window.addEventListener('admin-back', handleGlobalAdminBack);
});

onBeforeUnmount(() => {
  window.removeEventListener('admin-search', handleGlobalAdminSearch);
  window.removeEventListener('admin-back', handleGlobalAdminBack);
});

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

watch(() => route.path, () => {
  setActiveFromRoute();
});
</script>

<style scoped>
/* small spacing tweak so it looks centered when inside layout */
main {
  padding: 2rem 0;
}

body {
  background: #eff6ff;
}

.posts-container .post-title {
  font-weight: 700;
}

@media (max-width: 980px) {
  .posts-container {
    width: 300px;
  }
}
</style>
