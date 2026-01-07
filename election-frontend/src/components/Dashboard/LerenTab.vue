<template>
  <div class="leren-tab">
    <!-- Main Layout: Left content and Right cards -->
    <div class="flex gap-8 items-start justify-between max-w-6xl mx-auto">
      <!-- Left Content -->
      <div class="flex-1 max-w-xl mt-5">
        <div class="inline-flex items-center gap-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white border-0 rounded-full px-4 py-2 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
          Voor Nieuwe Kiezers
        </div>
        <h3 class="text-2xl font-bold mb-4 text-gray-900">
          Snap Jij Hoe Politiek Eigenlijk Werkt? 🤔
        </h3>
        <p class="text-gray-600 mb-6 leading-relaxed">
          Van jouw stem tot echte beslissingen in Den Haag. We leggen het Nederlandse politieke systeem simpel en visueel uit - perfect voor eerste keer kiezers!
        </p>

        <div class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl" style="padding: 1.5rem;">
          <h4 class="font-bold mb-2">⚡ Snel en overzichtelijk</h4>
          <p class="text-sm text-gray-600 mb-4">
            In 5 minuten begrijp je hoe jouw stem uiteindelijk beleid wordt.
          </p>
          <div class="w-full flex justify-center" style="margin-bottom: 0.5rem;">
            <button 
              class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 mr-16 text-white px-12 py-3 rounded-lg transition-all duration-300 flex items-center justify-center gap-2 group text-sm"
              @click="navigateToSystemPage"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
              Hoe werkt het Nederlandse systeem?
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Right Cards Grid -->
      <div class="grid grid-cols-2 gap-4 flex-shrink-0 self-center" style="width: 600px;">
      <!-- Jouw Stem - Blauw -->
      <div 
        class="group hover:shadow-lg transition-all duration-300 cursor-pointer bg-gradient-to-br from-blue-50 to-blue-100 border-2 border-blue-200 rounded-xl"
        @click="navigateToSection('jouw-stem')"
      >
          <div class="py-4 px-5 text-center">
          <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform mt-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
            </svg>
          </div>
          <h4 class="font-semibold text-sm mb-1 text-gray-900">Jouw Stem</h4>
          <p class="text-xs text-gray-600">
            Hoe wordt jouw stem zetels?
          </p>
        </div>
      </div>

      <!-- Tweede Kamer - Groen -->
      <div 
        class="group hover:shadow-lg transition-all duration-300 cursor-pointer bg-gradient-to-br from-green-50 to-green-100 border-2 border-green-200 rounded-xl"
        @click="navigateToSection('tweede-kamer')"
      >
          <div class="py-4 px-5 text-center">
          <div class="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-xl flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform mt-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <h4 class="font-semibold text-sm mb-1 text-gray-900">Tweede Kamer</h4>
          <p class="text-xs text-gray-600">
            Wie maakt de wetten?
          </p>
        </div>
      </div>

      <!-- Coalitie - Paars -->
      <div 
        class="group hover:shadow-lg transition-all duration-300 cursor-pointer bg-gradient-to-br from-purple-50 to-purple-100 border-2 border-purple-200 rounded-xl"
        @click="navigateToSection('coalitie')"
      >
          <div class="py-4 px-5 text-center">
          <div class="w-12 h-12 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform mt-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </div>
          <h4 class="font-semibold text-sm mb-1 text-gray-900">Coalitie</h4>
          <p class="text-xs text-gray-600">
            Hoe werken partijen samen?
          </p>
        </div>
      </div>

      <!-- De Koning - Oranje -->
      <div 
        class="group hover:shadow-lg transition-all duration-300 cursor-pointer bg-gradient-to-br from-orange-50 to-orange-100 border-2 border-orange-200 rounded-xl"
        @click="navigateToSection('de-koning')"
      >
          <div class="py-4 px-5 text-center">
          <div class="w-12 h-12 bg-gradient-to-br from-orange-500 to-orange-600 rounded-xl flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform mt-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <h4 class="font-semibold text-sm mb-1 text-gray-900">De Koning</h4>
          <p class="text-xs text-gray-600">
            Wat doet hij eigenlijk?
          </p>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter();

const navigateToSystemPage = () => {
  router.push('/leer');
};

const navigateToSection = (sectionId) => {
  router.push(`/leer#${sectionId}`);
};
</script>

<style scoped>
.leren-tab {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>