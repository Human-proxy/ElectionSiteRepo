<template>
  <div class="leer-voortgang-card">
    <div class="card-header">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="book-icon">
        <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
      </svg>
      <h3>Leer Voortgang</h3>
    </div>

    <div class="card-content">
      <!-- Progress Bar -->
      <div class="progress-section">
        <div class="progress-header">
          <span>Platform Verkenning</span>
          <span class="progress-count">{{ pagesVisited }}/{{ totalPages }}</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
        </div>
      </div>

      <div class="divider"></div>

      <!-- Completed Sections -->
      <div class="section">
        <h4>Voltooide Secties</h4>
        <div v-if="completedSections.length === 0" class="empty-message">
          Nog geen secties voltooid
        </div>
        <div v-for="section in completedSections" :key="section" class="section-item completed">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <span>{{ section }}</span>
        </div>
      </div>

      <!-- Next Sections -->
      <div class="section">
  <h4 class="muted">Volgende secties</h4>
        <div v-if="inProgressSections.length === 0" class="empty-message muted">
          Geen secties in uitvoering
        </div>
        <div v-for="section in inProgressSections" :key="section" class="section-item in-progress">
          <div class="circle-icon"></div>
          <span>{{ section }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { ProfileService } from '@/services/profileService';
import { getToken, isLoggedIn } from '@/utils/auth';
import { getTrackableRoutes } from '@/router';

// Automatically get all trackable pages from the router
const allPages = getTrackableRoutes();

const visitedPages = ref(new Set());

// Load visited pages from backend
const loadVisitedPages = async () => {
  if (!isLoggedIn()) return;

  try {
    const token = getToken();
    const profile = await ProfileService.getProfile(token);
    if (profile.visitedPages) {
      visitedPages.value = new Set(profile.visitedPages);
    }
  } catch (error) {
    console.error('Error loading visited pages:', error);
  }
};

onMounted(() => {
  loadVisitedPages();
  
  // Listen for custom event when a page is visited
  window.addEventListener('page-visited', loadVisitedPages);
});

// Cleanup event listener on component unmount
onUnmounted(() => {
  window.removeEventListener('page-visited', loadVisitedPages);
});

// Computed properties
const totalPages = computed(() => allPages.length);
const pagesVisited = computed(() => visitedPages.value.size);
const progressPercentage = computed(() => 
  Math.round((pagesVisited.value / totalPages.value) * 100)
);

const completedSections = computed(() => {
  return allPages
    .filter(page => visitedPages.value.has(page.path))
    .map(page => page.label);
});

const inProgressSections = computed(() => {
  return allPages
    .filter(page => !visitedPages.value.has(page.path))
    .map(page => page.label)
  .slice(0, 2); // Show max 2 "next sections" items
});
</script>

<style scoped>

.leer-voortgang-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-width: 340px;
  min-width: 260px;
  width: 100%;
  padding: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.card-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
}

.book-icon {
  color: #6366f1;
  flex-shrink: 0;
}

.card-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* Progress Section */
.progress-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.875rem;
}

.progress-count {
  font-weight: 600;
  color: #111827;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 9999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 9999px;
  transition: width 0.3s ease;
}

.divider {
  height: 1px;
  background-color: #e5e7eb;
}

/* Sections */
.section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.section h4 {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: #111827;
}

.section h4.muted {
  color: #6b7280;
}

.section-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  padding: 0.25rem 0;
}

.section-item.completed {
  color: #10b981;
}

.section-item.completed svg {
  stroke: #10b981;
  flex-shrink: 0;
}

.section-item.in-progress {
  color: #6b7280;
}

.circle-icon {
  width: 16px;
  height: 16px;
  border: 2px solid #6366f1;
  border-radius: 50%;
  flex-shrink: 0;
}

.empty-message {
  font-size: 0.875rem;
  color: #9ca3af;
  font-style: italic;
}

.empty-message.muted {
  color: #d1d5db;
}
</style>
