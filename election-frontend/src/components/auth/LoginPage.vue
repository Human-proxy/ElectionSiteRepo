<template>
  <!--
    Keeps layout and routes the AuthForm events:
    - @submit -> calls AuthService.login
    - @toggle-mode -> navigate to /registreren
    - @toast -> shows snackbar
  -->
  <layout-component>
    <div class="min-h-[calc(100vh-200px)] flex items-center justify-center p-4">
      <div class="w-full max-w-md">
        <auth-form
          mode="login"
          :isLoading="isLoading"
          @submit="handleSubmit"
          @toggle-mode="goRegister"
          @toast="toast"
        >
          <!-- footer links -->
          <template #footer>
            <div class="text-center text-sm text-gray-600">
              Nog geen account?
              <a class="text-indigo-600 hover:text-indigo-700 font-medium hover:underline" href="/registreren">
                Registreer hier
              </a>
            </div>
            <div class="text-center">
              <a href="/home" class="text-sm text-gray-600 hover:text-gray-900">← Terug naar home</a>
            </div>
          </template>

          <!-- Surface snackbar from page (centralized) -->
          <template #snackbar>
            <v-snackbar v-model="snackbar.show" :timeout="2500" location="top" rounded="lg">
              {{ snackbar.message }}
            </v-snackbar>
          </template>
        </auth-form>
      </div>
    </div>
  </layout-component>
</template>

<script>
/**
 * Thin page that:
 *  - delegates UI to <AuthForm mode="login">
 *  - calls AuthService.login on submit
 *  - persists auth (localStorage) via utils/auth
 *  - redirects on success
 */
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import AuthForm from "@/components/auth/AuthForm.vue";
import { AuthService } from "@/services/authService";
import { setAuth, isLoggedIn } from "@/utils/auth";

export default {
  name: "LoginPage",
  components: { LayoutComponent, AuthForm },
  data() {
    return {
      isLoading: false,
      snackbar: { show: false, message: "" },
    };
  },
  mounted() {
    // If already authenticated, skip the form
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
    goRegister() {
      this.$router ? this.$router.push("/registreren") : (window.location.href = "/registreren");
    },
    /**
     * Handle <AuthForm @submit> for login.
     * @param {{mode:'login', payload:{identifier:string,password:string}}} param0
     */
    async handleSubmit({ payload }) {
      this.isLoading = true;
      try {
        const data = await AuthService.login(payload); // -> { token, user, ... }
        setAuth({ token: data.token, user: data.user });
        
        if (data.user.deletedAt) {
          this.$router.push('/reactivate-account');
          return;
        }

        this.toast(`Welkom terug, ${data.user.username}!`);
        this.goHome();
      } catch (e) {
        const msg =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          e?.message ||
          "Inloggen mislukt";
        this.toast(msg);
        console.error("Login error:", e);
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>
