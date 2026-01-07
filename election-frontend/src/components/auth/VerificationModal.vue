<template>
  <v-dialog v-model="show" max-width="480px" persistent>
    <v-card class="verification-modal" :class="{ 'no-scroll': showSuccessAnimation }">
      <!-- Modern gradient header -->
      <div class="modal-header">
        <div class="icon-wrapper">
          <v-icon size="48" class="pulse-icon">mdi-email-check-outline</v-icon>
        </div>
        <h2 class="modal-title">Verifieer je e-mail</h2>
      </div>

      <v-card-text class="modal-content">
        <!-- Success animation overlay -->
        <transition name="success-overlay">
          <div v-if="showSuccessAnimation" class="success-animation-overlay">
            <div class="success-checkmark">
              <svg class="checkmark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
                <circle class="checkmark-circle" cx="26" cy="26" r="25" fill="#16a34a"/>
                <path class="checkmark-check" fill="none" stroke="white" stroke-width="3" d="M14.1 27.2l7.1 7.2 16.7-16.8"/>
              </svg>
            </div>
            <h3 class="success-title">
              <v-icon size="32" color="#667eea" class="success-icon">mdi-check-circle</v-icon>
              Gelukt!
            </h3>
            <p class="success-message">Je wordt nu ingelogd...</p>
          </div>
        </transition>

        <!-- Email display with modern styling -->
        <div class="email-display" v-show="!showSuccessAnimation">
          <span class="email-label">Code verstuurd naar</span>
          <span class="email-address">{{ email }}</span>
        </div>

        <!-- Expiration Timer -->
        <div class="expiration-timer" v-show="!showSuccessAnimation && !isExpired">
          <v-icon size="20" :color="timeRemaining < 60 ? '#ef4444' : '#667eea'">mdi-timer-outline</v-icon>
          <span :class="{ 'timer-warning': timeRemaining < 60 }">
            Code geldig voor nog <strong>{{ formattedTimeRemaining }}</strong>
          </span>
        </div>

        <div v-if="isExpired && !showSuccessAnimation" class="expired-notice">
          <v-icon size="20" color="#ef4444">mdi-alert-circle</v-icon>
          <span>Code verlopen. Vraag een nieuwe code aan.</span>
        </div>

        <!-- code input boxes -->
        <div class="code-input-container" v-show="!showSuccessAnimation">
          <v-text-field
            v-for="(digit, index) in code"
            :key="index"
            v-model="code[index]"
            :ref="'input' + index"
            variant="outlined"
            density="comfortable"
            class="code-input-box"
            maxlength="1"
            type="tel"
            inputmode="numeric"
            :disabled="isVerifying || showSuccessAnimation"
            @input="handleInput(index, $event)"
            @keydown="handleKeyDown(index, $event)"
            @paste="handlePaste"
          />
        </div>

        <!-- Error & Success messages -->
        <transition name="fade">
          <div v-if="error && !showSuccessAnimation" class="message-box error-box">
            <v-icon size="20">mdi-alert-circle</v-icon>
            <span>{{ error }}</span>
          </div>
        </transition>

        <!-- Resend code section -->
        <div class="resend-section" v-show="!showSuccessAnimation && !isVerifying">
          <p class="resend-text">Code niet ontvangen?</p>
          <button
            v-if="cooldown === 0"
            @click="resendCode"
            :disabled="isResending"
            class="resend-button"
          >
            <v-icon v-if="isResending" size="16" class="button-icon">mdi-loading mdi-spin</v-icon>
            <v-icon v-else size="16" class="button-icon">mdi-refresh</v-icon>
            <span>{{ isResending ? 'Versturen...' : 'Verstuur opnieuw' }}</span>
          </button>
          <p v-else class="cooldown-text">
            <v-icon size="16" class="cooldown-icon">mdi-timer-sand</v-icon>
            Opnieuw versturen over {{ cooldown }}s
          </p>
        </div>

        <!-- Help instructions (collapsible) -->
        <div class="help-section" v-show="!showSuccessAnimation && !isVerifying">
          <div class="help-title" @click="showHelp = !showHelp">
            <v-icon size="18" color="#6b7280">mdi-information-outline</v-icon>
            <span>Geen e-mail ontvangen?</span>
            <v-icon size="18" color="#6b7280" class="expand-icon">
              {{ showHelp ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
            </v-icon>
          </div>
          <transition name="expand">
            <ul v-show="showHelp" class="help-list">
              <li>
                <v-icon size="16" color="#667eea">mdi-folder-alert</v-icon>
                <span>Controleer je <strong>spam/ongewenste e-mail</strong> map</span>
              </li>
              <li>
                <v-icon size="16" color="#667eea">mdi-clock-outline</v-icon>
                <span>Wacht een paar minuten (kan soms vertraging hebben)</span>
              </li>
              <li>
                <v-icon size="16" color="#667eea">mdi-email-check</v-icon>
                <span>Zorg dat <strong>{{ email }}</strong> correct is</span>
              </li>
              <li>
                <v-icon size="16" color="#667eea">mdi-refresh-circle</v-icon>
                <span>Gebruik de knop hierboven om opnieuw te versturen</span>
              </li>
            </ul>
          </transition>
        </div>

        <!-- Loading indicator -->
        <div v-if="isVerifying && !showSuccessAnimation" class="verifying-indicator">
          <v-progress-circular
            indeterminate
            color="primary"
            size="32"
          />
          <p class="verifying-text">Bezig met verifiëren...</p>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import { VerificationService } from '@/services/verificationService';

export default {
  name: 'VerificationModal',
  props: {
    modelValue: Boolean,
    email: {
      type: String,
      required: true
    },
    username: {
      type: String,
      required: true
    },
    expiresAt: {
      type: String,
      required: true
    }
  },
  emits: ['update:modelValue', 'verified', 'error', 'update-expires-at'],
  data() {
    return {
      code: ['', '', '', ''],
      isVerifying: false,
      isResending: false,
      error: '',
      success: '',
      showSuccessAnimation: false,
      showHelp: false,
      cooldown: 0,
      cooldownInterval: null,
      timeRemaining: 900, // 15 minutes in seconds
      expirationInterval: null
    };
  },
  computed: {
    show: {
      get() {
        return this.modelValue;
      },
      set(value) {
        this.$emit('update:modelValue', value);
      }
    },
    isCodeComplete() {
      return this.code.every(digit => digit.length === 1);
    },
    formattedTimeRemaining() {
      const minutes = Math.floor(this.timeRemaining / 60);
      const seconds = this.timeRemaining % 60;
      return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    },
    isExpired() {
      return this.timeRemaining <= 0;
    }
  },
  watch: {
    modelValue(newVal) {
      if (newVal) {
        this.resetForm();
        this.startExpirationTimer();
        this.$nextTick(() => {
          this.focusInput(0);
        });
      }
    },
    expiresAt(newVal) {
      // Restart timer when expiresAt changes (e.g., after resend)
      if (newVal && this.modelValue) {
        this.startExpirationTimer();
      }
    }
  },
  methods: {
    resetForm() {
      this.code = ['', '', '', ''];
      this.error = '';
      this.success = '';
      this.showSuccessAnimation = false;
      // Start with 60 second cooldown on first open
      this.startCooldown(60);
      if (this.expirationInterval) {
        clearInterval(this.expirationInterval);
      }
    },
    
    startExpirationTimer() {
      // Calculate time remaining from expiresAt
      if (this.expiresAt) {
        const expirationTime = new Date(this.expiresAt).getTime();
        const now = new Date().getTime();
        this.timeRemaining = Math.max(0, Math.floor((expirationTime - now) / 1000));
      }
      
      // Clear existing interval
      if (this.expirationInterval) {
        clearInterval(this.expirationInterval);
      }
      
      // Start countdown
      this.expirationInterval = setInterval(() => {
        if (this.timeRemaining > 0) {
          this.timeRemaining--;
        } else {
          clearInterval(this.expirationInterval);
          this.error = 'Code verlopen. Vraag een nieuwe code aan!';
        }
      }, 1000);
    },
    
    focusInput(index) {
      const input = this.$refs['input' + index];
      if (input && input[0]) {
        input[0].focus();
      }
    },

    handleInput(index, event) {
      const value = event.target.value;
      
      // Only allow digits
      if (value && !/^\d$/.test(value)) {
        this.code[index] = '';
        return;
      }

      // Move to next input if digit entered
      if (value && index < 3) {
        this.$nextTick(() => {
          this.focusInput(index + 1);
        });
      }

      // Auto-submit when all 4 digits are entered
      if (this.isCodeComplete) {
        this.$nextTick(() => {
          this.verifyCode();
        });
      }
    },

    handleKeyDown(index, event) {
      // Handle backspace
      if (event.key === 'Backspace' && !this.code[index] && index > 0) {
        this.focusInput(index - 1);
      }
      
      // Handle arrow keys
      if (event.key === 'ArrowLeft' && index > 0) {
        event.preventDefault();
        this.focusInput(index - 1);
      }
      if (event.key === 'ArrowRight' && index < 3) {
        event.preventDefault();
        this.focusInput(index + 1);
      }
    },

    handlePaste(event) {
      event.preventDefault();
      const pasteData = event.clipboardData.getData('text');
      const digits = pasteData.replace(/\D/g, '').split('').slice(0, 4);
      
      digits.forEach((digit, index) => {
        if (index < 4) {
          this.code[index] = digit;
        }
      });

      // Focus last filled input or verify if complete
      if (digits.length === 4) {
        this.focusInput(3);
        this.$nextTick(() => {
          this.verifyCode();
        });
      } else if (digits.length > 0) {
        this.focusInput(Math.min(digits.length, 3));
      }
    },

    async verifyCode() {
      if (!this.isCodeComplete || this.isVerifying) return;

      this.isVerifying = true;
      this.error = '';
      this.success = '';

      try {
        const codeString = this.code.join('');
        const response = await VerificationService.verifyEmail(this.username, codeString);
        
        // Show success animation
        this.showSuccessAnimation = true;
        
        // Emit verified event with JWT token after animation
        setTimeout(() => {
          this.$emit('verified', response);
        }, 2500);
        
      } catch (err) {
        // Nederlandse error messages
        const errorMsg = err?.response?.data?.message;
        if (errorMsg?.includes('Invalid') || errorMsg?.includes('code')) {
          this.error = 'Onjuiste code. Probeer het opnieuw!';
        } else if (errorMsg?.includes('expired')) {
          this.error = 'Code verlopen. Vraag een nieuwe aan!';
        } else if (errorMsg?.includes('pending') || errorMsg?.includes('found')) {
          this.error = 'Geen registratie gevonden. Registreer opnieuw!';
        } else {
          this.error = errorMsg || 'Verificatie mislukt. Probeer opnieuw!';
        }
        this.code = ['', '', '', ''];
        this.focusInput(0);
      } finally {
        this.isVerifying = false;
      }
    },

    async resendCode() {
      if (this.isResending || this.cooldown > 0) return;

      this.isResending = true;
      this.error = '';
      this.success = '';

      try {
        const response = await VerificationService.resendCode(this.username);
        
        // Update the expiration time with the new one
        if (response.expiresAt) {
          this.$emit('update-expires-at', response.expiresAt);
          // Restart the expiration timer with new time
          this.startExpirationTimer();
        }
        
        this.success = 'Nieuwe code verstuurd! Controleer je e-mail.';
        
        // Start 60 second cooldown
        this.startCooldown(60);
        
        // Clear the input fields
        this.code = ['', '', '', ''];
        this.focusInput(0);

      } catch (err) {
        const errorMsg = err?.response?.data?.message;
        if (errorMsg?.includes('Wacht nog')) {
          this.error = errorMsg;
        } else if (errorMsg?.includes('Maximaal aantal')) {
          this.error = 'Maximaal aantal pogingen bereikt. Registreer opnieuw.';
        } else {
          this.error = errorMsg || 'Fout bij versturen code';
        }
      } finally {
        this.isResending = false;
      }
    },

    startCooldown(seconds) {
      this.cooldown = seconds;
      if (this.cooldownInterval) {
        clearInterval(this.cooldownInterval);
      }
      this.cooldownInterval = setInterval(() => {
        this.cooldown--;
        if (this.cooldown <= 0) {
          clearInterval(this.cooldownInterval);
        }
      }, 1000);
    }
  },
  beforeUnmount() {
    if (this.cooldownInterval) {
      clearInterval(this.cooldownInterval);
    }
    if (this.expirationInterval) {
      clearInterval(this.expirationInterval);
    }
  }
};
</script>

<style scoped>
/* Modern modal styling for Gen Z (18-25) */
.verification-modal {
  border-radius: 24px !important;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
}

.verification-modal.no-scroll {
  overflow: hidden !important;
  max-height: 100vh;
}

.verification-modal.no-scroll .modal-content {
  overflow: hidden !important;
}

/* Gradient header with animation */
.modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 32px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.modal-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.3;
}

.icon-wrapper {
  position: relative;
  z-index: 1;
  margin-bottom: 8px;
}

.pulse-icon {
  color: white;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.modal-title {
  color: white;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  position: relative;
  z-index: 1;
}

/* Content area */
.modal-content {
  padding: 20px 28px 20px !important;
  position: relative;
  min-height: 400px; /* Ensure consistent height for overlay */
  max-height: 100vh; /* Prevent expansion */
  overflow: hidden !important; /* Prevent scroll during success animation */
}

/* Success animation overlay */
.success-animation-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  z-index: 9999;
  margin: 0;
  padding: 40px 28px;
}

.success-overlay-enter-active {
  animation: overlayFadeIn 0.5s ease;
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* Animated checkmark */
.success-checkmark {
  margin-bottom: 32px;
}

.checkmark {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: block;
  animation: scaleCheckmark 0.5s ease-in-out both;
}

.checkmark-circle {
  animation: scaleIn 0.5s cubic-bezier(0.65, 0, 0.45, 1) forwards;
  transform-origin: center;
}

.checkmark-check {
  stroke-dasharray: 48;
  stroke-dashoffset: 48;
  animation: drawCheck 0.5s cubic-bezier(0.65, 0, 0.45, 1) 0.3s forwards;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes drawCheck {
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes scaleCheckmark {
  0% {
    transform: scale(0) rotate(-45deg);
    opacity: 0;
  }
  50% {
    transform: scale(1.1) rotate(5deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
  }
}

.success-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 16px 0;
  animation: slideUp 0.5s ease 0.6s both;
}

.success-icon {
  animation: slideUp 0.5s ease 0.6s both, rotate 0.6s ease 0.8s;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.success-message {
  font-size: 20px;
  color: #6b7280;
  font-weight: 600;
  margin: 0;
  animation: slideUp 0.5s ease 0.8s both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}


/* Email display */
.email-display {
  text-align: center;
  margin-bottom: 12px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8edf5 100%);
  border-radius: 12px;
  border: 2px solid #e0e7ff;
}

.email-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 2px;
  font-weight: 500;
}

.email-address {
  display: block;
  font-size: 15px;
  color: #667eea;
  font-weight: 700;
  word-break: break-all;
}

/* Expiration Timer */
.expiration-timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 2px solid #bbf7d0;
  border-radius: 12px;
  font-size: 13px;
  color: #15803d;
  font-weight: 500;
  animation: fadeIn 0.3s ease;
}

.expiration-timer strong {
  font-size: 15px;
  font-weight: 700;
  color: #166534;
}

.timer-warning {
  color: #991b1b !important;
}

.timer-warning strong {
  color: #7f1d1d !important;
  animation: pulse 1s ease-in-out infinite;
}

.expired-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  margin-bottom: 20px;
  background: #fef2f2;
  border: 2px solid #fecaca;
  border-radius: 12px;
  font-size: 13px;
  color: #991b1b;
  font-weight: 600;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

/* Code input boxes */
.code-input-container {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 18px;
}

.code-input-box {
  max-width: 56px;
  flex: 1;
}

.code-input-box :deep(input) {
  text-align: center;
  font-size: 32px;
  font-weight: 800;
  padding: 16px 8px !important;
  height: 64px;
  color: #667eea;
  caret-color: #667eea;
}

.code-input-box :deep(.v-field) {
  border-radius: 14px;
  border: 2px solid #e0e7ff;
  transition: all 0.3s ease;
}

.code-input-box :deep(.v-field:hover) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.code-input-box :deep(.v-field--focused) {
  border-color: #667eea !important;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2) !important;
}

/* Message boxes */
.message-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 20px;
  font-weight: 500;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.error-box {
  background: #fef2f2;
  border: 2px solid #fecaca;
  color: #dc2626;
}

.success-box {
  background: #f0fdf4;
  border: 2px solid #bbf7d0;
  color: #16a34a;
}

.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Resend section */
.resend-section {
  text-align: center;
  padding: 16px;
  background: #fafbfc;
  border-radius: 12px;
  margin-bottom: 8px;
}

.resend-text {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.resend-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #667eea;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 3px;
  transition: all 0.2s ease;
}

.resend-button .button-icon {
  margin-bottom: 1px;
}

.resend-button:hover:not(:disabled) {
  color: #764ba2;
  transform: translateY(-1px);
}

.resend-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cooldown-text {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
  font-weight: 500;
}

.cooldown-icon {
  opacity: 0.7;
}

/* Help section */
.help-section {
  margin-top: 12px;
  padding: 0;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  border: 1px solid #bae6fd;
  overflow: hidden;
}

.help-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin: 0;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s ease;
  user-select: none;
}

.help-title:hover {
  background: rgba(255, 255, 255, 0.5);
}

.help-title .expand-icon {
  margin-left: auto;
  transition: transform 0.3s ease;
}

.help-list {
  list-style: none;
  padding: 0 16px 12px 16px;
  margin: 0;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
}

.expand-enter-to,
.expand-leave-from {
  max-height: 200px;
  opacity: 1;
}

.help-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.help-list li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 12px;
  color: #4b5563;
  line-height: 1.6;
  padding: 3px 0;
}

.help-list li .v-icon {
  margin-top: 1px;
  flex-shrink: 0;
}

.help-list li span {
  flex: 1;
}

.help-list li strong {
  color: #1f2937;
  font-weight: 600;
}

/* Verifying indicator */
.verifying-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  margin-top: 16px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.verifying-text {
  font-size: 15px;
  color: #667eea;
  font-weight: 600;
  margin: 0;
}

/* Mobile responsive */
@media (max-width: 600px) {
  .modal-content {
    padding: 24px 20px 20px !important;
  }

  .modal-header {
    padding: 32px 20px 24px;
  }

  .modal-title {
    font-size: 24px;
  }

  .code-input-box {
    max-width: 56px;
  }

  .code-input-box :deep(input) {
    font-size: 28px;
    height: 64px;
  }
}
</style>
