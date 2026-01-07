<template>
  <div class="reset-request-container">
    <div class="reset-card">
      <div class="logo-section">
        <div class="logo">
          <span class="logo-icon">NL</span>
          <span class="logo-name">StemWijs</span>
        </div>
      </div>

      <h1 class="title">Wachtwoord Vergeten</h1>
      <p class="subtitle">Geen probleem! Vul je e-mailadres in en we sturen je een link om je wachtwoord te resetten.</p>

      <form @submit.prevent="handleSubmit" class="reset-form">
        <div class="form-group">
          <label for="email">E-mailadres</label>
          <input
            type="email"
            id="email"
            v-model="email"
            placeholder="jouw@email.nl"
            required
            :disabled="isLoading"
            class="input-field"
          />
        </div>

        <button type="submit" :disabled="isLoading" class="submit-btn">
          <span v-if="!isLoading">Verstuur Reset Link</span>
          <span v-else>Versturen...</span>
        </button>
      </form>

      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>

      <div class="back-to-login">
        <router-link to="/inloggen">← Terug naar inloggen</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'WachtwoordVergetenPage',
  data() {
    return {
      email: '',
      isLoading: false,
      message: '',
      messageType: '' // 'success' or 'error'
    };
  },
  methods: {
    async handleSubmit() {
      this.message = '';
      this.messageType = '';
      this.isLoading = true;

      try {
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/password-reset/request`, {
          email: this.email
        });

        this.message = response.data.message;
        this.messageType = 'success';
        this.email = ''; // Clear the form
      } catch (error) {
        this.message = error.response?.data?.message || 'Er is iets misgegaan. Probeer het later opnieuw.';
        this.messageType = 'error';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.reset-request-container {
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

.input-field {
  width: 100%;
  padding: 14px 16px;
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

.submit-btn {
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
}

.submit-btn:hover:not(:disabled) {
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

.message.success {
  background-color: #d1fae5;
  color: #065f46;
  border-left: 4px solid #10b981;
}

.message.error {
  background-color: #fee2e2;
  color: #991b1b;
  border-left: 4px solid #ef4444;
}

.back-to-login {
  text-align: center;
  margin-top: 20px;
}

.back-to-login a {
  color: #7c3aed;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.back-to-login a:hover {
  color: #6366f1;
  text-decoration: underline;
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
