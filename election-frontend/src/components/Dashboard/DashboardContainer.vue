<template>
  <section class="bg-gray-50 py-16">
    <div class="max-w-7xl mx-auto px-6 lg:px-8">
      <!-- Dashboard Header -->
      <div class="text-center mb-12">
        <h2 class="text-3xl lg:text-4xl font-bold text-gray-900 mb-4">
          Jouw Politieke Dashboard 📊
        </h2>
        
        <!-- Tab Navigation -->
        <div class="tab-container">
          <button
            @click="activeTab = 'inzichten'"
            :class="['tab-button', { 'active': activeTab === 'inzichten' }]"
          >
            <!-- Globe Icon -->
            <svg class="tab-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"></path>
            </svg>
            <span class="tab-text">Inzichten</span>
          </button>

          <button
            @click="activeTab = 'community'"
            :class="['tab-button', { 'active': activeTab === 'community' }]"
          >
            <!-- MessageCircle Icon -->
            <svg class="tab-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
            </svg>
            <span class="tab-text">Community</span>
          </button>

          <button
            @click="activeTab = 'leren'"
            :class="['tab-button', { 'active': activeTab === 'leren' }]"
          >
            <!-- BookOpen Icon -->
            <svg class="tab-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
            </svg>
            <span class="tab-text">Leren</span>
          </button>
        </div>
      </div>

      <!-- Tab Content -->
      <div class="bg-white rounded-2xl shadow-lg overflow-hidden min-h-[400px]">
        <!-- Content displayed here based on activeTab -->
        <div v-if="activeTab === 'inzichten'" style="padding: 1.5rem; height: 82%;">
          <InzichtenTab />
        </div>
        
        <div v-else-if="activeTab === 'community'" style="padding: 1.5rem;">
          <div class="text-center py-12">
            <CommunityTab />
          </div>
        </div>
        
        <div v-else-if="activeTab === 'leren'" style="padding: 2rem;">
          <LerenTab />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
//TODO Import tab components
//import InsightsTab from './InsightsTab.vue'
import CommunityTab from './CommunityTab.vue'
import LerenTab from './LerenTab.vue'
import InzichtenTab from "@/components/Dashboard/InzichtenTab.vue";

// Reactive data for active tab
const activeTab = ref('inzichten') // Start with insights tab
</script>

<style scoped>
/* Custom styling for tab transitions */
.tab-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Tab Navigation Styles */
.tab-container {
  display: flex;
  justify-content: space-between;
  background: #E8E8E8;
  border-radius: 30px;
  padding: 4px 20px;
  margin-bottom: 32px;
  margin-left: auto;
  margin-right: auto;
  width: 600px;
  max-width: 90%;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 26px;
  transition: all 0.2s ease;
  font-size: 14px;
  border: none;
  background: none;
  cursor: pointer;
  color: #666;
  min-height: 40px;
  white-space: nowrap;
}

.tab-button.active {
  background: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  color: #111;
}

.tab-button:hover:not(.active) {
  color: #333;
}

.tab-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.tab-text {
  font-weight: 500;
}
</style>