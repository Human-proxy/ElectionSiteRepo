<template>
  <div class="profiel-informatie-card">
    <div class="card-header">
      <h3 class="card-title">
        <svg 
          class="user-icon" 
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
          <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        Profiel Informatie
      </h3>
      <p class="card-description">
        Werk je persoonlijke gegevens bij
      </p>
    </div>
    
    <div class="card-content">
      <form @submit.prevent="handleSaveProfile" class="profile-form">
        <div class="form-group">
          <label for="name" class="form-label">
            Gebruikersnaam
          </label>
          <input
            id="name"
            v-model="formData.name"
            type="text"
            class="form-input"
            placeholder="Je gebruikersnaam"
          />
        </div>
        
        <div class="form-group">
          <label for="email" class="form-label">Email</label>
          <input
            id="email"
            v-model="formData.email"
            type="email"
            class="form-input"
            :class="{ 'input-error': emailError && formData.email }"
            placeholder="je@email.nl"
            @blur="validateEmail"
          />
          <p v-if="emailError && formData.email" class="error-message">
            {{ emailError }}
          </p>
          <p v-else-if="formData.email && !emailError" class="success-message">
            ✓ Geldig email adres
          </p>
        </div>
        
        <button type="submit" class="save-button" :disabled="isSaving">
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
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
          {{ isSaving ? 'Opslaan...' : 'Profiel Opslaan' }}
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
import { ref, onMounted, watch } from 'vue';
import { getUser, getToken, setAuth, clearAuth } from '@/utils/auth';
import { ProfileService } from '@/services/profileService';
import { useRouter } from 'vue-router';

const router = useRouter();
const formData = ref({
  name: '',
  email: ''
});

const isSaving = ref(false);
const toast = ref({ show: false, message: '', type: 'success' });
const emailError = ref('');

// Load user data on mount
onMounted(() => {
  const user = getUser();
  if (user) {
    formData.value.name = user.username || '';
    formData.value.email = user.email || '';
  }
});

// Watch email field for live validation
watch(() => formData.value.email, (newEmail) => {
  if (newEmail) {
    validateEmail();
  } else {
    emailError.value = '';
  }
});

// Validate email format
function validateEmail() {
  const email = formData.value.email.trim();
  
  if (!email) {
    emailError.value = '';
    return false;
  }
  
  // Basic email regex
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    emailError.value = 'Ongeldig email formaat';
    return false;
  }
  
  // Check length
  if (email.length > 254) {
    emailError.value = 'Email adres is te lang (max 254 tekens)';
    return false;
  }
  
  // Check local part and domain
  const [localPart, domain] = email.split('@');
  if (!localPart || !domain) {
    emailError.value = 'Ongeldig email formaat';
    return false;
  }
  
  if (localPart.length > 64) {
    emailError.value = 'Email gebruikersnaam is te lang (max 64 tekens)';
    return false;
  }
  
  // Valid email
  emailError.value = '';
  return true;
}

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type };
  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
}

async function handleSaveProfile() {
  // Validate username
  if (!formData.value.name.trim()) {
    showToast('Gebruikersnaam is verplicht', 'error');
    return;
  }

  if (formData.value.name.trim().length < 3) {
    showToast('Gebruikersnaam moet minimaal 3 tekens bevatten', 'error');
    return;
  }

  // Validate email
  if (!formData.value.email.trim()) {
    showToast('Email is verplicht', 'error');
    return;
  }

  // Comprehensive email validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(formData.value.email.trim())) {
    showToast('Voer een geldig email adres in', 'error');
    return;
  }

  // Additional email format checks
  const email = formData.value.email.trim();
  if (email.length > 254) {
    showToast('Email adres is te lang', 'error');
    return;
  }

  const [localPart, domain] = email.split('@');
  if (!localPart || !domain || localPart.length > 64) {
    showToast('Voer een geldig email adres in', 'error');
    return;
  }

  isSaving.value = true;

  try {
    const response = await ProfileService.updateProfile({
      username: formData.value.name.trim(),
      email: formData.value.email.trim()
    });

    // Check if response contains a new token (username was changed)
    if (response.token) {
      // Username was changed, update auth with the new token
      setAuth({
        token: response.token,
        user: response.user
      });
      showToast('Gebruikersnaam succesvol gewijzigd!', 'success');
      
      // Update form with new data
      formData.value.name = response.user.username;
      formData.value.email = response.user.email;
      
      // Trigger auth-changed event so other components update
      window.dispatchEvent(new Event('auth-changed'));
    } else {
      // Only email was changed, update user data (keep existing token)
      const token = getToken();
      setAuth({
        token: token,
        user: response.user
      });
      showToast('Profiel succesvol opgeslagen!', 'success');
    }
  } catch (error) {
    console.error('Error updating profile:', error);
    
    // Handle specific error messages
    if (error.response?.data?.message) {
      showToast(error.response.data.message, 'error');
    } else if (error.response?.status === 400) {
      const errorMsg = error.response?.data || 'Ongeldige invoer';
      if (typeof errorMsg === 'string') {
        if (errorMsg.includes('Username already exists')) {
          showToast('Deze gebruikersnaam is al in gebruik', 'error');
        } else if (errorMsg.includes('Email already exists')) {
          showToast('Dit email adres is al in gebruik', 'error');
        } else if (errorMsg.includes('Invalid email format')) {
          showToast('Ongeldig email formaat', 'error');
        } else if (errorMsg.includes('User not found')) {
          showToast('Gebruiker niet gevonden. Log opnieuw in.', 'error');
        } else {
          showToast('Fout bij het opslaan: ' + errorMsg, 'error');
        }
      } else {
        showToast('Ongeldige invoer, controleer je gegevens', 'error');
      }
    } else {
      showToast('Fout bij het opslaan van profiel', 'error');
    }
  } finally {
    isSaving.value = false;
  }
}
</script>

<style scoped>
.profiel-informatie-card {
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

.user-icon {
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

.profile-form {
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
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

.error-message {
  margin: 6px 0 0 0;
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
  margin: 6px 0 0 0;
  font-size: 13px;
  color: #16a34a;
  font-weight: 500;
}

.field-hint {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #b45309;
  background: #fef3c7;
  border: 1.5px solid #f59e0b;
  border-radius: 6px;
  font-weight: 600;
  padding: 10px 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.08);
}

.save-button {
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

.save-button:hover:not(:disabled) {
  background: #4338CA;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(79, 70, 229, 0.2);
}

.save-button:active:not(:disabled) {
  transform: translateY(0);
}

.save-button:disabled {
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
