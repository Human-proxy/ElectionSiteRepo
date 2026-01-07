<template>
  <div class="reset-password-container">
    <div class="reset-card">
      <div class="logo-section">
        <div class="logo">
          <span class="logo-icon">NL</span>
          <span class="logo-name">StemWijs</span>
        </div>
      </div>

      <div v-if="validatingToken" class="loading-state">
        <div class="spinner"></div>
        <p>Token valideren...</p>
      </div>

      <div v-else-if="!tokenValid" class="error-state">
        <div class="error-icon">⚠️</div>
        <h1 class="title">Ongeldige of Verlopen Link</h1>
        <p class="subtitle">
          Deze wachtwoord reset link is verlopen of ongeldig. 
          Vraag een nieuwe reset link aan.
        </p>
        <router-link to="/wachtwoord-vergeten" class="btn-primary">
          Nieuwe Link Aanvragen
        </router-link>
      </div>

      <div v-else-if="resetSuccess" class="success-state">
        <div class="success-icon">✓</div>
        <h1 class="title">Wachtwoord Gewijzigd!</h1>
        <p class="subtitle">
          Je wachtwoord is succesvol gewijzigd. 
          Je kunt nu inloggen met je nieuwe wachtwoord.
        </p>
        <router-link to="/inloggen" class="btn-primary">
          Ga naar Inloggen
        </router-link>
      </div>

      <div v-else>
        <h1 class="title">Nieuw Wachtwoord Instellen</h1>
        <p class="subtitle">Kies een sterk wachtwoord van minimaal 8 karakters.</p>

        <form @submit.prevent="handleSubmit" class="reset-form">
          <div class="form-group">
            <label for="password">Nieuw Wachtwoord</label>
            <div class="password-input-wrapper">
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                placeholder="Minimaal 8 karakters"
                required
                minlength="8"
                :disabled="isLoading"
                class="input-field"
                :class="{ 'input-error': passwordError && password }"
                @input="validatePassword"
              />
              <button 
                type="button" 
                @click="showPassword = !showPassword"
                class="toggle-password"
                :disabled="isLoading"
              >
                <svg 
                  v-if="showPassword"
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
            <p v-if="passwordError && password" class="error-text">
              {{ passwordError }}
            </p>
            <p v-else-if="password && !passwordError" class="success-text">
              ✓ Sterk wachtwoord
            </p>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Bevestig Wachtwoord</label>
            <div class="password-input-wrapper">
              <input
                :type="showConfirmPassword ? 'text' : 'password'"
                id="confirmPassword"
                v-model="confirmPassword"
                placeholder="Herhaal je wachtwoord"
                required
                minlength="8"
                :disabled="isLoading"
                class="input-field"
                :class="{ 'input-error': confirmError && confirmPassword }"
                @input="validateConfirm"
              />
              <button 
                type="button" 
                @click="showConfirmPassword = !showConfirmPassword"
                class="toggle-password"
                :disabled="isLoading"
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
            <p v-if="confirmError && confirmPassword" class="error-text">
              {{ confirmError }}
            </p>
            <p v-else-if="confirmPassword && !confirmError && password" class="success-text">
              ✓ Wachtwoorden komen overeen
            </p>
          </div>

          <button type="submit" :disabled="isLoading || !isFormValid" class="submit-btn">
            <span v-if="!isLoading">Wachtwoord Wijzigen</span>
            <span v-else>Wijzigen...</span>
          </button>
        </form>

        <div v-if="errorMessage" class="message error">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'WachtwoordResetten',
  data() {
    return {
      token: '',
      password: '',
      confirmPassword: '',
      showPassword: false,
      showConfirmPassword: false,
      isLoading: false,
      validatingToken: true,
      tokenValid: false,
      resetSuccess: false,
      errorMessage: '',
      passwordError: '',
      confirmError: ''
    };
  },
  computed: {
    passwordMismatch() {
      return this.confirmPassword.length > 0 && this.password !== this.confirmPassword;
    },
    isFormValid() {
      return this.password && 
             this.confirmPassword && 
             !this.passwordError && 
             !this.confirmError &&
             this.password === this.confirmPassword;
    }
  },
  async mounted() {
    // Get token from URL query parameter
    this.token = this.$route.query.token;

    if (!this.token) {
      this.validatingToken = false;
      this.tokenValid = false;
      return;
    }

    // Validate the token
    await this.validateToken();
  },
  methods: {
    validatePassword() {
      const password = this.password;
      
      if (!password) {
        this.passwordError = '';
        return false;
      }
      
      if (password.length < 8) {
        this.passwordError = 'Wachtwoord moet minimaal 8 tekens bevatten';
        return false;
      }
      
      // Check for at least one number, one lowercase, one uppercase
      const hasNumber = /\d/.test(password);
      const hasLowercase = /[a-z]/.test(password);
      const hasUppercase = /[A-Z]/.test(password);
      
      if (!hasNumber || !hasLowercase || !hasUppercase) {
        this.passwordError = 'Wachtwoord moet een hoofdletter, kleine letter en cijfer bevatten';
        return false;
      }
      
      this.passwordError = '';
      this.validateConfirm(); // Revalidate confirm password when new password changes
      return true;
    },
    
    validateConfirm() {
      const newPassword = this.password;
      const confirmPassword = this.confirmPassword;
      
      if (!confirmPassword) {
        this.confirmError = '';
        return false;
      }
      
      if (newPassword !== confirmPassword) {
        this.confirmError = 'Wachtwoorden komen niet overeen';
        return false;
      }
      
      this.confirmError = '';
      return true;
    },
    
    async validateToken() {
      try {
        const response = await axios.get(`${import.meta.env.VITE_API_URL}/password-reset/validate/${this.token}`);
        this.tokenValid = response.data.valid;
      } catch (error) {
        this.tokenValid = false;
      } finally {
        this.validatingToken = false;
      }
    },
    async handleSubmit() {
      this.errorMessage = '';

      if (!this.validatePassword()) {
        this.errorMessage = 'Controleer het nieuwe wachtwoord';
        return;
      }

      if (!this.validateConfirm()) {
        this.errorMessage = 'Wachtwoorden komen niet overeen';
        return;
      }

      this.isLoading = true;

      try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/password-reset/reset`, {
          token: this.token,
          newPassword: this.password
        });

        this.resetSuccess = true;
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Er is iets misgegaan. Probeer het later opnieuw.';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.reset-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  max-width: 480px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.logo-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.logo {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #7c3aed 0%, #ff7624 100%);
  padding: 12px 24px;
  border-radius: 50px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: white;
  color: #7c3aed;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  margin-right: 12px;
}

.logo-name {
  color: white;
  font-size: 20px;
  font-weight: 700;
}

.loading-state,
.error-state,
.success-state {
  text-align: center;
  padding: 20px;
}

.spinner {
  border: 4px solid #f3f4f6;
  border-top: 4px solid #7c3aed;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.success-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: bold;
  margin: 0 auto 20px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
  margin-bottom: 12px;
}

.subtitle {
  font-size: 15px;
  color: #6b7280;
  text-align: center;
  margin-bottom: 30px;
  line-height: 1.5;
}

.reset-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.password-input-wrapper {
  position: relative;
}

.input-field {
  width: 100%;
  padding: 14px 50px 14px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  outline: none;
}

.input-field:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}

.input-field:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.input-field.input-error {
  border-color: #ef4444;
}

.input-field.input-error:focus {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  padding: 5px;
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.toggle-password:hover:not(:disabled) {
  opacity: 1;
}

.toggle-password:disabled {
  cursor: not-allowed;
}

.error-text {
  color: #ef4444;
  font-size: 13px;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.success-text {
  color: #10b981;
  font-size: 13px;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.validation-error {
  color: #ef4444;
  font-size: 13px;
  margin-top: -15px;
  margin-bottom: 15px;
}

.submit-btn,
.btn-primary {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #7c3aed 0%, #6366f1 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
  text-decoration: none;
  display: inline-block;
  text-align: center;
}

.submit-btn:hover:not(:disabled),
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(124, 58, 237, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.message {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  line-height: 1.5;
}

.message.error {
  background-color: #fee2e2;
  color: #991b1b;
  border-left: 4px solid #ef4444;
}

@media (max-width: 640px) {
  .reset-card {
    padding: 30px 20px;
  }

  .title {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }
}
</style>
