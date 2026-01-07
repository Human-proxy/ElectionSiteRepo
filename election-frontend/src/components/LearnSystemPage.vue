<template>
  <LayoutComponent>
    <div class="learn-page">
      <!-- Hero Section -->
      <section class="hero-section" aria-labelledby="hero-title">
        <div class="hero-overlay">
          <div class="hero-content">
            <span class="badge" role="status">Kennismaking</span>
            <h1 id="hero-title" class="hero-title">Leer Hoe Nederland Bestuurd Wordt</h1>
            <p class="hero-subtitle">
              Ontdek in 5 simpele stappen hoe jouw stem leidt tot beleid. 
              Van stemhokje tot het Binnenhof - begrijp het Nederlandse politieke systeem.
            </p>
            <div class="hero-buttons" role="group" aria-label="Navigatie knoppen">
              <button 
                class="btn-primary-hero" 
                @click="scrollToLearnMore"
                aria-label="Scroll naar leermeer sectie">
                <IconComponent name="lightbulb" class="icon-svg-small" aria-hidden="true" />
                <span>Lees verder</span>
                <IconComponent name="arrow-right" class="icon-svg-small" aria-hidden="true" />
              </button>
              <router-link 
                to="/results" 
                class="btn-secondary"
                aria-label="Bekijk live verkiezingsdata">
                Bekijk Live Data
              </router-link>
            </div>
          </div>
        </div>
      </section>

      <!-- Steps Section -->
      <section id="learn-more" class="steps-section" aria-labelledby="steps-title">
        <div class="container">
          <div class="section-header">
            <h2 id="steps-title" class="section-title">
              Van Stem tot Beleid: 5 Stappen
              <IconComponent name="chart" class="title-icon" aria-hidden="true" />
            </h2>
            <p class="section-subtitle">
              Ontdek hoe jouw stem uiteindelijk leidt tot beleid dat jouw leven beïnvloedt.
            </p>
          </div>

          <div class="steps-container" role="list" aria-label="Stappen van stem tot beleid">
            <StepCard
              id="jouw-stem"
              :step-number="1"
              title="Het Stemmen"
              subtitle="Evenredige vertegenwoordiging"
              description="Nederland gebruikt een evenredig kiesstelsel. Elke stem telt mee voor zetels in het parlement."
              :details="[
                '150 zetels in de Tweede Kamer',
                'Elke partij krijgt zetels op basis van stemmen',
                'Meer stemmen = meer zetels',
                'Kieskringen zorgen voor regionale vertegenwoordiging'
              ]"
              color="blue"
              icon-name="vote"
            >
              <template #visualization>
                <SeatsVisualization />
              </template>
            </StepCard>

            <StepCard
              id="tweede-kamer"
              :step-number="2"
              title="Tweede Kamer"
              subtitle="Het debat en de wetten"
              description="De 150 volksvertegenwoordigers maken wetten en controleren de regering."
              :details="[
                'Stelt wetsvoorstellen voor',
                'Debatteert over belangrijke onderwerpen',
                'Controleert de regering (ministerraad)',
                'Kiest nieuwe regering na verkiezingen'
              ]"
              color="green"
              icon-name="users"
              :reverse="true"
            />

            <StepCard
              id="eerste-kamer"
              :step-number="3"
              title="Eerste Kamer"
              subtitle="De controle en reflectie"
              description="75 senatoren toetsen wetten op kwaliteit en uitvoerbaarheid."
              :details="[
                'Goedkeuren of afwijzen van wetten',
                'Geen recht om wetten aan te passen',
                'Gekozen door provinciale staten',
                'Functioneert als senaat of chambre de réflexion'
              ]"
              color="purple"
              icon-name="gavel"
            />

            <StepCard
              id="coalitie"
              :step-number="4"
              title="De Regering"
              subtitle="Uitvoering van beleid"
              description="Ministers en staatssecretarissen voeren wetten uit en maken beleid."
              :details="[
                'Gevormd door coalitie partijen',
                'Voert wetten uit en maakt regelgeving',
                'Beheert ministeries en beleid',
                'Verantwoordelijk aan de Tweede Kamer'
              ]"
              color="orange"
              icon-name="building"
              :reverse="true"
            />

            <StepCard
              id="de-koning"
              :step-number="5"
              title="De Koning"
              subtitle="Ceremoniële rol"
              description="Het staatshoofd ondertekent wetten en vertegenwoordigt Nederland."
              :details="[
                'Ondertekent wetten (formele goedkeuring)',
                'Geen politieke macht',
                'Symbool van nationale eenheid',
                'Ministers zijn verantwoordelijk voor daden van de Koning'
              ]"
              color="yellow"
              icon-name="crown"
            />
          </div>
        </div>
      </section>

      <!-- Election Timeline -->
      <section class="timeline-section" aria-labelledby="timeline-title">
        <div class="container">
          <div class="section-header">
            <h2 id="timeline-title" class="section-title">
              Verkiezingen Elke 4 Jaar
              <IconComponent name="calendar" class="title-icon" aria-hidden="true" />
            </h2>
            <p class="section-subtitle">
              Regelmatige verkiezingen zorgen ervoor dat politici verantwoording afleggen aan het volk.
            </p>
          </div>

          <div class="timeline-grid" role="list" aria-label="Verkiezingsproces tijdlijn">
            <TimelineCard
              title="Verkiezingsdag"
              subtitle="Jij gaat stemmen"
              description="Elke Nederlandse burger van 18+ kan stemmen op de partij die het beste bij hun ideeën past."
              color="blue"
              icon-name="vote"
            />

            <TimelineCard
              title="Zetelverdeling"
              subtitle="Stemmen worden zetels"
              description="Alle stemmen worden geteld en omgezet in zetels voor elke partij in de Tweede Kamer."
              color="green"
              icon-name="users"
            />

            <TimelineCard
              title="Coalitievorming"
              subtitle="Partijen gaan samenwerken"
              description="Partijen vormen een coalitie om een meerderheid te krijgen en samen te regeren."
              color="orange"
              icon-name="handshake"
            />
          </div>
        </div>
      </section>

      <!-- Why It Matters CTA -->
      <section class="why-section" aria-labelledby="why-title">
        <div class="container">
          <div class="cta-box">
            <div class="cta-icon" aria-hidden="true">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" role="presentation">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            
            <h2 id="why-title" class="cta-title">
              Waarom Dit Allemaal Belangrijk Is
              <IconComponent name="target" class="title-icon" aria-hidden="true" />
            </h2>
            
            <p class="cta-text">
              Jouw stem bepaalt wie er de komende 4 jaar beslissingen neemt over onderwerpen die 
              jouw leven direct beïnvloeden: studiefinanciering, klimaat, wonen, werk en nog veel meer.
            </p>

            <div class="cta-highlight-box">
              <h3 class="cta-highlight-title">Jouw stem vormt de toekomst!</h3>
              <p class="cta-highlight-text">
                Nu je weet hoe het systeem werkt, ben je klaar om een geïnformeerde keuze te maken.
              </p>
              
              <div class="cta-buttons" role="group" aria-label="Call-to-action knoppen">
                <router-link 
                  to="/results" 
                  class="btn-primary"
                  aria-label="Bekijk live verkiezingsdata en resultaten">
                  <IconComponent name="eye" class="icon-svg-small" aria-hidden="true" />
                  Bekijk Live Verkiezingsdata
                </router-link>
                <router-link 
                  to="/quiz" 
                  class="btn-secondary-dark"
                  aria-label="Start de stemwijzer filter quiz">
                  <IconComponent name="filter" class="icon-svg-small" aria-hidden="true" />
                  Doe de Filter Quiz
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </LayoutComponent>
</template>

<script setup>
import { onMounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import LayoutComponent from './LayoutComponent/LayoutComponent.vue'
import StepCard from './LearnPage/StepCard.vue'
import SeatsVisualization from './LearnPage/SeatsVisualization.vue'
import TimelineCard from './LearnPage/TimelineCard.vue'
import IconComponent from './LearnPage/IconComponent.vue'

const route = useRoute()

const scrollToLearnMore = () => {
  const element = document.getElementById('learn-more')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

const scrollToSection = async () => {
  if (!route.hash) return
  
  // Wait for DOM to be fully updated
  await nextTick()
  
  // Additional wait to ensure everything is rendered
  setTimeout(() => {
    const id = route.hash.replace('#', '')
    const element = document.getElementById(id)
    
    if (element) {
      const yOffset = -100 // Offset for fixed header
      const y = element.getBoundingClientRect().top + window.pageYOffset + yOffset
      window.scrollTo({ top: y, behavior: 'smooth' })
    }
  }, 300)
}

onMounted(() => {
  scrollToSection()
})

// Watch for hash changes (when clicking on cards while already on the page)
watch(() => route.hash, () => {
  scrollToSection()
})
</script>

<style scoped>
.learn-page {
  background-color: white;
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  min-height: 70vh;
  background: linear-gradient(135deg, #3b82f6 0%, #10b981 50%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.hero-overlay {
  width: 100%;
  padding: 64px 24px;
  position: relative;
  z-index: 10;
}

.hero-content {
  max-width: 1152px;
  margin: 0 auto;
  text-align: center;
}

.badge {
  display: inline-block;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 10px 24px;
  border-radius: 9999px;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-size: 56px;
  font-weight: 800;
  color: white;
  margin-bottom: 24px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.9);
  max-width: 896px;
  margin: 0 auto 48px;
  line-height: 1.75;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary-hero {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background-color: white;
  color: #111827;
  padding: 16px 32px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  text-decoration: none;
  transition: all 0.3s;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
}

.btn-primary-hero:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 35px -5px rgba(0, 0, 0, 0.15);
  background-color: #f9fafb;
}

.btn-secondary {
  display: inline-block;
  background-color: transparent;
  color: white;
  padding: 16px 32px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  text-decoration: none;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: white;
  transform: translateY(-2px);
}

/* Steps Section */
.steps-section {
  padding: 120px 0;
}

.container {
  max-width: 1152px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 80px;
}

.section-title {
  font-size: 36px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  width: 36px;
  height: 36px;
  color: #3b82f6;
  flex-shrink: 0;
}

.section-subtitle {
  font-size: 20px;
  color: #6b7280;
  max-width: 768px;
  margin: 0 auto;
  line-height: 1.75;
}

.steps-container {
  display: flex;
  flex-direction: column;
  gap: 128px;
}

/* Timeline Section */
.timeline-section {
  padding: 64px 0;
  background: linear-gradient(to right, rgba(59, 130, 246, 0.05), rgba(16, 185, 129, 0.05));
}

.timeline-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
  margin-bottom: 48px;
}

.timeline-next-election {
  margin-top: 48px;
  text-align: center;
}

.next-election-box {
  display: inline-flex;
  align-items: center;
  gap: 16px;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.election-clock-icon {
  width: 32px;
  height: 32px;
  color: #3b82f6;
}

.election-info {
  text-align: left;
}

.election-label {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.election-year {
  font-size: 24px;
  font-weight: 800;
  color: #3b82f6;
}

/* Why Section (CTA) */
.why-section {
  padding: 64px 0;
  background-color: white;
}

.cta-box {
  text-align: center;
  max-width: 896px;
  margin: 0 auto;
}

.cta-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #8b5cf6, #f97316);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cta-icon svg {
  width: 32px;
  height: 32px;
  color: white;
}

.cta-title {
  font-size: 36px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.cta-title .title-icon {
  width: 36px;
  height: 36px;
  color: #8b5cf6;
}

.cta-text {
  font-size: 20px;
  color: #6b7280;
  line-height: 1.75;
  margin-bottom: 32px;
}

.cta-highlight-box {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(249, 115, 22, 0.1));
  border-radius: 16px;
  padding: 48px;
  margin-bottom: 32px;
}

.cta-highlight-title {
  font-size: 24px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 16px;
}

.cta-highlight-text {
  font-size: 18px;
  color: #6b7280;
  margin-bottom: 24px;
}

.cta-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary .icon-svg-small,
.btn-secondary-dark .icon-svg-small {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  display: inline-block;
  vertical-align: middle;
}

.btn-secondary-dark {
  display: inline-flex;
  align-items: center;
  background-color: transparent;
  color: #111827;
  padding: 16px 32px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  text-decoration: none;
  border: 2px solid #e5e7eb;
  transition: all 0.3s;
}

.btn-secondary-dark:hover {
  border-color: #111827;
  background-color: #f9fafb;
  transform: translateY(-2px);
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #8b5cf6, #f97316);
  color: white;
  padding: 16px 32px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  text-decoration: none;
  transition: all 0.3s;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #7c3aed, #ea580c);
  transform: translateY(-2px);
  box-shadow: 0 20px 35px -5px rgba(0, 0, 0, 0.15);
}

.icon-svg-small {
  width: 20px;
  height: 20px;
}

/* Responsive */
@media (max-width: 1024px) {
  .section-title {
    font-size: 30px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .hero-buttons {
    flex-direction: column;
  }
  
  .btn-primary-hero,
  .btn-secondary {
    width: 100%;
  }
  
  .steps-container {
    gap: 64px;
  }
  
  .cta-highlight-box {
    padding: 32px;
  }
}
</style>
