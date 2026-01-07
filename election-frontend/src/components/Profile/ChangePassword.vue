<template>
  <div class="wachtwoord-wijzigen-card">
    <div class="card-header">
      <h3 class="card-title">
        <svg 
          class="lock-icon" 
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
          <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
          <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
        </svg>
        Wachtwoord Wijzigen
      </h3>
      <p class="card-description">
        Wijzig je wachtwoord voor extra beveiliging
      </p>
    </div>
    
    <div class="card-content">
      <form @submit.prevent="handleChangePassword" class="password-form">
        <!-- Huidig Wachtwoord -->
        <div class="form-group">
          <label for="currentPassword" class="form-label">Huidig Wachtwoord</label>
          <div class="password-input-wrapper">
            <input
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              :type="showCurrentPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="••••••••"
            />
            <button 
              type="button" 
              class="password-toggle"
              @click="showCurrentPassword = !showCurrentPassword"
              tabindex="-1"
            >
              <svg 
                v-if="showCurrentPassword"
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
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                <line x1="1" y1="1" x2="23" y2="23"/>
              </svg>
              <svg 
                v-else
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
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
        </div>
        
        <!-- Nieuw Wachtwoord -->
        <div class="form-group">
          <label for="newPassword" class="form-label">Nieuw Wachtwoord</label>
          <div class="password-input-wrapper">
            <input
              id="newPassword"
              v-model="passwordForm.newPassword"
              :type="showNewPassword ? 'text' : 'password'"
              class="form-input"
              :class="{ 'input-error': passwordError && passwordForm.newPassword }"
              placeholder="••••••••"
              @input="validatePassword"
            />
            <button 
              type="button" 
              class="password-toggle"
              @click="showNewPassword = !showNewPassword"
              tabindex="-1"
            >
              <svg 
                v-if="showNewPassword"
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
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                <line x1="1" y1="1" x2="23" y2="23"/>
              </svg>
              <svg 
                v-else
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
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <p v-if="passwordError && passwordForm.newPassword" class="error-message">
            {{ passwordError }}
          </p>
          <p v-else-if="passwordForm.newPassword && !passwordError" class="success-message">
            ✓ Sterk wachtwoord
          </p>
        </div>
        
        <!-- Validate new password -->
        <div class="form-group">
          <label for="confirmPassword" class="form-label">Bevestig Nieuw Wachtwoord</label>
          <div class="password-input-wrapper">
            <input
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              class="form-input"
              :class="{ 'input-error': confirmError && passwordForm.confirmPassword }"
              placeholder="••••••••"
              @input="validateConfirm"
            />
            <button 
              type="button" 
              class="password-toggle"
              @click="showConfirmPassword = !showConfirmPassword"
              tabindex="-1"
            >
              <svg 
                v-if="showConfirmPassword"
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
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                <line x1="1" y1="1" x2="23" y2="23"/>
              </svg>
              <svg 
                v-else
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
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <p v-if="confirmError && passwordForm.confirmPassword" class="error-message">
            {{ confirmError }}
          </p>
          <p v-else-if="passwordForm.confirmPassword && !confirmError && passwordForm.newPassword" class="success-message">
            ✓ Wachtwoorden komen overeen
          </p>
        </div>
        
        <button type="submit" class="change-button" :disabled="isSaving || !isFormValid">
          <svg 
            class="button-icon" 
            xmlns="http://www.w3.org/2000/svg" 
            width="16" 
            height="16" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor" 
            stroke-width="2" 
            stroke-linecap="round" 
            stroke-linejoin="round"
          >
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          {{ isSaving ? 'Wijzigen...' : 'Wachtwoord Wijzigen' }}
        </button>
      </form>
    </div>
    
    <!-- Toast Notification -->
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
</template>

<script setup>
import { ref, computed } from 'vue';
import { getToken } from '@/utils/auth';
import { ProfileService } from '@/services/profileService';

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
const isSaving = ref(false);
const toast = ref({ show: false, message: '', type: 'success' });
const passwordError = ref('');
const confirmError = ref('');

// Validate new password
function validatePassword() {
  const password = passwordForm.value.newPassword;
  
  if (!password) {
    passwordError.value = '';
    return false;
  }
  
  if (password.length < 8) {
    passwordError.value = 'Wachtwoord moet minimaal 8 tekens bevatten';
    return false;
  }
  
  // Check for at least one number, one lowercase, one uppercase
  const hasNumber = /\d/.test(password);
  const hasLowercase = /[a-z]/.test(password);
  const hasUppercase = /[A-Z]/.test(password);
  
  if (!hasNumber || !hasLowercase || !hasUppercase) {
    passwordError.value = 'Wachtwoord moet een hoofdletter, kleine letter en cijfer bevatten';
    return false;
  }
  
  passwordError.value = '';
  validateConfirm(); // Revalidate confirm password when new password changes
  return true;
}

// Validate confirm password
function validateConfirm() {
  const newPassword = passwordForm.value.newPassword;
  const confirmPassword = passwordForm.value.confirmPassword;
  
  if (!confirmPassword) {
    confirmError.value = '';
    return false;
  }
  
  if (newPassword !== confirmPassword) {
    confirmError.value = 'Wachtwoorden komen niet overeen';
    return false;
  }
  
  confirmError.value = '';
  return true;
}

// Check if form is valid
const isFormValid = computed(() => {
  return passwordForm.value.currentPassword && 
         passwordForm.value.newPassword && 
         passwordForm.value.confirmPassword &&
         !passwordError.value && 
         !confirmError.value;
});

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type };
  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
}

async function handleChangePassword() {
  // Validate all fields
  if (!passwordForm.value.currentPassword) {
    showToast('Voer je huidige wachtwoord in', 'error');
    return;
  }
  
  if (!validatePassword()) {
    showToast('Controleer het nieuwe wachtwoord', 'error');
    return;
  }
  
  if (!validateConfirm()) {
    showToast('Wachtwoorden komen niet overeen', 'error');
    return;
  }
  
  isSaving.value = true;
  
  try {
    const token = getToken();
    if (!token) {
      showToast('Je bent niet ingelogd', 'error');
      return;
    }
    await ProfileService.changePassword(
      token,
      passwordForm.value.currentPassword,
      passwordForm.value.newPassword
    );
    // Success - clear form
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
    showToast('Wachtwoord succesvol gewijzigd!', 'success');
  } catch (error) {
    console.error('Error changing password:', error);
    if (error.response?.status === 400) {
      const errorMsg = error.response?.data || 'Ongeldig wachtwoord';
      if (typeof errorMsg === 'string') {
        if (errorMsg.includes('Current password is incorrect')) {
          showToast('Huidig wachtwoord is onjuist', 'error');
        } else if (errorMsg.includes('New password cannot be the same')) {
          showToast('Nieuw wachtwoord mag niet hetzelfde zijn als het oude', 'error');
        } else {
          showToast('Fout bij wijzigen: ' + errorMsg, 'error');
        }
      } else {
        showToast('Controleer je invoer', 'error');
      }
    } else if (error.response?.status === 401) {
      showToast('Je sessie is verlopen. Log opnieuw in.', 'error');
    } else {
      showToast('Fout bij het wijzigen van wachtwoord', 'error');
    }
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped>
.wachtwoord-wijzigen-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
}

.card-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
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

.lock-icon {
  color: #4F46E5; /* Blue color */
  flex-shrink: 0;
}

.card-description {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.card-content {
  padding: 24px;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-input {
  width: 100%;
  padding: 10px 40px 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  color: #1f2937;
  transition: all 0.2s;
}

.form-input.input-error {
  border-color: #ef4444;
  background: #fef2f2;
}

.form-input:focus {
  outline: none;
  border-color: #4F46E5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.form-input.input-error:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.form-input::placeholder {
  color: #9ca3af;
}

.password-toggle {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: #4F46E5;
}

.error-message {
  margin: 0;
  font-size: 13px;
  color: #dc2626;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.error-message::before {
  content: '✕';
  font-weight: bold;
}

.success-message {
  margin: 0;
  font-size: 13px;
  color: #16a34a;
  font-weight: 500;
}

.change-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  background: #4F46E5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 8px;
}

.change-button:hover:not(:disabled) {
  background: #4338CA;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(79, 70, 229, 0.2);
}

.change-button:active:not(:disabled) {
  transform: translateY(0);
}

.change-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.button-icon {
  flex-shrink: 0;
}

/* Toast Notifications */
.toast {
  position: fixed;
  bottom: 24px;
  right: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  font-weight: 500;
  z-index: 1000;
  animation: slideIn 0.3s ease;
}

.toast.success {
  background: #10b981;
  color: white;
}

.toast.error {
  background: #ef4444;
  color: white;
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

/* Responsive */
@media (max-width: 768px) {
  .card-header,
  .card-content {
    padding: 16px;
  }
  
  .toast {
    left: 16px;
    right: 16px;
    bottom: 16px;
  }
}
</style>
