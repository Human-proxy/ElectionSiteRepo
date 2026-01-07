<template>
  <layout-component>
    <div class="page-container">
      <div class="card-container fade-in">
        <!-- Header with Warning Icon -->
        <div class="card-header">
          <div class="icon-wrapper pulse-animation">
            <svg xmlns="http://www.w3.org/2000/svg" class="warning-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h2 class="title">Account Gedeactiveerd</h2>
          <p class="subtitle">Welkom terug, <span class="username">{{ username }}</span></p>
        </div>

        <!-- Content -->
        <div class="card-body">
          <!-- Success Overlay -->
          <div v-if="showSuccess" class="success-overlay">
            <div class="success-checkmark">
              <svg class="checkmark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
                <circle class="checkmark-circle" cx="26" cy="26" r="25" fill="#16a34a"/>
                <path class="checkmark-check" fill="none" stroke="white" stroke-width="3" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
              </svg>
            </div>
            <h3 class="success-title">Gelukt!</h3>
            <p class="success-message">Je account is geactiveerd.<br>Je wordt nu doorgestuurd naar de inlogpagina...</p>
          </div>

          <div v-else class="info-section">
            <div v-if="errorMessage" class="error-alert fade-in">
              <svg xmlns="http://www.w3.org/2000/svg" class="error-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <p class="info-text">
              Je account staat op de planning om permanent verwijderd te worden.
              Je hebt nog <span class="days-highlight">{{ daysRemaining }} dagen</span> om je account te herstellen.
            </p>
            
            <div class="date-box">
              <p class="date-label">Geplande verwijderdatum</p>
              <p class="date-value">{{ deletionDateFormatted }}</p>
            </div>
          </div>

          <!-- Actions -->
          <div v-if="!showSuccess" class="actions">
            <button 
              @click="handleReactivate" 
              :disabled="isLoading"
              class="btn btn-primary"
            >
              <svg v-if="!isLoading" xmlns="http://www.w3.org/2000/svg" class="btn-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span v-if="isLoading" class="spinner"></span>
              <span v-else>Account Activeren</span>
            </button>

            <button 
              @click="handleLogout" 
              :disabled="isLoading"
              class="btn btn-secondary"
            >
              Annuleren & Uitloggen
            </button>
          </div>
        </div>
      </div>
    </div>
  </layout-component>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { AuthService } from '@/services/authService';
import { getUser, clearAuth, setAuth, getToken } from '@/utils/auth';
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";

const router = useRouter();
const isLoading = ref(false);
const showSuccess = ref(false);
const errorMessage = ref('');
const user = ref(null);

onMounted(() => {
  const currentUser = getUser();
  if (!currentUser || !currentUser.deletedAt) {
    router.push('/');
    return;
  }
  user.value = currentUser;
});

const username = computed(() => user.value?.username || 'Gebruiker');

const deletionDate = computed(() => {
  if (!user.value?.deletedAt) return new Date();
  const deletedAt = new Date(user.value.deletedAt);
  const deletionDate = new Date(deletedAt);
  deletionDate.setDate(deletedAt.getDate() + 30);
  return deletionDate;
});

const deletionDateFormatted = computed(() => {
  return deletionDate.value.toLocaleDateString('nl-NL', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
});

const daysRemaining = computed(() => {
  const now = new Date();
  const diffTime = Math.abs(deletionDate.value - now);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
  return Math.max(0, diffDays);
});

const handleReactivate = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    await AuthService.reactivateAccount();
    
    // Show success animation
    showSuccess.value = true;
    
    // Wait for animation then redirect to login
    setTimeout(() => {
      clearAuth();
      router.push('/inloggen');
    }, 3000);
    
  } catch (error) {
    console.error('Reactivation failed:', error);
    errorMessage.value = 'Er is iets misgegaan bij het activeren van je account. Probeer het later opnieuw.';
    isLoading.value = false;
  }
};

const handleLogout = () => {
  clearAuth();
  router.push('/inloggen');
};
</script>

<style scoped>
.page-container {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background-color: #f9fafb;
}

.card-container {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: 1px solid #f3f4f6;
}

.card-header {
  background-color: #fffbeb;
  padding: 2.5rem 2rem;
  text-align: center;
  border-bottom: 1px solid #fef3c7;
}

.icon-wrapper {
  margin: 0 auto 1.5rem;
  width: 80px;
  height: 80px;
  background-color: #fef3c7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.warning-icon {
  height: 40px;
  width: 40px;
  color: #d97706;
}

.title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #4b5563;
  font-size: 1rem;
}

.username {
  font-weight: 600;
  color: #111827;
}

.card-body {
  padding: 2rem;
}

.info-section {
  margin-bottom: 2rem;
  text-align: center;
}

.info-text {
  color: #4b5563;
  margin-bottom: 1.5rem;
  line-height: 1.6;
  font-size: 1.05rem;
}

.days-highlight {
  font-weight: 700;
  color: #d97706;
  font-size: 1.1rem;
}

.date-box {
  background-color: #f9fafb;
  border-radius: 12px;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  display: inline-block;
  width: 100%;
}

.date-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
}

.date-value {
  font-weight: 600;
  color: #1f2937;
  font-size: 1rem;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn {
  width: 100%;
  padding: 0.875rem 1rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border: none;
}

.btn-primary {
  background-color: #4f46e5;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2);
}

.btn-primary:hover:not(:disabled) {
  background-color: #4338ca;
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(79, 70, 229, 0.3);
}

.btn-secondary {
  background-color: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #f9fafb;
  color: #111827;
  border-color: #9ca3af;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-icon {
  height: 1.25rem;
  width: 1.25rem;
}

/* Animations */
.fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pulse-animation {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(254, 243, 199, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(254, 243, 199, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(254, 243, 199, 0);
  }
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Success Animation Styles */
.success-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
  animation: fadeIn 0.5s ease;
}

.success-checkmark {
  margin-bottom: 1.5rem;
}

.checkmark {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: block;
  stroke-width: 2;
  stroke: #fff;
  stroke-miterlimit: 10;
  box-shadow: inset 0px 0px 0px #16a34a;
  animation: fill .4s ease-in-out .4s forwards, scale .3s ease-in-out .9s both;
}

.checkmark-circle {
  stroke-dasharray: 166;
  stroke-dashoffset: 166;
  stroke-width: 2;
  stroke-miterlimit: 10;
  stroke: #16a34a;
  fill: none;
  animation: stroke 0.6s cubic-bezier(0.65, 0, 0.45, 1) forwards;
}

.checkmark-check {
  transform-origin: 50% 50%;
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: stroke 0.3s cubic-bezier(0.65, 0, 0.45, 1) 0.8s forwards;
}

@keyframes stroke {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes scale {
  0%, 100% {
    transform: none;
  }
  50% {
    transform: scale3d(1.1, 1.1, 1);
  }
}

@keyframes fill {
  100% {
    box-shadow: inset 0px 0px 0px 50px #16a34a;
  }
}

.success-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: #16a34a;
  margin-bottom: 0.5rem;
  animation: slideUp 0.5s ease 0.5s both;
}

.success-message {
  text-align: center;
  color: #4b5563;
  font-size: 1.1rem;
  line-height: 1.5;
  animation: slideUp 0.5s ease 0.7s both;
}

/* Error Alert Styles */
.error-alert {
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  color: #b91c1c;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-align: left;
  font-size: 0.95rem;
}

.error-icon {
  width: 1.25rem;
  height: 1.25rem;
  flex-shrink: 0;
  color: #ef4444;
}
</style>
