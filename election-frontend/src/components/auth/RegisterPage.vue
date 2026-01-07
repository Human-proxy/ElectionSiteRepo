<template>
  <!--
    Thin page that:
    - delegates UI to <AuthForm mode="register">
    - derives username from first name (lowercase, spaces -> dashes)
    - calls AuthService.register and shows verification modal
  -->
  <layout-component>
    <div class="min-h-[calc(100vh-200px)] flex items-center justify-center p-4">
      <div class="w-full max-w-md">
        <auth-form
          mode="register"
          :isLoading="isLoading"
          :requireTerms="true"
          :showAge="true"
          @submit="handleSubmit"
          @toggle-mode="goLogin"
          @toast="toast"
        >
          <!-- Footer links (your originals) -->
          <template #footer>
            <div class="text-center text-sm text-gray-600">
              Heb je al een account?
              <a class="text-indigo-600 hover:text-indigo-700 font-medium hover:underline" href="/inloggen">
                Log hier in
              </a>
            </div>
            <div class="text-center">
              <a href="/home" class="text-sm text-gray-600 hover:text-gray-900">← Terug naar home</a>
            </div>
          </template>

          <!-- Snackbar passthrough -->
          <template #snackbar>
            <v-snackbar v-model="snackbar.show" :timeout="2500" location="top" rounded="lg">
              {{ snackbar.message }}
            </v-snackbar>
          </template>
        </auth-form>
      </div>
    </div>

    <!-- Verification Modal -->
    <verification-modal
      v-model="showVerificationModal"
      :email="registeredEmail"
      :username="registeredUsername"
      :expires-at="expiresAt"
      @verified="handleVerified"
      @update-expires-at="expiresAt = $event"
    />
  </layout-component>
</template>

<script>
/**
 * - Uses AuthForm in "register" mode
 * - Handles API call and shows verification modal
 * - User is logged in AFTER email verification
 */
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import AuthForm from "@/components/auth/AuthForm.vue";
import VerificationModal from "@/components/auth/VerificationModal.vue";
import { AuthService } from "@/services/authService";
import { setAuth, isLoggedIn } from "@/utils/auth";

export default {
  name: "RegisterPage",
  components: { LayoutComponent, AuthForm, VerificationModal },
  data() {
    return {
      isLoading: false,
      snackbar: { show: false, message: "" },
      showVerificationModal: false,
      registeredEmail: '',
      registeredUsername: '',
      expiresAt: null,
    };
  },
  mounted() {
    if (isLoggedIn()) this.goHome();
  },
  methods: {
    toast(msg) {
      this.snackbar.message = msg;
      this.snackbar.show = true;
    },
    goHome() {
      this.$router ? this.$router.push("/") : (window.location.href = "/");
    },
    goLogin() {
      this.$router ? this.$router.push("/inloggen") : (window.location.href = "/inloggen");
    },
    /**
     * Handle <AuthForm @submit> for register.
     * Shows verification modal instead of logging in immediately.
     * @param {{mode:'register', payload:{name:string,email:string,password:string,confirmPassword:string}}} param0
     */
    async handleSubmit({ payload }) {
      this.isLoading = true;
      try {
        const username = payload.name.trim().toLowerCase().replace(/\s+/g, "-");

        // Backend returns: { username, email, emailSent, message, expiresAt }
        const data = await AuthService.register({
          username,
          email: payload.email,
          password: payload.password,
          confirmPassword: payload.confirmPassword,
        });

        // Store registration data and show verification modal
        this.registeredEmail = data.email;
        this.registeredUsername = data.username;
        this.expiresAt = data.expiresAt;
        this.showVerificationModal = true;
        
        this.toast(data.message || 'Registratie succesvol! Controleer je e-mail.');
      } catch (e) {
        const msg = e?.response?.data?.message || e?.message || "Registreren mislukt";
        this.toast(msg);
        console.error("Register error:", e);
      } finally {
        this.isLoading = false;
      }
    },
    
    /**
     * Handle successful email verification
     * @param {{token: string, expiresIn: number, user: object}} response - JWT response from verification
     */
    handleVerified(response) {
      // Close modal
      this.showVerificationModal = false;
      
      // Store auth token and user data
      setAuth({ token: response.token, user: response.user });
      
      // Welcome user
      this.toast(`Welkom bij StemWijs, ${response.user.username}!`);
      
      // Redirect to home
      setTimeout(() => {
        this.goHome();
      }, 1500);
    }
  },
};
</script>
