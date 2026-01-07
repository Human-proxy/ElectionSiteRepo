<template>
  <div class="account-management-card">
    <div class="card-header">
      <h3 class="card-title">
        <svg 
          class="info-icon" 
          xmlns="http://www.w3.org/2000/svg" 
          width="20" 
          height="20" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round"
        >
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="16" x2="12" y2="12"/>
          <line x1="12" y1="8" x2="12.01" y2="8"/>
        </svg>
        Account Beheer
      </h3>
      <p class="card-description">
        Beheer je account instellingen
      </p>
    </div>
    
    <div class="card-content">
      <!-- Privacy Info Box 
      <div class="info-box">
        <p class="info-text">
          Je gegevens worden veilig opgeslagen en nooit gedeeld met derden. 
          Bekijk ons privacybeleid voor meer informatie.
        </p>
      
      </div>
      -->

      <!-- Privacy Policy Button
      <button 
        type="button" 
        class="btn btn-outline"
        @click="handlePrivacyPolicy"
      >
        Privacybeleid Bekijken
      </button> 
      -->
      <!-- Delete Account Button -->
      <button 
        type="button" 
        class="btn btn-danger"
        @click="showDeleteModal = true"
      >
        <svg 
          xmlns="http://www.w3.org/2000/svg" 
          width="18" 
          height="18" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round"
        >
          <polyline points="3 6 5 6 21 6"/>
          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          <line x1="10" y1="11" x2="10" y2="17"/>
          <line x1="14" y1="11" x2="14" y2="17"/>
        </svg>
        Account Verwijderen
      </button>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Account Verwijderen</h3>
          <button class="close-btn" @click="closeModal">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="warning-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          
          <p class="modal-text">
            Weet je zeker dat je je account wilt verwijderen? 
            Je account wordt gedeactiveerd en je gegevens worden <strong>30 dagen bewaard</strong>.
            Binnen deze periode kun je je account herstellen door opnieuw in te loggen.
          </p>
          
          <p class="modal-subtext">
            Na 30 dagen worden je gegevens permanent verwijderd.
          </p>
          
          <div v-if="deleteError" class="error-message">
            {{ deleteError }}
          </div>
        </div>
        
        <div class="modal-footer">
          <button 
            type="button" 
            class="btn btn-secondary"
            @click="closeModal"
            :disabled="isDeleting"
          >
            Annuleren
          </button>
          <button 
            type="button" 
            class="btn btn-danger-solid"
            @click="handleDeleteAccount"
            :disabled="isDeleting"
          >
            <span v-if="!isDeleting">Ja, Verwijder Account</span>
            <span v-else>Verwijderen...</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Success Toast Notification -->
    <div v-if="showSuccessToast" class="toast-overlay">
      <div class="toast-notification success">
        <div class="toast-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="toast-content">
          <h4>Account Gedeactiveerd</h4>
          <p>Je account is gedeactiveerd. Je kunt het binnen 30 dagen herstellen.</p>
        </div>
        <!-- Progress bar -->
        <div class="progress-container">
          <div class="progress-bar-animated"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ProfileService } from '@/services/profileService';
import { getToken, clearAuth } from '@/utils/auth';

const router = useRouter();
const showDeleteModal = ref(false);
const isDeleting = ref(false);
const deleteError = ref(null);
const showSuccessToast = ref(false);

const handlePrivacyPolicy = () => {
  window.open('/privacy-policy', '_blank');
};

const closeModal = () => {
  if (!isDeleting.value) {
    showDeleteModal.value = false;
    deleteError.value = null;
  }
};

const handleDeleteAccount = async () => {
  isDeleting.value = true;
  deleteError.value = null;
  
  try {
    const token = getToken();
    
    // Call backend to delete account
    await ProfileService.deleteAccount(token);
    
    // Close the modal
    showDeleteModal.value = false;
    
    // Show success toast
    showSuccessToast.value = true;
    
    // Wait 2.5 seconds before redirecting
    setTimeout(() => {
      clearAuth();
      router.push('/');
    }, 2500);
    
  } catch (error) {
    console.error('Error deleting account:', error);
    deleteError.value = error.message || 'Er is een fout opgetreden bij het verwijderen van je account. Probeer het later opnieuw.';
  } finally {
    isDeleting.value = false;
  }
};
</script>

<style scoped>
.account-management-card {
  background: white;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  height: fit-content;
  max-width: 340px;
  min-width: 260px;
  width: 100%;
  margin-top: 24px;
  border: 1px solid #f3f4f6;
  transition: box-shadow 0.2s ease;
}

.account-management-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.card-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.info-icon {
  color: #3b82f6;
  flex-shrink: 0;
}

.card-description {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  font-weight: 400;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.info-box {
  background: linear-gradient(135deg, #eff6ff 0%, #f0f9ff 100%);
  border: 1px solid #bfdbfe;
  border-radius: 10px;
  padding: 18px;
  box-shadow: 0 1px 3px rgba(59, 130, 246, 0.08);
}

.info-text {
  margin: 0;
  font-size: 13.5px;
  color: #1e40af;
  line-height: 1.6;
}

.btn {
  width: 100%;
  padding: 14px 18px;
  border-radius: 10px;
  font-size: 14.5px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
}

.btn-outline {
  background: white;
  color: #374151;
  border: 1.5px solid #d1d5db;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.btn-outline:hover {
  background: #f9fafb;
  border-color: #9ca3af;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.btn-danger {
  background: white;
  color: #dc2626;
  border: 1.5px solid #fecaca;
  box-shadow: 0 1px 2px rgba(220, 38, 38, 0.1);
}

.btn-danger:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  box-shadow: 0 2px 4px rgba(220, 38, 38, 0.15);
  transform: translateY(-1px);
}

.btn-danger svg {
  flex-shrink: 0;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 480px;
  width: 100%;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #6b7280;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 24px;
}

.warning-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
  color: #dc2626;
}

.modal-text {
  margin: 0 0 12px 0;
  font-size: 15px;
  color: #1f2937;
  text-align: center;
  line-height: 1.5;
}

.modal-subtext {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  text-align: center;
  line-height: 1.5;
}

.error-message {
  margin-top: 16px;
  padding: 12px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  font-size: 13px;
  text-align: center;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-secondary {
  flex: 1;
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 10px 16px;
}

.btn-secondary:hover:not(:disabled) {
  background: #f9fafb;
}

.btn-danger-solid {
  flex: 1;
  background: #dc2626;
  color: white;
  border: 1px solid #dc2626;
  padding: 10px 16px;
}

.btn-danger-solid:hover:not(:disabled) {
  background: #b91c1c;
  border-color: #b91c1c;
}

.btn-secondary:disabled,
.btn-danger-solid:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Toast Notification Styles */
.toast-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease-out;
}

.toast-notification {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  animation: slideUp 0.4s ease-out;
}

.toast-notification.success {
  border-top: 4px solid #10b981;
}

.toast-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
}

.toast-icon svg {
  stroke: white;
}

.toast-content h4 {
  margin: 0 0 12px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.toast-content p {
  margin: 0;
  font-size: 15px;
  color: #6b7280;
  line-height: 1.5;
}

/* Progress bar container */
.progress-container {
  width: 100%;
  margin-top: 24px;
  background: #f3f4f6;
  border-radius: 9999px;
  height: 6px;
  overflow: hidden;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
}

.progress-bar-animated {
  height: 100%;
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  border-radius: 9999px;
  animation: progressAnimation 2.5s linear forwards;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.5);
}

@keyframes progressAnimation {
  from {
    width: 0%;
  }
  to {
    width: 100%;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(40px) scale(0.95);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

/* Responsive adjustments */
@media (max-width: 640px) {
  .modal-overlay {
    padding: 0;
  }
  
  .modal-content {
    border-radius: 0;
    max-width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
  }
  
  .modal-body {
    flex: 1;
    overflow-y: auto;
  }

  .toast-notification {
    padding: 28px 24px;
    border-radius: 12px;
  }

  .toast-icon {
    width: 64px;
    height: 64px;
  }

  .toast-icon svg {
    width: 28px;
    height: 28px;
  }

  .toast-content h4 {
    font-size: 18px;
  }

  .toast-content p {
    font-size: 14px;
  }
}
</style>
