<template>
  <LayoutComponent>
  <div class="profile-page">
    <!-- Header Section -->
    <section class="profile-header">
      <div class="header-content">
        <!-- Back button -->
        <button class="back-button" @click="goToHome">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m15 18-6-6 6-6"/>
          </svg>
          Terug naar Home
        </button>
        
        <!-- User info -->
        <div class="user-info">
          <!-- Avatar circle with initial or image -->
          <div class="avatar-wrapper">
            <div class="avatar" v-if="!user?.profileImageUrl">
              {{ userInitial }}
            </div>
            <img v-else :src="user.profileImageUrl" alt="Profile" class="avatar-image" />
            
            <!-- Loading overlay -->
            <div v-if="isUploading" class="upload-overlay">
              <div class="spinner"></div>
            </div>
            
            <!-- Edit button overlay -->
            <button class="edit-avatar-btn" @click="triggerFileInput" :disabled="isUploading" title="Wijzig profielfoto">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z"/>
                <path d="m15 5 4 4"/>
              </svg>
            </button>
            
            <!-- Hidden file input -->
            <input 
              ref="fileInput" 
              type="file" 
              accept="image/*" 
              style="display: none" 
              @change="handleFileUpload"
            />
          </div>
          
          <!-- Welcome text -->
          <div class="welcome-section">
            <h1 class="welcome-title">
              Welkom, {{ user?.username || 'Gebruiker' }}! 👋
            </h1>
            <p class="welcome-subtitle">
              Beheer je account en bekijk je voortgang op StemWijs
            </p>
            
            <!-- Badges -->
            <div class="badges">
              <div class="badge">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
                  <line x1="16" x2="16" y1="2" y2="6"/>
                  <line x1="8" x2="8" y1="2" y2="6"/>
                  <line x1="3" x2="21" y1="10" y2="10"/>
                </svg>
                Lid sinds {{ memberSince }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Content Section -->
    <section class="profile-content">
      <div class="content-container">
        <div class="profile-grid">
          <!-- Left Column: Learning Progress + Account Management -->
          <div class="left-column">
            <LeerVoortgang />
            <AccountManagement />
          </div>
          
          <!-- Right Column: Profile Information + Change Password -->
          <div class="right-column">
            <ProfileInformation />
            <WachtwoordWijzigen />
          </div>
        </div>
      </div>
    </section>
    
    <!-- Toast Notifications -->
    <div v-if="toast.show" :class="['toast', toast.type]">
      <svg v-if="toast.type === 'success'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
        <polyline points="22 4 12 14.01 9 11.01"/>
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="8" x2="12" y2="12"/>
        <line x1="12" y1="16" x2="12.01" y2="16"/>
      </svg>
      <span>{{ toast.message }}</span>
    </div>
  </div>
    </LayoutComponent>
</template>

<script setup>
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import LeerVoortgang from "@/components/Profile/LearningProgress.vue";
import AccountManagement from "@/components/Profile/AccountManagement.vue";
import ProfileInformation from "@/components/Profile/ProfileInformation.vue";
import WachtwoordWijzigen from "@/components/Profile/ChangePassword.vue";
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getUser, setAuth, getToken } from '@/utils/auth';
import { ProfileService } from '@/services/profileService';

const router = useRouter();
const user = ref(getUser());
const fileInput = ref(null);
const isUploading = ref(false);
const toast = ref({ show: false, message: '', type: 'success' });

// Get user's initials (first letter of username)
const userInitial = computed(() => {
  if (!user.value?.username) return 'U';
  return user.value.username.charAt(0).toUpperCase();
});

// Get member since date (from user's createdAt)
const memberSince = computed(() => {
  if (!user.value?.createdAt) return 'Onbekend';
  
  const date = new Date(user.value.createdAt);
  const months = ['januari', 'februari', 'maart', 'april', 'mei', 'juni', 
                  'juli', 'augustus', 'september', 'oktober', 'november', 'december'];
  
  const month = months[date.getMonth()];
  const year = date.getFullYear();
  
  return `${month} ${year}`;
});

function goToHome() {
  router.push('/home');
}

function triggerFileInput() {
  fileInput.value?.click();
}

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type };
  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
}

async function handleFileUpload(event) {
  const file = event.target.files?.[0];
  if (!file) return;

  // Validate file type
  if (!file.type.startsWith('image/')) {
    showToast('Alleen afbeeldingen zijn toegestaan', 'error');
    return;
  }

  // Validate file size (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    showToast('Afbeelding is te groot. Maximaal 5MB toegestaan', 'error');
    return;
  }

  isUploading.value = true;

  try {
    // Compress image using ProfileService
    const compressedBase64 = await ProfileService.compressImage(file);

    // Update profile image via ProfileService
    const updatedUser = await ProfileService.updateProfileImage(getToken(), compressedBase64);

    // Update local user object
    user.value = updatedUser;
    
    // Update localStorage and trigger auth-changed event
    setAuth({ token: getToken(), user: updatedUser });

    showToast('Profielfoto succesvol bijgewerkt!', 'success');
  } catch (error) {
    console.error('Fout bij uploaden profielfoto:', error);
    showToast('Er is een fout opgetreden bij het uploaden', 'error');
  } finally {
    isUploading.value = false;
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f9fafb;
}

/* Header Section */
.profile-header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #f97316 100%);
  color: white;
  padding: 3rem 0;
  width: 100%;
}

.header-content {
  max-width: 1280px;
  margin: 0;
  padding: 0 1.5rem;
  width: 100%;
  box-sizing: border-box;
}

/* Back button */
.back-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  margin-bottom: 1.5rem;
  transition: background 0.2s;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* User info section */
.user-info {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

/* Avatar wrapper with edit button */
.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

/* Avatar */
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f97316 0%, #8b5cf6 100%);
  border: 4px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  color: white;
}

/* Avatar image (when profile photo is uploaded) */
.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.3);
  object-fit: cover;
}

/* Edit avatar button */
.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: white;
  border: 2px solid #8b5cf6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.edit-avatar-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.edit-avatar-btn:hover:not(:disabled) {
  background: #8b5cf6;
  transform: scale(1.1);
}

.edit-avatar-btn:hover:not(:disabled) svg {
  stroke: white;
}

.edit-avatar-btn svg {
  stroke: #8b5cf6;
  transition: stroke 0.2s;
}

/* Upload overlay */
.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

/* Spinner animation */
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Welcome section */
.welcome-section {
  flex: 1;
}

.welcome-title {
  font-size: 2rem;
  margin: 0 0 0.5rem 0;
  font-weight: 700;
}

.welcome-subtitle {
  font-size: 1.125rem;
  margin: 0 0 0.75rem 0;
  color: rgba(255, 255, 255, 0.9);
}

/* Badges */
.badges {
  display: flex;
  gap: 1rem;
  margin-top: 0.75rem;
}

.badge {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 0.375rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  color: white;
}

/* Content Section */
.profile-content {
  padding: 2rem 0;
  width: 100%;
}

.content-container {
  max-width: 1280px;
  margin: 0;
  padding: 0 1.5rem;
  width: 100%;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1.5rem;
}

/* Responsive */
@media (max-width: 768px) {
  .user-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .avatar-wrapper {
    width: 64px;
    height: 64px;
  }
  
  .avatar,
  .avatar-image {
    width: 64px;
    height: 64px;
    font-size: 1.5rem;
  }
  
  .edit-avatar-btn {
    width: 28px;
    height: 28px;
  }
  
  .edit-avatar-btn svg {
    width: 14px;
    height: 14px;
  }
}

/* Toast Notification */
.toast {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  font-weight: 500;
  z-index: 10000;
  animation: slideIn 0.3s ease-out;
}

.toast.success {
  background: white;
  color: #10b981;
  border: 2px solid #10b981;
}

.toast.success svg {
  stroke: #10b981;
}

.toast.error {
  background: white;
  color: #ef4444;
  border: 2px solid #ef4444;
}

.toast.error svg {
  stroke: #ef4444;
}

@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* Profile Grid */
.profile-grid {
  display: grid;
  grid-template-columns: minmax(360px, 480px) minmax(480px, 760px);
  gap: 24px;
  align-items: start;
  max-width: 1400px;
  margin: 0 auto;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
  width: 100%;
}

/* Responsive: stack on smaller screens */
@media (max-width: 1024px) {
  .profile-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .toast {
    bottom: 1rem;
    right: 1rem;
    left: 1rem;
    font-size: 0.9rem;
  }
  
  .two-column-layout {
    gap: 16px;
  }
}
</style>
