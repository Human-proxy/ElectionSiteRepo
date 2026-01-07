<template>
  <!--
    Reusable authentication form.
    Renders either a login or register form based on the `mode` prop.
    Visual structure (header/body/footer) is slot-friendly so pages can override copy/links.
  -->
  <v-card elevation="8" class="custom-card border-0 overflow-hidden">
    <!-- Header -->
    <v-card-title class="text-center">
      <!--
        Slot: icon
        Replace to show a custom icon or illustration.
      -->
      <slot name="icon">
        <div
          class="w-16 h-16 mx-auto bg-gradient-to-br from-blue-500 to-purple-600 rounded-full grid place-items-center mb-3"
        >
          <v-icon size="28" color="white">
            {{ mode === 'login' ? 'mdi-login' : 'mdi-account-plus-outline' }}
          </v-icon>
        </div>
      </slot>

      <!-- Slot: title -->
      <div class="text-2xl font-semibold mb-1">
        <slot name="title">
          {{ mode === 'login' ? 'Welkom terug!' : 'Account aanmaken' }}
        </slot>
      </div>

      <!-- Slot: subtitle -->
      <div class="text-gray-600 text-sm">
        <slot name="subtitle">
          {{
            mode === 'login'
              ? 'Log in met je StemWijs account om verder te gaan'
              : 'Maak gratis je account aan en begin direct'
          }}
        </slot>
      </div>
    </v-card-title>

    <!-- Body -->
    <v-card-text>
      <div class="space-y-4 md:space-y-5">
        <!--
          Vuetify v-form:
          - Binds overall validity to `valid`
          - gate submit button by `valid` + `isLoading`
        -->
        <v-form v-model="valid" @submit.prevent="onSubmit">
          <!-- LOGIN FIELDS -->
          <template v-if="mode === 'login'">
            <!-- Identifier (email OR username) -->
            <label for="identifier" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-account-outline</v-icon>
              <span>E-mailadres <em>of</em> gebruikersnaam</span>
            </label>
            <v-text-field
              id="identifier"
              v-model="login.identifier"
              type="text"
              placeholder="bijv. gebruikersnaam of naam@voorbeeld.nl"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required, rules.identifierRule]"
              hide-details="auto"
            />

            <!-- Password (with toggle eye) -->
            <label for="password" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-lock-outline</v-icon>
              <span>Wachtwoord</span>
            </label>
            <v-text-field
              id="password"
              v-model="login.password"
              :type="showPassword ? 'text' : 'password'"
              :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
              @click:append-inner="showPassword = !showPassword"
              placeholder="Minimaal 8 karakters"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required, rules.minPassword]"
              hide-details="auto"
            />
            
            <!-- Wachtwoord vergeten link -->
            <div class="mt-2 mb-2" style="text-align: left;">
              <a href="/wachtwoord-vergeten" class="text-sm text-indigo-600 hover:text-indigo-700 hover:underline">
                Wachtwoord vergeten?
              </a>
            </div>
          </template>

          <!-- REGISTER FIELDS -->
          <template v-else>
            <!-- Name -->
            <label for="name" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-account-outline</v-icon>
              <span>Voornaam *</span>
            </label>
            <v-text-field
              id="name"
              v-model="register.name"
              placeholder="Je voornaam"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required]"
              hide-details="auto"
            />

            <!-- Email -->
            <label for="email" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-email-outline</v-icon>
              <span>E-mailadres *</span>
            </label>
            <v-text-field
              id="email"
              v-model="register.email"
              type="email"
              placeholder="je@email.nl"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required, rules.email]"
              hide-details="auto"
            />

            <!-- Age -->
            <div v-if="showAge" class="space-y-2">
              <label for="age" class="flex items-center gap-2 text-sm font-medium">
                <span>Geef je leeftijd</span>
              </label>
              <v-text-field
                id="age"
                v-model="register.age"
                type="number"
                placeholder="Bijv. 19"
                variant="outlined"
                density="comfortable"
                class="rounded-xl"
                hide-details="auto"
              />
            </div>

            <!-- Password -->
            <label for="reg-password" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-lock-outline</v-icon>
              <span>Wachtwoord *</span>
            </label>
            <v-text-field
              id="reg-password"
              v-model="register.password"
              :type="showPassword ? 'text' : 'password'"
              :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
              @click:append-inner="showPassword = !showPassword"
              placeholder="Minimaal 8 karakters"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required, rules.minPassword]"
              hide-details="auto"
            />

            <!-- Confirm password -->
            <label for="confirm" class="flex items-center gap-2 text-sm font-medium mb-1">
              <v-icon size="18">mdi-lock-check-outline</v-icon>
              <span>Bevestig wachtwoord *</span>
            </label>
            <v-text-field
              id="confirm"
              v-model="register.confirmPassword"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Herhaal je wachtwoord"
              variant="outlined"
              density="comfortable"
              class="rounded-xl"
              :rules="[rules.required, matchPassword]"
              hide-details="auto"
            />

            <!-- Terms  -->
            <div v-if="requireTerms" class="flex items-start gap-3 pt-1">
              <v-checkbox v-model="register.agreeTerms" density="comfortable" hide-details class="mt-1" />
              <div class="text-sm leading-relaxed">
                Ik ga akkoord met de
                <button type="button" class="text-indigo-600 hover:text-indigo-700 underline" @click="$emit('toast','Algemene voorwaarden worden binnenkort toegevoegd')">
                  algemene voorwaarden
                </button>
                en
                <button type="button" class="text-indigo-600 hover:text-indigo-700 underline" @click="$emit('toast','Privacy policy wordt binnenkort toegevoegd')">
                  privacy policy
                </button>.
              </div>
            </div>
          </template>

          <!-- Submit -->
          <v-btn
            type="submit"
            :disabled="submitDisabled"
            class="w-full font-semibold mt-2 rounded-xl"
            color="indigo"
            variant="flat"
          >
            <!-- Loading label -->
            <template v-if="isLoading">
              <span class="flex items-center gap-2">
                <span class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                {{ mode === 'login' ? 'Inloggen...' : 'Account aanmaken...' }}
              </span>
            </template>

            <!-- Idle label -->
            <template v-else>
              <span class="flex items-center gap-2">
                <v-icon size="18">{{ mode === 'login' ? 'mdi-login' : 'mdi-account-plus-outline' }}</v-icon>
                {{ submitTextComputed }}
                <v-icon size="18">mdi-arrow-right</v-icon>
              </span>
            </template>
          </v-btn>
        </v-form>
      </div>
    </v-card-text>

    <!-- Footer -->
    <v-card-actions class="flex-col gap-4 md:gap-5">
      <slot name="footer" :mode="mode" @toggle="toggleMode">
        <div class="text-center text-sm text-gray-600">
          <template v-if="mode === 'login'">
            Nog geen account?
            <button type="button" class="text-indigo-600 hover:text-indigo-700 font-medium hover:underline" @click="toggleMode">
              Registreer hier
            </button>
          </template>
          <template v-else>
            Heb je al een account?
            <button type="button" class="text-indigo-600 hover:text-indigo-700 font-medium hover:underline" @click="toggleMode">
              Log hier in
            </button>
          </template>
        </div>
      </slot>
    </v-card-actions>

    <!-- Optional snackbar passthrough -->
    <slot name="snackbar" />
  </v-card>
</template>

<script>
/**
 *
 * Props:
 * - mode: "login" | "register"
 * - isLoading: disables inputs + shows spinner label
 * - submitText: override submit button label
 * - requireTerms: show & enforce "agree to terms" (register only)
 * - showAge: show age field (register)
 * - initial: prefill values (handy for demos/tests)
 *
 * Emits:
 * - "submit": { mode: 'login'|'register', payload: object }  // page handles API call
 * - "toggle-mode": 'login'|'register'                         // parent can navigate
 * - "toast": string                                          // bubble UI messages up
 */
export default {
  name: "AuthForm",
  props: {
    mode: { type: String, default: "login" }, // 'login' | 'register'
    isLoading: { type: Boolean, default: false },
    submitText: { type: String, default: "" },
    requireTerms: { type: Boolean, default: true },
    showAge: { type: Boolean, default: true },
    initial: { type: Object, default: () => ({}) },
  },
  emits: ["submit", "toggle-mode", "toast"],
  data() {
    return {
      /** Vuetify form validity */
      valid: false,

      /** Show/hide password fields */
      showPassword: false,

      /** Login model */
      login: {
        identifier: this.initial.identifier || "",
        password: this.initial.password || "",
      },

      /** Register model */
      register: {
        name: this.initial.name || "",
        email: this.initial.email || "",
        password: this.initial.password || "",
        confirmPassword: this.initial.confirmPassword || "",
        age: this.initial.age || "",
        agreeTerms: this.initial.agreeTerms ?? false,
      },

      /** Validation rules kept local to the component */
      rules: {
        required: (v) => (!!v && String(v).trim().length) || "Vul dit veld in",
        email: (v) => /.+@.+\..+/.test(v) || "Voer een geldig e-mailadres in",
        minPassword: (v) => (v && v.length >= 8) || "Minimaal 8 tekens",
        identifierRule: (v) => {
          if (!v) return "Vul dit veld in";
          const s = String(v).trim();
          const looksEmail = /.+@.+\..+/.test(s);
          const looksUsername = s.length >= 3;
          return looksEmail || looksUsername || "Voer een geldig e-mailadres of gebruikersnaam in";
        },
      },
    };
  },
  computed: {
    /** Compute button label if not provided via prop */
    submitTextComputed() {
      if (this.submitText) return this.submitText;
      return this.mode === "login" ? "Inloggen" : "Account aanmaken";
    },

    /** Disable submit while loading, invalid, or when terms are required but unchecked */
    submitDisabled() {
      if (this.isLoading || !this.valid) return true;
      if (this.mode === "register" && this.requireTerms && !this.register.agreeTerms) return true;
      return false;
    },
  },
  methods: {
    /** Register-only validator to confirm password equality */
    matchPassword(v) {
      return v === this.register.password || "Wachtwoorden komen niet overeen";
    },

    /**
     * Normalize and emit the form payload upward.
     * Parent (page) decides how to call the backend and what to do on success.
     */
    onSubmit() {
      if (!this.valid) return;

      if (this.mode === "login") {
        const payload = {
          identifier: this.login.identifier,
          password: this.login.password,
        };
        this.$emit("submit", { mode: "login", payload });
      } else {
        const payload = {
          name: this.register.name,
          email: this.register.email,
          password: this.register.password,
          confirmPassword: this.register.confirmPassword,
          age: this.register.age,
          agreeTerms: this.register.agreeTerms,
        };
        this.$emit("submit", { mode: "register", payload });
      }
    },

    /**
     * Ask parent to toggle mode (often used to navigate between /inloggen and /registreren).
     */
    toggleMode() {
      const next = this.mode === "login" ? "register" : "login";
      this.$emit("toggle-mode", next);
    },
  },
};
</script>

<style scoped>
/* Glassy card styling aligned with your current design */
.custom-card {
  border-radius: 22px !important;
  border: 1px solid rgba(0,0,0,.06);
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  margin-top: 50px;
}

/* Tighten Vuetify spacing for this card only */
:deep(.v-card-text){padding:24px}
:deep(.v-card-title){padding:28px 24px 8px}
:deep(.v-card-actions){padding:0 24px 24px}
:deep(.v-input){margin-bottom:14px}
:deep(.v-btn){min-height:48px;border-radius:12px}

@media (min-width:768px){
  :deep(.v-card-text){padding:28px}
  :deep(.v-card-title){padding:32px 28px 12px}
  :deep(.v-card-actions){padding:0 28px 28px}
}
</style>
