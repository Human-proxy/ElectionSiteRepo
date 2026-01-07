<template>
  <layout-component>
    <div class="quiz-page">
      
      <!-- Hero section: intro text and headline -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">Verkiezingsquiz</h1>
          <p class="hero-subtitle">
            Beantwoord 4 vragen en ontdek direct jouw politieke inzichten op maat.
          </p>
        </div>
      </section>

      <div class="content-section">
        <div class="quiz-container">
          
          <!-- Global state wrapper: loading / error / result / question -->
          <transition name="fade" mode="out-in">
            <!-- Loading state -->
            <div v-if="loading" class="state-card loading-state" key="loading">
              <div class="spinner-wrapper">
                <div class="spinner-gradient"></div>
              </div>
              <p>Data ophalen...</p>
            </div>

            <!-- Error state -->
            <div v-else-if="error" class="state-card error-state" key="error">
              <div class="icon-bg error">
                <v-icon icon="mdi-alert-circle-outline" size="48" color="white"></v-icon>
              </div>
              <h3>Oeps!</h3>
              <p>{{ error }}</p>
              <button @click="initQuiz" class="btn-retry">Opnieuw proberen</button>
            </div>

            <!-- Result state -->
            <div v-else-if="result" class="state-card result-card" key="result">
              <div class="confetti-container">
                <div v-for="n in 10" :key="n" class="confetti"></div>
              </div>

              <div class="result-header">
                <span class="result-subtitle">Jouw Resultaat voor {{ answers['q1'] }}</span>
                <h2 class="party-title">{{ result.partyName }}</h2>
                <div class="region-badge">
                  <v-icon icon="mdi-map-marker" size="14" start></v-icon>
                  {{ result.regionName }}
                </div>
              </div>

              <div class="result-visualizer">
                <!-- Seat visualization: half doughnut chart -->
                <div v-if="answers['q4'] === 'SEATS'" class="chart-wrapper">
                  <Doughnut :data="seatChartData" :options="seatChartOptions" />
                  <div class="chart-center-text">
                    <span class="big-number">{{ animatedNumber }}</span>
                    <span class="unit">Zetels</span>
                  </div>
                </div>

                <!-- Percentage visualization: circular progress -->
                <div v-else-if="answers['q4'] === 'PERCENTAGE'" class="circle-wrapper">
                  <v-progress-circular
                    :model-value="animatedNumber"
                    :size="200"
                    :width="20"
                    color="#8B5CF6"
                    bg-color="#e2e8f0"
                  >
                    <div class="circle-content">
                      <span class="big-number">{{ animatedNumber }}%</span>
                      <span class="unit">van de stemmen</span>
                    </div>
                  </v-progress-circular>
                </div>

                <!-- Votes visualization: card + bar -->
                <div v-else class="votes-wrapper">
                  <div class="vote-card">
                    <div class="vote-icon">🗳️</div>
                    <div class="vote-content">
                      <span class="label">Aantal Stemmen</span>
                      <span class="big-number-text">{{ result.formattedValue }}</span>
                    </div>
                  </div>
                  <div class="vote-bar-bg">
                    <div class="vote-bar-fill"></div>
                  </div>
                </div>
              </div>

              <div class="result-narrative">
                <p>"{{ result.narrative }}"</p>
              </div>

              <!-- result actions with edit + export -->
              <div class="result-actions-grid">
                <button @click="editAnswers" class="btn-secondary-action">
                  <v-icon icon="mdi-pencil" size="18" start></v-icon> Pas aan
                </button>
                
                <button @click="downloadExport" class="btn-secondary-action" :disabled="exporting">
                  <v-icon
                    :icon="exporting ? 'mdi-loading' : 'mdi-download'"
                    :class="{ 'spin': exporting }"
                    size="18"
                    start
                  ></v-icon>
                  {{ exporting ? 'Downloaden...' : 'Export CSV' }}
                </button>

                <button @click="resetQuiz" class="btn-primary-action full-width">
                  Nieuwe Quiz
                </button>
              </div>
            </div>

            <!-- Question state -->
            <div v-else-if="currentQuestion" class="state-card question-card" key="question">
              <div class="question-header">
                <span class="step-count">Vraag {{ currentStep + 1 }} / {{ questions.length }}</span>
                <div class="progress-track">
                  <div class="progress-bar" :style="{ width: `${progressPercentage}%` }"></div>
                </div>
              </div>

              <transition name="slide-fade" mode="out-in">
                <div :key="currentQuestion.id" class="question-content">
                  <h2 class="question-text">{{ currentQuestion.text }}</h2>

                  <!-- Plain select style (buttons) -->
                  <div v-if="currentQuestion.type === 'SELECT'" class="input-area">
                    <div class="select-grid">
                      <button 
                        v-for="opt in currentQuestion.options" 
                        :key="opt.value"
                        class="option-btn"
                        :class="{ active: answers[currentQuestion.id] === opt.value }"
                        @click="selectOption(opt.value)"
                      >
                        {{ opt.label }}
                      </button>
                    </div>
                  </div>

                  <!-- Searchable select (Vuetify autocomplete) -->
                  <div v-else-if="currentQuestion.type === 'SEARCHABLE_SELECT'" class="input-area">
                    <v-autocomplete
                      v-model="answers[currentQuestion.id]"
                      :items="currentQuestion.options"
                      item-title="label"
                      item-value="value"
                      variant="outlined"
                      class="creative-input"
                      placeholder="Maak uw keuze..."
                      :menu-props="{ maxHeight: 300 }"
                      auto-select-first
                    ></v-autocomplete>
                  </div>

                  <!-- Card-style options (data types) -->
                  <div v-else-if="currentQuestion.type === 'CARDS'" class="cards-grid">
                    <div 
                      v-for="opt in currentQuestion.options" 
                      :key="opt.value"
                      class="visual-card"
                      :class="{ selected: answers[currentQuestion.id] === opt.value }"
                      @click="selectOption(opt.value)"
                    >
                      <div class="card-icon">
                        {{ getIconForType(opt.value) }}
                      </div>
                      <div class="card-info">
                        <h3>{{ opt.label }}</h3>
                        <p>{{ opt.description }}</p>
                      </div>
                      <div class="check-circle">
                        <v-icon icon="mdi-check" size="16"></v-icon>
                      </div>
                    </div>
                  </div>
                </div>
              </transition>

              <!-- Navigation buttons -->
              <div class="quiz-footer">
                <button 
                  v-if="currentStep > 0" 
                  @click="prevStep" 
                  class="btn-back"
                >
                  <v-icon icon="mdi-arrow-left" start></v-icon> Terug
                </button>
                <div class="spacer"></div>
                <button 
                  @click="nextStep" 
                  class="btn-next"
                  :disabled="!canProceed"
                >
                  {{ isLastQuestion ? 'Resultaat Tonen' : 'Volgende Vraag' }}
                </button>
              </div>
            </div>
          </transition>
        </div>
      </div>
      <div class="sideCommunity" v-if="result">
        <ForumQuiz :result="result"></ForumQuiz>
      </div>
    </div>
  </layout-component>
</template>

<script setup>
/**
 * QuizPage.vue
 *
 * Interactive election quiz that:
 * - Fetches dynamic questions from the backend (year-dependent)
 * - Guides user through 4 steps
 * - Visualizes the result with creative charts / cards
 * - Allows exporting the result and tweaking answers afterwards
 */

import { ref, computed, onMounted, watch } from 'vue';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'vue-chartjs';
import LayoutComponent from '@/components/LayoutComponent/LayoutComponent.vue';
import { QuizService } from '@/services/quizService';
import ForumQuiz from "@/components/Forum/ForumQuiz.vue";

// Register ChartJS components once for this component
ChartJS.register(ArcElement, Tooltip, Legend);

/* ----------------------------------------------------------------
 * Reactive state
 * ---------------------------------------------------------------- */

// List of questions as returned by the backend
const questions = ref([]);

// Key-value object that stores answers by question ID, e.g. { q1: "2023", q2: 42, ... }
const answers = ref({});

// Pointer to the current question index (0-based)
const currentStep = ref(0);

// Global UI flags
const loading = ref(true);
const error = ref(null);

// Export loading flag for CSV export
const exporting = ref(false);

// Final quiz result returned by the backend
const result = ref(null);

// Animated numeric value used in visualizations (percentage / seats)
const animatedNumber = ref(0);

/* ----------------------------------------------------------------
 * Computed properties
 * ---------------------------------------------------------------- */

/**
 * Currently active question based on the current step.
 */
const currentQuestion = computed(() => questions.value[currentStep.value]);

/**
 * Percentage (0–100) for the progress bar in the quiz header.
 */
const progressPercentage = computed(() =>
  questions.value.length > 0
    ? ((currentStep.value + 1) / questions.value.length) * 100
    : 0
);

/**
 * True when the user is on the last question.
 */
const isLastQuestion = computed(
  () => questions.value.length > 0 && currentStep.value === questions.value.length - 1
);

/**
 * Guard that controls if the "next" button is enabled.
 * Only allows progress if the current question has an answer.
 */
const canProceed = computed(() => {
  if (!currentQuestion.value) return false;
  return !!answers.value[currentQuestion.value.id];
});

/* ----------------------------------------------------------------
 * Chart configuration (seats view)
 * ---------------------------------------------------------------- */

/**
 * Doughnut chart data showing seat distribution:
 * - one slice for selected party
 * - one slice for "other seats"
 */
const seatChartData = computed(() => {
  if (!result.value) return { labels: [], datasets: [] };
  
  // `formattedValue` is expected in style "35 zetels"
  const seats = parseInt(result.value.formattedValue); 
  const totalSeats = 150;
  const remaining = totalSeats - seats;

  return {
    labels: [result.value.partyName, 'Andere zetels'],
    datasets: [{
      backgroundColor: ['#8B5CF6', '#e2e8f0'],
      borderWidth: 0,
      data: [seats, remaining]
    }]
  };
});

/**
 * Static chart options for the half-doughnut visualization.
 */
const seatChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  rotation: -90,      // Start at the bottom
  circumference: 180, // Only half a circle
  cutout: '75%',      // Donut style
  plugins: {
    legend: { display: false },
    tooltip: { enabled: false }
  }
};

/* ----------------------------------------------------------------
 * Helper utilities
 * ---------------------------------------------------------------- */

/**
 * Returns an emoji icon per data type for the cards in question 4.
 *
 * @param {string} type - One of "SEATS", "VOTES", "PERCENTAGE"
 * @returns {string} emoji
 */
const getIconForType = (type) => {
  switch(type) {
    case 'SEATS': return '💺';
    case 'VOTES': return '🗳️';
    case 'PERCENTAGE': return '📊';
    default: return '🔹';
  }
};

/**
 * Maps internal question IDs (q1..q4) to DTO field names
 * used by the backend in QuizRequestDTO.
 */
const answerMapping = { q1: 'year', q2: 'partyId', q3: 'region', q4: 'dataType' };

/* ----------------------------------------------------------------
 * Core logic
 * ---------------------------------------------------------------- */

/**
 * Initializes or resets the quiz flow.
 * - Resets UI state and answers
 * - Loads questions for a default year (2023)
 * - Preselects 2023 as answer for q1 to keep UX smooth
 */
const initQuiz = async () => {
  loading.value = true;
  error.value = null;
  result.value = null;
  currentStep.value = 0;
  answers.value = {};
  
  try {
    questions.value = await QuizService.getQuestions('2023');
    // Pre-fill the first question to remove "select a year" friction
    answers.value['q1'] = '2023'; 
  } catch (err) {
    error.value = "Kon de quiz niet laden. Probeer het later opnieuw.";
  } finally {
    loading.value = false;
  }
};

/**
 * Writes the selected value into the current question's answer,
 * and automatically advances to the next step for click-based questions.
 *
 * @param {*} val - Answer value (string, number, etc.)
 */
const selectOption = (val) => {
  answers.value[currentQuestion.value.id] = val;

  // For button/card questions we auto-advance after a short delay
  if (currentQuestion.value.type === 'SELECT' || currentQuestion.value.type === 'CARDS') {
    setTimeout(() => nextStep(), 300);
  }
};

/**
 * Handles side-effects when an answer changes.
 * Currently only reacts to Q1 (year):
 * - Re-fetches questions for the selected year
 * - Clears dependent answers (party, region, data type)
 */
const handleAnswerChange = async () => {
  const qId = currentQuestion.value.id;
  if (qId === 'q1') {
    loading.value = true;
    try {
      const selectedYear = answers.value['q1'];
      const newConfig = await QuizService.getQuestions(selectedYear);
      questions.value = newConfig;

      // Reset answers that depend on the year
      delete answers.value['q2'];
      delete answers.value['q3'];
      delete answers.value['q4'];
    } catch (err) {
      error.value = "Fout bij ophalen jaar data.";
    } finally {
      loading.value = false;
    }
  }
};

/**
 * Watcher: whenever the current question's answer changes,
 * we optionally trigger `handleAnswerChange` (currently for q1).
 */
watch(
  () => answers.value[currentQuestion.value?.id],
  () => {
    if (currentQuestion.value?.id === 'q1') handleAnswerChange();
  }
);

/**
 * Moves the quiz to the next step.
 * If the current step is the last one, it triggers the submit flow.
 */
const nextStep = async () => {
  if (isLastQuestion.value) {
    await submitQuiz();
  } else {
    currentStep.value++;
  }
};

/**
 * Moves the quiz one step back, if possible.
 */
const prevStep = () => {
  if (currentStep.value > 0) currentStep.value--;
};

/**
 * Builds the payload expected by the backend from the local `answers` object
 * and calls the QuizService to compute the result.
 * Also starts the animation for visual numbers.
 */
const submitQuiz = async () => {
  loading.value = true;
  const payload = {};

  // Map answers (q1..q4) to DTO fields (year, partyId, region, dataType)
  Object.keys(answers.value).forEach(key => {
    const dtoKey = answerMapping[key];
    if (dtoKey) payload[dtoKey] = answers.value[key];
  });

  try {
    const data = await QuizService.getResult(payload);
    result.value = data;
    animateResultValue(data);
  } catch (err) {
    if (err.response && err.response.status === 404) {
      error.value = "Geen data gevonden voor deze combinatie.";
    } else {
      error.value = "Er ging iets mis bij het berekenen.";
    }
  } finally {
    loading.value = false;
  }
};

/**
 * Animates `animatedNumber` from 0 to the numeric part of `formattedValue`.
 * Supports:
 * - percentages (e.g. "12,3%")
 * - seat count (e.g. "25 zetels")
 *
 * @param {Object} data - QuizResultDTO from the backend
 */
const animateResultValue = (data) => {
  let target = 0;

  if (data.formattedValue.includes('%')) {
    // Convert "12,3%" → 12.3 as number
    target = parseFloat(data.formattedValue.replace('%', '').replace(',', '.'));
  } else if (data.formattedValue.includes('zetels')) {
    // Extract integer from "35 zetels"
    target = parseInt(data.formattedValue);
  } else {
    // Other types currently do not use animatedNumber (e.g. raw votes)
    target = 0; 
  }

  let start = 0;
  const duration = 1500; // animation duration in ms
  const startTime = performance.now();

  /**
   * Eased animation loop using requestAnimationFrame.
   */
  const animate = (currentTime) => {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);

    // Ease-out curve for a smoother finish
    const ease = 1 - Math.pow(1 - progress, 4);
    
    animatedNumber.value = Math.floor(start + (target - start) * ease);

    if (progress < 1) {
      requestAnimationFrame(animate);
    } else {
      // On completion: keep decimals for percentages, plain integer for seats
      animatedNumber.value = data.formattedValue.includes('%')
        ? target.toFixed(1)
        : target;
    }
  };
  
  if (target > 0) requestAnimationFrame(animate);
};

/* ----------------------------------------------------------------
 * Result actions (edit + export + reset)
 * ---------------------------------------------------------------- */

/**
 * Public reset handler bound to the "Nieuwe Quiz" button.
 */
const resetQuiz = () => initQuiz();

/**
 * Allows the user to go back into the quiz and adjust their answers.
 *
 * - Hide the result
 * - Jump back to the last question (data type)
 *   so they can directly tweak the view or go back further.
 */
const editAnswers = () => {
  result.value = null;
  if (questions.value.length > 0) {
    currentStep.value = questions.value.length - 1;
  }
};

/**
 * Triggers CSV export via QuizService.
 * Uses same payload mapping as `submitQuiz` but relies on
 * the service to handle file download / error handling.
 */
const downloadExport = async () => {
  exporting.value = true;
  const payload = {};

  Object.keys(answers.value).forEach(key => {
    const dtoKey = answerMapping[key];
    if (dtoKey) payload[dtoKey] = answers.value[key];
  });

  try {
    await QuizService.exportResult(payload);
  } catch (e) {
    // Error is already logged / surfaced in the service; reset UI state
  } finally {
    exporting.value = false;
  }
};

/**
 * On mount, start the initial quiz load.
 */
onMounted(() => initQuiz());
</script>

<style scoped>
.hidden {
  display: none;
}
/* --- Hero Section (Restored Gradient) --- */
.hero-section {
  background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
  color: white;
  padding: 80px 0 100px; /* Extra bottom padding for overlap */
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-title {
  font-family: sans-serif;
  font-size: 48px;
  font-weight: bold;
  line-height: 1.2;
  margin-bottom: 20px;
}

.hero-subtitle {
  font-size: 20px;
  line-height: 1.5;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

/* --- Content Section --- */
.content-section {
  background: #f8fafc;
  min-height: 60vh;
  padding-bottom: 60px;
  display: flex;
  justify-content: center;
}

/* --- Main Quiz Container (Overlapping) --- */
.quiz-container {
  width: 100%;
  max-width: 650px;
  margin-top: -60px; /* Overlap effect */
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

/* --- State Cards (Glassmorphism/Clean) --- */
.state-card {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.05),
    0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

/* --- Loading & Error --- */
.loading-state, .error-state {
  text-align: center;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.spinner-gradient {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 4px solid transparent;
  border-top-color: #3B82F6;
  border-right-color: #8B5CF6;
  animation: spin 1s linear infinite;
}

.icon-bg.error {
  background: #EF4444;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 10px 20px rgba(239, 68, 68, 0.3);
}

/* --- Result Card --- */
.result-header {
  text-align: center;
  margin-bottom: 30px;
}
.result-subtitle {
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 1.5px;
  color: #6B7280;
  font-weight: 700;
}
.party-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6);
  background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 5px 0 10px;
}
.region-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #EFF6FF;
  color: #3B82F6;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.result-visualizer {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  margin-bottom: 30px;
}

/* Chart Styling */
.chart-wrapper {
  position: relative;
  width: 300px;
  height: 180px;
}
.chart-center-text {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
}
.circle-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1;
}

/* Votes Styling */
.votes-wrapper {
  width: 100%;
  text-align: center;
}
.vote-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.05);
  display: inline-flex;
  align-items: center;
  gap: 20px;
  border: 1px solid #e5e7eb;
  margin-bottom: 20px;
}
.vote-icon { font-size: 40px; }
.vote-content { display: flex; flex-direction: column; text-align: left; }
.vote-content .label { font-size: 12px; color: #9CA3AF; font-weight: 600; text-transform: uppercase; }
.big-number-text { font-size: 32px; font-weight: 800; color: #1F2937; }

.vote-bar-bg { height: 8px; width: 100%; background: #e5e7eb; border-radius: 4px; overflow: hidden; }
.vote-bar-fill { height: 100%; width: 0; background: linear-gradient(90deg, #3B82F6, #8B5CF6); animation: fillBar 1.5s ease-out forwards; width: 80%; }

@keyframes fillBar { from { width: 0; } to { width: 80%; } }

.big-number { font-size: 42px; font-weight: 900; color: #1F2937; }
.unit { font-size: 14px; color: #6B7280; font-weight: 600; display: block; margin-top: 5px; }

.result-narrative {
  text-align: center;
  font-size: 18px;
  color: #4B5563;
  font-style: italic;
  margin-bottom: 40px;
  padding: 0 20px;
}

/* --- Result Actions (Grid Layout) --- */
.result-actions-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 20px;
}

.full-width {
  grid-column: span 2;
}

.btn-secondary-action {
  background: white;
  border: 2px solid #E5E7EB;
  color: #4B5563;
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-secondary-action:hover {
  border-color: #3B82F6;
  color: #3B82F6;
  background: #EFF6FF;
}

.btn-secondary-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary-action {
  background: linear-gradient(90deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  transition: transform 0.2s;
}

.btn-primary-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(59, 130, 246, 0.4);
}

.spin {
  animation: spin 1s linear infinite;
}

/* --- Question Card --- */
.question-header {
  margin-bottom: 30px;
}
.step-count {
  font-size: 12px;
  font-weight: 700;
  color: #9CA3AF;
  text-transform: uppercase;
  display: block;
  margin-bottom: 8px;
}
.progress-track {
  height: 6px;
  background: #E5E7EB;
  border-radius: 3px;
  overflow: hidden;
}
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6);
  border-radius: 3px;
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.question-text {
  font-size: 28px;
  color: #1F2937;
  font-weight: 700;
  margin-bottom: 30px;
}

/* Option Buttons (Select) */
.select-grid {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.option-btn {
  flex: 1;
  padding: 16px;
  border: 2px solid #E5E7EB;
  border-radius: 12px;
  background: white;
  font-weight: 600;
  color: #4B5563;
  cursor: pointer;
  transition: all 0.2s;
}
.option-btn:hover { border-color: #8B5CF6; color: #8B5CF6; }
.option-btn.active {
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

/* Visual Cards */
.cards-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}
.visual-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px solid #E5E7EB;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}
.card-icon { font-size: 28px; background: #F3F4F6; width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; border-radius: 12px; }
.card-info h3 { margin: 0 0 4px; font-size: 16px; color: #1F2937; }
.card-info p { margin: 0; font-size: 13px; color: #6B7280; }
.check-circle { position: absolute; right: 20px; top: 50%; transform: translateY(-50%); width: 24px; height: 24px; border-radius: 50%; border: 2px solid #E5E7EB; display: flex; align-items: center; justify-content: center; color: white; opacity: 0; transition: all 0.2s; }

.visual-card:hover { border-color: #8B5CF6; background: #F9FAFB; }
.visual-card.selected { border-color: #8B5CF6; background: #F5F3FF; }
.visual-card.selected .check-circle { background: #8B5CF6; border-color: #8B5CF6; opacity: 1; }

/* Navigation Buttons */
.quiz-footer { display: flex; margin-top: 40px; }
.spacer { flex: 1; }
.btn-next {
  background: linear-gradient(90deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  padding: 14px 32px;
  border-radius: 12px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-left: 10px;
}
.btn-next:disabled { opacity: 0.5; cursor: not-allowed; background: #E5E7EB; color: #9CA3AF; }
.btn-next:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(139, 92, 246, 0.25); }

.btn-back { background: transparent; border: none; color: #9CA3AF; font-weight: 600; cursor: pointer; display: flex; align-items: center; }
.btn-back:hover { color: #4B5563; }

.btn-ghost { background: white; border: 2px solid #E5E7EB; color: #4B5563; width: 100%; border-radius: 12px; font-weight: 600; padding: 14px; cursor: pointer; }
.btn-ghost:hover { border-color: #3B82F6; color: #3B82F6; }

/* --- Transitions --- */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.slide-fade-enter-active { transition: all 0.4s ease-out; }
.slide-fade-leave-active { transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from { transform: translateX(20px); opacity: 0; }
.slide-fade-leave-to { transform: translateX(-20px); opacity: 0; }

/* Confetti */
.confetti-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; overflow: hidden; pointer-events: none; z-index: 0; }
.confetti { position: absolute; width: 10px; height: 10px; background: #ffd700; animation: fall 3s linear infinite; opacity: 0; }
.confetti:nth-child(odd) { background: #8B5CF6; }
.confetti:nth-child(even) { background: #3B82F6; }
@keyframes fall { 0% { top: -10%; opacity: 1; transform: rotate(0deg); } 100% { top: 110%; opacity: 0; transform: rotate(360deg); } }
.confetti:nth-child(1) { left: 10%; animation-delay: 0s; }
.confetti:nth-child(2) { left: 30%; animation-delay: 0.5s; }
.confetti:nth-child(3) { left: 50%; animation-delay: 1.2s; }
.confetti:nth-child(4) { left: 70%; animation-delay: 0.2s; }
.confetti:nth-child(5) { left: 90%; animation-delay: 0.8s; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>
