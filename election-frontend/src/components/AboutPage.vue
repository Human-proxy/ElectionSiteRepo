<template>
  <LayoutComponent>
    <div class="min-h-screen py-12 bg-gray-50">
      <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <section class="text-center mb-16">
          <div class="w-16 h-16 bg-gradient-to-br from-orange-500 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg">
            <span class="text-white font-bold text-2xl">SW</span>
          </div>
          <h1 class="text-3xl lg:text-4xl font-bold mb-4 text-gray-900">
            Over StemWijs
          </h1>
          <p class="text-xl text-gray-600 max-w-2xl mx-auto leading-relaxed">
            StemWijs maakt politiek toegankelijk en aantrekkelijk voor jongeren. 
            Ontdek partijen, vergelijk standpunten en maak een weloverwogen keuze.
          </p>
        </section>

        <section class="mb-20">
          <div class="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <span class="inline-flex items-center rounded-md border border-transparent bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-700 mb-4">
                Onze Missie
              </span>
              
              <h2 class="text-2xl lg:text-3xl font-bold mb-6 text-gray-900">
                Politiek begrijpelijk maken voor elke jongere
              </h2>
              <p class="text-lg text-gray-600 mb-6 leading-relaxed">
                Wij geloven dat elke stem telt en dat politiek niet saai hoeft te zijn. 
                Daarom hebben we StemWijs gemaakt: een platform dat politieke informatie 
                presenteert op een manier die aansluit bij hoe jongeren willen leren en ontdekken.
              </p>
              <p class="text-lg text-gray-600 mb-8 leading-relaxed">
                Door gebruik te maken van modern design, gamification en toegankelijke 
                informatie, maken we het makkelijker voor jongeren om geïnformeerde 
                beslissingen te nemen over hun politieke toekomst.
              </p>
              
              <button 
                @click="goToQuiz" 
                class="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none ring-offset-background bg-blue-600 text-white hover:bg-blue-700 h-11 px-8"
              >
                <Zap class="w-5 h-5 mr-2" />
                Begin je politieke reis
                <ArrowRight class="w-5 h-5 ml-2" />
              </button>
            </div>
            
            <div class="relative">
              <div class="aspect-video rounded-2xl overflow-hidden shadow-2xl border border-gray-200">
                <ImageWithFallback
                  src="https://images.unsplash.com/photo-1592246966407-4eedfa69bd2e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHxuZXRoZXJsYW5kcyUyMHBhcmxpYW1lbnQlMjBidWlsZGluZ3xlbnwxfHx8fDE3NTc1ODI2MDJ8MA&ixlib=rb-4.1.0&q=80&w=1080"
                  alt="Nederlandse Tweede Kamer"
                  className="w-full h-full object-cover hover:scale-105 transition-transform duration-700"
                />
              </div>
            </div>
          </div>
        </section>

        <section class="mb-20">
          <div class="text-center mb-12">
            <h2 class="text-2xl lg:text-3xl font-bold mb-4 text-gray-900">
              Wat maakt StemWijs bijzonder?
            </h2>
            <p class="text-lg text-gray-600 max-w-2xl mx-auto">
              Ontdek de functies die politiek leuker en toegankelijker maken
            </p>
          </div>

          <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div 
              v-for="(feature, index) in features" 
              :key="index" 
              class="bg-white rounded-xl border border-gray-200 shadow-sm p-6 hover:shadow-md transition-shadow pt-10 pb-10 pr-10 pl-10"
            >
              <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center mx-auto mb-4">
                <component :is="feature.icon" class="w-6 h-6 text-white" />
              </div>
              <h3 class="text-lg font-semibold text-center mb-2">{{ feature.title }}</h3>
              <p class="text-gray-500 text-center text-sm leading-relaxed">
                {{ feature.description }}
              </p>
            </div>
          </div>
        </section>

        <section class="mb-20">
          <div class="text-center mb-12">
            <h2 class="text-2xl lg:text-3xl font-bold mb-4 text-gray-900 pt-10 pb-10 pr-10 pl-10">
              Onze Waarden
            </h2>
            <p class="text-lg text-gray-600 max-w-2xl mx-auto">
              Deze principes staan centraal in alles wat we doen
            </p>
          </div>

          <div class="grid md:grid-cols-3 gap-8">
            <div v-for="(value, index) in values" :key="index" class="text-center group">
              <div class="w-16 h-16 bg-gradient-to-br from-green-500 to-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-4 transform group-hover:scale-110 transition-transform duration-300">
                <component :is="value.icon" class="w-8 h-8 text-white" />
              </div>
              <h3 class="text-xl font-bold mb-3 text-gray-900">{{ value.title }}</h3>
              <p class="text-gray-500 leading-relaxed">{{ value.description }}</p>
            </div>
          </div>
        </section>

        <section class="mb-16  mt-16">
          <div class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl border border-blue-100 p-8 lg:p-12 ">
            <div class="text-center mb-10 pt-10 pb-10 pr-10 pl-10 ">
              <h2 class="text-2xl lg:text-3xl font-bold mb-4 text-gray-900">
                Samen maken we politiek toegankelijk
              </h2>
              <p class="text-lg text-gray-600">
                Door jongeren, voor jongeren
              </p>
            </div>
            
            <div class="grid sm:grid-cols-3 gap-8 text-center divide-y sm:divide-y-0 sm:divide-x divide-gray-200/50 mb-8">
              <div class="p-4">
                <div class="text-4xl lg:text-5xl font-bold text-blue-600 mb-2">26</div>
                <p class="text-gray-600 font-medium">Partijen in detail</p>
              </div>
              <div class="p-4">
                <div class="text-4xl lg:text-5xl font-bold text-blue-600 mb-2">150</div>
                <p class="text-gray-600 font-medium">Tweede Kamer zetels</p>
              </div>
              <div class="p-4">
                <div class="text-4xl lg:text-5xl font-bold text-blue-600 mb-2">4</div>
                <p class="text-gray-600 font-medium">Interactieve inzichten</p>
              </div>
            </div>
          </div>
        </section>

        <section class="text-center pb-8 ">
          <div class="bg-white rounded-xl border-2 border-blue-100 shadow-lg p-8 lg:p-12">
            <h2 class="text-2xl lg:text-3xl font-bold mb-4 text-gray-900 mt-8">
              Klaar om je politieke reis te beginnen?
            </h2>
            <p class="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">
              Ontdek welke partij het beste bij jou past en maak een geïnformeerde keuze 
              bij de volgende verkiezingen. Je stem telt!
            </p>
            
            <div class="flex flex-col sm:flex-row gap-4 justify-center mb-8">
              <button 
                @click="goToQuiz" 
                class="inline-flex items-center justify-center rounded-md text-base font-medium transition-all shadow-md hover:shadow-lg bg-gradient-to-r from-blue-600 to-purple-600 text-white hover:opacity-90 h-12 px-8"
              >
                <Zap class="w-5 h-5 mr-2" />
                Start Partij-Match
              </button>
              
              <button 
                @click="goToResults" 
                class="inline-flex items-center justify-center rounded-md text-base font-medium transition-colors border-2 border-gray-200 bg-transparent hover:bg-gray-100 text-gray-900 h-12 px-8"
              >
                Bekijk alle resultaten
              </button>
            </div>
          </div>
        </section>

      </div>
    </div>
  </LayoutComponent>
</template>

<script setup>
import { useRouter } from 'vue-router';
import LayoutComponent from '@/components/LayoutComponent/LayoutComponent.vue';
import ImageWithFallback from './ImageWithFallback.vue';
import { 
  Target, 
  Users, 
  Heart, 
  Zap, 
  Shield, 
  Globe,
  ArrowRight,
  CheckCircle,
  Lightbulb
} from 'lucide-vue-next';

const router = useRouter();

const goToQuiz = () => {
  router.push('/quiz');
};

const goToResults = () => {
  router.push('/results');
};

const features = [
  {
    icon: Target,
    title: 'Partij-Match',
    description: 'Ontdek welke partij het beste bij jou past met onze interactieve quiz'
  },
  {
    icon: Globe,
    title: 'Parlement Visualisatie',
    description: 'Bekijk de zetelverdeling in een overzichtelijke en interactieve grafiek'
  },
  {
    icon: Users,
    title: 'Partij Profielen',
    description: 'Uitgebreide informatie over alle partijen en hun standpunten'
  },
  {
    icon: Heart,
    title: 'Jeugdgericht',
    description: 'Speciaal ontworpen voor jongeren van 16-25 jaar'
  },
  {
    icon: Zap,
    title: 'Gamification',
    description: 'Maak politiek leuk met badges, voortgang en interactieve elementen'
  },
  {
    icon: Shield,
    title: 'Toegankelijk',
    description: 'Volledig toegankelijk volgens WCAG 2.1 AA richtlijnen'
  }
];

const values = [
  {
    title: 'Onpartijdig',
    description: 'Wij presenteren alle partijen eerlijk en objectief, zonder voorkeur.',
    icon: CheckCircle
  },
  {
    title: 'Toegankelijk',
    description: 'Politiek moet voor iedereen begrijpelijk en toegankelijk zijn.',
    icon: Users
  },
  {
    title: 'Educatief',
    description: 'Wij helpen jongeren weloverwogen keuzes te maken door goede informatie.',
    icon: Lightbulb
  }
];
</script>

<style scoped>

</style>