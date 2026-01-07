<template>
  <div class="home-page">

    <layout-component>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Hero Section - Full Width -->
      <section style="padding: 80px 0; color: white; min-height: 60vh; background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);">
        <!-- Content -->
        <div style="padding: 20px 40px; display: flex; align-items: flex-start; gap: 40px; width: 100%; justify-content: space-between;">
            
            <!-- Left: Welcome & Quick Actions -->
            <div style="flex: 1;">
              <!-- Title -->
              <div>
                <h1 style="font-family: sans-serif; font-size: 48px; font-weight: bold; line-height: 1.2;">
                  Nederland Stemt!
                </h1>
                
                <!-- Description -->
                <div style="font-size: 20px; margin: 20px 0;">
                  <p>Ontdek hoe Nederland stemde, hoe jouw</p>
                  <p>regio denkt, en wat jouw generatie</p>
                  <p>belangrijk vindt. Politiek wordt pas echt</p>
                  <p>interessant als je de cijfers begrijpt! 📊</p>
                </div>
              </div>
              
              <!-- Action Button -->
              <div style="margin-top: 30px;">
                <button
                  @click="goToElections"
                  class="discover-btn"
                  style="
                    background: linear-gradient(90deg, rgba(59, 130, 246, 0.30) 0%, #8B5CF6 100%); 
                    border-radius: 10px; 
                    box-shadow: 0 2px 8px 0 rgba(139,92,246,0.10);
                    width: 55vh;
                    padding: 10px 8px;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    overflow: hidden;
                  "
                >
                  <div style="display: flex; flex-direction: column; align-items: flex-start;">
                    <div style="font-family: sans-serif; font-size: 18px; font-weight: 700; line-height: 1.1;">Ontdek Live Verkiezingsdata</div>
                    <div style="font-family: sans-serif; font-size: 14px; font-weight: 400; line-height: 1.2; margin-top: 4px;">Interactieve quiz & trends voor jongeren</div>
                  </div>
                  <div class="discover-btn-arrow">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                      <path fill="currentColor" d="M13.293 5.293a1 1 0 0 1 1.414 0l6 6a1 1 0 0 1 0 1.414l-6 6a1 1 0 0 1-1.414-1.414L16.586 13H4a1 1 0 1 1 0-2h12.586l-3.293-3.293a1 1 0 0 1 0-1.414z"/>
                    </svg>
                  </div>
                </button>
              </div>
            </div>

            <!-- Election Results Section -->
            <div style="flex: 450px; min-width: 400px; max-width: 500px; background: rgba(255, 255, 255, 0.95); border-radius: 15px; padding: 24px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px); color: #333;">
              <div style="display: flex; align-items: center; margin-bottom: 4px;">
                <svg style="width: 18px; height: 18px; margin-right: 8px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                </svg>
                <h2 style="font-size: 18px; font-weight: bold; color: #333;">Laatste Verkiezingsuitslag {{ electionYear }}</h2>
              </div>
              <p style="font-size: 13px; color: rgba(51, 51, 51, 0.7); margin-bottom: 20px;">Zetelverdeling Tweede Kamer (150 zetels totaal)</p>
              
              <!-- Loading State -->
              <div v-if="loadingResults" style="text-align: center; padding: 60px 0;">
                <p style="color: rgba(51, 51, 51, 0.7);">Loading results...</p>
              </div>

              <!-- Chart.js Bar Chart -->
              <div v-if="!loadingResults && !errorResults && chartData.length > 0" style="height: 280px; padding: 10px;">
                <Bar 
                  :data="chartData_chartjs" 
                  :options="chartOptions"
                />
              </div>

              <!-- Error State -->
              <div v-else-if="errorResults" style="text-align: center; padding: 60px 0;">
                <p style="color: #FCA5A5;">{{ errorResults }}</p>
              </div>
            </div>
        </div>
      </section>

      <!-- Dashboard Section TODO -->
       <DashboardContainer />
    </main>

    </layout-component>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import DashboardContainer from './Dashboard/DashboardContainer.vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const router = useRouter()

// Election results data
const electionResults = ref(null)
const loadingResults = ref(false)
const errorResults = ref(null)
const chartData = ref([])

// Chart scaling
const maxSeats = ref(60) // Dynamic maximum for Y-axis

// Election year (extracted from data)
const electionYear = ref("....") // Default fallback

// Tooltip state
const tooltip = ref({
  visible: false,
  x: 0,
  y: 0,
  party: '',
  seats: 0
})

const goToElections = () => {
  router.push('/elections')
}

// Chart.js configuration
const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.9)',
      titleColor: 'white',
      bodyColor: 'white',
      borderColor: 'rgba(255, 255, 255, 0.2)',
      borderWidth: 1,
      cornerRadius: 8,
      displayColors: false,
      callbacks: {
        title: function(context) {
          const item = chartData.value[context[0].dataIndex]
          return item?.fullName || context[0].label
        },
        label: function(context) {
          return `${context.parsed.y} zetels`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      max: maxSeats.value,
      ticks: {
        stepSize: Math.ceil(maxSeats.value / 4),
        color: 'rgba(51,51,51,0.7)',
        font: {
          size: 12
        }
      },
      grid: {
        color: 'rgba(51,51,51,0.12)',
        drawBorder: false
      }
    },
    x: {
      ticks: {
        color: '#333',
        font: {
          size: 12,
          weight: 500
        }
      },
      grid: {
        display: false
      }
    }
  }
}))

const chartData_chartjs = computed(() => ({
  labels: chartData.value.map(item => item.name),
  datasets: [{
    data: chartData.value.map(item => item.seats),
    backgroundColor: chartData.value.map(item => 
      item.name === 'Overig' ? '#6b7280' : (item.color || '#3b82f6')
    ),
    borderRadius: 3,
    borderSkipped: false,
  }]
}))


// Function to shorten party names for chart display
const shortenPartyName = (name) => {
  if (!name) return name
  
  // Common party name mappings
  const shortNames = {
    'PVV (Partij voor de Vrijheid)': 'PVV',
    'GROENLINKS / Partij van de Arbeid (PvdA)': 'GL/PvdA',
    'Nieuw Sociaal Contract': 'NSC',
    'Partij voor de Vrijheid': 'PVV',
    'Volkspartij voor Vrijheid en Democratie': 'VVD',
    'Democraten 66': 'D66',
    'Christen-Democratisch Appèl': 'CDA',
    'Socialistische Partij': 'SP',
    'Partij van de Arbeid': 'PvdA',
    'GroenLinks': 'GL',
    'ChristenUnie': 'CU',
    'Partij voor de Dieren': 'PvdD',
    'Staatkundig Gereformeerde Partij': 'SGP',
    'Forum voor Democratie': 'FvD',
    'JA21': 'JA21',
    'Volt Nederland': 'Volt',
    'Bij1': 'BIJ1',
    'DENK': 'DENK',
    '50PLUS': '50+',
    'BBB': 'BBB',
    'BoerBurgerBeweging': 'BBB'
  }
  
  // Check for exact match first
  if (shortNames[name]) {
    return shortNames[name]
  }
  
  // If name is longer than 8 characters, try to extract abbreviation
  if (name.length > 8) {
    // Look for abbreviation in parentheses
    const abbrevMatch = name.match(/\(([A-Z0-9+]{2,6})\)/)
    if (abbrevMatch) {
      return abbrevMatch[1]
    }
    
    // Look for uppercase letters to create abbreviation
    const upperCaseLetters = name.match(/[A-Z]/g)
    if (upperCaseLetters && upperCaseLetters.length >= 2) {
      return upperCaseLetters.slice(0, 4).join('')
    }
    
    // Fallback: take first 6 characters
    return name.substring(0, 6) + '...'
  }
  
  return name
}

// Process election data for chart (top 5 + others)
const processChartData = (electionData) => {
  if (!electionData?.parties) return []
  
  // Sort parties by seats (descending)
  const sortedParties = [...electionData.parties]
    .filter(party => party.seats > 0)
    .sort((a, b) => b.seats - a.seats)
  
  // Get top 5 parties
  const top5 = sortedParties.slice(0, 5)
  
  // Calculate "Overig" (others) total
  const others = sortedParties.slice(5)
  const othersTotal = others.reduce((sum, party) => sum + party.seats, 0)
  
  // Create chart data
  const data = top5.map(party => ({
    name: shortenPartyName(party.name || party.id),
    fullName: party.name || party.id,
    seats: party.seats,
    color: party.color // Add color from backend
  }))
  
  // Add "Overig" if there are other parties
  if (othersTotal > 0) {
    data.push({
      name: 'Overig',
      fullName: `Overig (${others.length} partijen)`,
      seats: othersTotal,
      color: '#6b7280' // Gray for "Overig"
    })
  }
  
  return data
}


// Fetch election results for homepage chart
const fetchElectionResults = async () => {
  loadingResults.value = true
  errorResults.value = null
  
  try {
    // Use TK2023 as the default election ID
    const electionId = 'TK2023'
    
    // Fetch parties from database for the specific election
    const partiesResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/parties/homepage?electionId=${electionId}`)

    if (!partiesResponse.ok) {
      throw new Error(`HTTP error! status: ${partiesResponse.status}`)
    }

    const parties = await partiesResponse.json()

    // Fetch election metadata from database
    const metadataResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/elections/metadata/${electionId}`)
    
    let electionDate = null // fallback
    let electionName = null // fallback
    
    if (metadataResponse.ok) {
      const metadata = await metadataResponse.json()
      electionDate = metadata.date
      electionName = metadata.name
      
      // Extract year from date
      if (metadata.date) {
        const dateObj = new Date(metadata.date)
        electionYear.value = dateObj.getFullYear().toString()
      }
    }
    
    // Transform party data to match expected format
    const transformedData = {
      parties: parties,
      date: electionDate,
      id: 'TK2023',
      name: electionName
    }
    
    electionResults.value = transformedData
    chartData.value = processChartData(transformedData)

  } catch (err) {
    console.error('Error fetching party data:', err)
    errorResults.value = err.message
  } finally {
    loadingResults.value = false
  }
}

// Fetch data when component mounts
onMounted(() => {
  fetchElectionResults()
})
</script>

<style scoped>
/* Global Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home-page {
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

/* Navigation en Footer styling is verplaatst naar aparte components */

/* Main Content */
.main-content {
  flex: 1;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6rem 0;
  text-align: center;
}

.hero-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.hero-description {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

/* Buttons */
.btn {
  padding: 0.8rem 2rem;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: #ff6b6b;
  color: white;
}

.btn-primary:hover {
  background: #ee5a5a;
  transform: translateY(-2px);
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background: white;
  color: #667eea;
}

.btn-outline {
  background: transparent;
  color: #2a5298;
  border: 2px solid #2a5298;
}

.btn-outline:hover {
  background: #2a5298;
  color: white;
}

/* Stats Section */
.stats-section {
  padding: 4rem 0;
  background: #f8f9fa;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card h3 {
  font-size: 2.5rem;
  color: #2a5298;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.stat-card p {
  color: #666;
  font-size: 1.1rem;
}

/* Recent Elections Section */
.recent-elections {
  padding: 4rem 0;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.recent-elections h2 {
  text-align: center;
  margin-bottom: 3rem;
  font-size: 2.5rem;
  color: #333;
}

/* --- Discover Button Hover Animation --- */
.discover-btn {
  transition: transform 0.32s cubic-bezier(.22,1,.36,1), box-shadow 0.32s cubic-bezier(.22,1,.36,1), background 0.32s;
  will-change: transform, box-shadow;
  position: relative;
}
.discover-btn:hover {
  transform: translateY(-2.5px) scale(1.018);
  box-shadow: 0 8px 28px 0 rgba(139,92,246,0.13), 0 2px 8px 0 rgba(59,130,246,0.10);
  background: linear-gradient(90deg, #8B5CF6 0%, #3B82F6 100%);
}
.discover-btn-arrow {
  display: flex;
  align-items: center;
  margin-left: 20px;
  transition: transform 0.38s cubic-bezier(.22,1,.36,1), filter 0.38s cubic-bezier(.22,1,.36,1);
}
.discover-btn:hover .discover-btn-arrow {
  transform: translateX(5px) scale(1.07);
  filter: drop-shadow(0 0 4px #8B5CF6);
}
.discover-btn:active {
  transform: scale(0.985);
  box-shadow: 0 2px 8px 0 rgba(139,92,246,0.10);
}
</style>