<template>
  <layout-component>
    <div class="election-results">
      <!-- Hero Section with Gradient Background -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">
            Verkiezingsresultaten 2023
          </h1>
          <p class="hero-subtitle">
            Bekijk de officiële uitslag van de Nederlandse Tweede Kamerverkiezingen
          </p>
        </div>
      </section>

      <!-- Main Content Section -->
      <section class="content-section">
        <div class="container">
          <!-- Loading State -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>Loading election results...</p>
          </div>

          <!-- Error State -->
          <div v-else-if="error" class="error-state">
            <div class="error-message">
              <h3>Error loading results</h3>
              <p>{{ error }}</p>
            </div>
          </div>

          <!-- KPI Overview Cards -->
          <div v-else-if="electionData && electionData.parties && electionData.parties.length > 0" class="results-container">
            <div class="kpi-section">
              <h2 class="kpi-section-title">📊 Verkiezingen in een Oogopslag</h2>
              <div class="kpi-grid">
                <div class="kpi-card winner">
                  <div class="kpi-icon">🏆</div>
                  <div class="kpi-content">
                    <div class="kpi-description">
                    <h3>Grootste Partij</h3>
                    <IconWithInfoComponent title="" context="De grootste partij is de partij die in totaal de meeste stemmen heeft ontvangen tijdens deze verkiezingen. Dit betekent dat zij het meeste aantal zetels hebben en daardoor de meeste invloed heeft bij het maken van wetten en het samenstellen van een coalitie." />
                    </div>
                    <p class="kpi-value">{{ winningParty?.name || 'Laden...' }}</p>
                    <p class="kpi-detail">{{ winningParty?.seats || 0 }} zetels</p>
                  </div>
                </div>
                
                <div class="kpi-card votes">
                  <div class="kpi-icon">🗳️</div>
                  <div class="kpi-content">
                    <div class="kpi-description">
                    <h3>Totaal Stemmen</h3>
                    <IconWithInfoComponent title="" context="Totaal stemmen in het land betekent het aantal mensen dat in heel Nederland heeft gestemd. Al deze stemmen samen bepalen hoe groot elke partij wordt en hoeveel invloed ze krijgt in de landelijke politiek." />
                    </div>
                    <p class="kpi-value">{{ formatNumber(totalVotes) }}</p>
                    <p class="kpi-detail">Alle partijen samen</p>
                  </div>
                </div>
                
                <div class="kpi-card parties">
                  <div class="kpi-icon">🎯</div>
                  <div class="kpi-content">
                    <div class="kpi-description">
                    <h3>Partijen in Kamer</h3>
                    <IconWithInfoComponent title="" context="De partijen in de Kamer zijn de politieke partijen die genoeg stemmen hebben gekregen om een of meer zetels te krijgen. Hun vertegenwoordigers vormen samen de Tweede Kamer, waar zij discussiëren, wetten maken en het kabinet controleren." />
                    </div>
                    <p class="kpi-value">{{ electedPartiesCount }}</p>
                    <p class="kpi-detail">van {{ totalPartiesCount }} partijen</p>
                  </div>
                </div>
                
                <div class="kpi-card majority">
                  <div class="kpi-icon">🤝</div>
                  <div class="kpi-content">
                    <div class="kpi-description">
                    <h3>Meerderheid Grens</h3>
                    <IconWithInfoComponent title="" context="De meerderheidgrens is het aantal zetels dat een partij of coalitie minimaal nodig heeft om de meerderheid in de tweede Kamer te vormen. Met een meerderheid kan een partij makkelijker wetten maken, omdat ze dan meer dan de helft van de stemmen hebben binnen de tweede kamer." />
                    </div>
                    <p class="kpi-value">76 zetels</p>
                    <p class="kpi-detail">nodig van 150</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Main Results Section -->
            <div class="results-card">
              <h2 class="results-title">
                <svg style="width: 24px; height: 24px; margin-right: 12px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                </svg>
                Verkiezingsresultaten - Tweede Kamer 2023
              </h2>
              <p class="results-subtitle">150 zetels totaal - Gesorteerd op aantal stemmen (hoogste eerst)</p>
              
              <!-- Single Scrollable Table with Percentage Background -->
              <div class="single-table-container">
                <div class="table-header">
                  <h3>�️ Verkiezingsresultaten - Alle Partijen</h3>
                  <p>{{ partyData.length }} partijen • Totaal {{ formatNumber(totalVotes) }} stemmen</p>
                </div>
                <div class="scrollable-table">
                  <table class="results-table">
                    <thead>
                      <tr>
                        <th class="rank-col"> <div class="containerIcon"> #
                          <IconWithInfoComponent title="Rang" context="Hier zie je op welke plek een partij staat, van groot naar klein. De partijen met de meeste stemmen staat bovenaan." />
                        </div>
                        </th>
                        <th class="party-col"> <div class="containerIcon">Partij
                          <IconWithInfoComponent title="Partij" context="Hieronder staan de politieke partijen waarop mensen konden stemmen. Elke partij heeft haar eigen ideeën over hoe het land bestuurd moet worden." />
                        </div>
                        </th>
                        <th class="votes-col"> <div class="containerIcon"> Stemmen
                          <IconWithInfoComponent title="Stemmen" context="Vermeldt het exacte aantal stemmen dat elke partij heeft behaald tijdens de verkiezing. Deze cijfers vormen de basis voor het berekenen van zowel de percentages als de uiteindelijke zetelverdeling." />
                        </div>
                        </th>
                        <th class="percentage-col"><div class="containerIcon">%
                          <IconWithInfoComponent title="Percentage" context="Hier zie je welk deel van alle stemmen naar een bepaalde partij is gegaan. Bijvoorbeeld: als een partij 25% heeft, betekent dat dat een op de vier mensen op die partij heeft gestemd." />
                        </div>
                        </th>
                        <th class="seats-col"> <div class="containerIcon">
                            Zetels
                          <IconWithInfoComponent title="Zetels" context="Een zetel is een plek in de tweede kamer die een partij krijgt na de verkiezing. Elke zetel vertegenwoordigt een politicus die namens die partij meebeslist over wetten en beleid. Het aantal zetels dat een partij krijgt, hangt af van hoeveel stemmen ze heeft gekregen." />
                          </div>
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(party, index) in partyData" :key="party.id" 
                          class="table-row"
                          :style="{ 
                            '--vote-percentage': `${getVotePercentage(party.votes)}%`,
                            '--party-color': getPartyColor(index)
                          }">
                        <td class="party-name">{{ party.partyName }}</td>
                        <td class="vote-count">{{ formatNumber(party.votes) }}</td>
                        <td class="percentage">{{ getVotePercentage(party.votes) }}%</td>
                        <td class="seat-count">{{ formatNumber(party.seats) }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              
              <!-- Simple Bar Chart -->
              <div class="bar-chart-section">
                <div class="bar-section-header">
                <h3 class="bar-chart-title">Top 8 Partijen - Stemverdeling </h3>
                <IconWithInfoComponent title="Stem verdeling" context="Hier onder zie je de 8 partijen met het percentage van de kiezers die de partijen hebben gekregen" />
                </div>
                  <div class="simple-bars">
                  <div v-for="(party, index) in top8Parties" :key="party.id" class="simple-bar-item">
                    <div class="bar-label">
                      <span class="party-short">{{ party.partyName }}</span>
                      <span class="vote-percentage">{{ getVotePercentage(party.votes) }}%</span>
                    </div>
                    <div class="bar-track">
                      <div class="bar-progress" 
                           :style="{ 
                             width: `${getBarWidth(party.votes)}%`,
                             backgroundColor: getPartyColor(index)
                           }">
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="map-component">
            <map-component />
          </div>
        </div>
      </section>
    </div>
  </layout-component>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElectionService } from '@/services/electionService.js'
import LayoutComponent from '@/components/LayoutComponent/LayoutComponent.vue'
import MapComponent from '@/components/MapComponent.vue'
import IconWithInfoComponent from "@/components/IconWithInfoComponent.vue";

const electionData = ref(null)
const kpiData = ref(null)
const loading = ref(false)
const error = ref('')

const tableHeaders = [
  { title: 'Party Name', key: 'partyName', sortable: true },
  { title: 'Vote Count', key: 'votes', sortable: true }
]

// Transform party data for table display
const partyData = computed(() => {
  if (!electionData.value || !electionData.value.parties) return []
  
  return electionData.value.parties
    .map(party => ({
      id: party.id,
      partyName: party.name,
      votes: party.votes,
      seats: party.seats,
      elected: party.elected
    }))
    .sort((a, b) => b.votes - a.votes)
})

// KPI Computed Properties - now from backend
const winningParty = computed(() => {
  if (!kpiData.value) return null
  return {
    name: kpiData.value.winningPartyName,
    seats: kpiData.value.winningPartySeats
  }
})

const totalVotes = computed(() => {
  return kpiData.value?.totalVotes || 0
})

const electedPartiesCount = computed(() => {
  return kpiData.value?.electedPartiesCount || 0
})

const totalPartiesCount = computed(() => {
  return kpiData.value?.totalPartiesCount || 0
})

// Table split computed properties
const leftColumnParties = computed(() => {
  const half = Math.ceil(partyData.value.length / 2)
  return partyData.value.slice(0, half)
})

const rightColumnParties = computed(() => {
  const half = Math.ceil(partyData.value.length / 2)
  return partyData.value.slice(half)
})

// Chart computed properties
const top8Parties = computed(() => {
  if (!electionData.value || !electionData.value.parties) return []
  return electionData.value.parties
    .filter(party => party.votes > 0)
    .sort((a, b) => b.votes - a.votes)
    .slice(0, 8)
    .map(party => ({
      id: party.id,
      partyName: party.name.length > 20 ? party.name.substring(0, 20) + '...' : party.name,
      votes: party.votes
    }))
})

const maxVotes = computed(() => {
  if (!partyData.value.length) return 0
  return Math.max(...partyData.value.map(party => party.votes))
})

// Chart methods
const getBarWidth = (votes) => {
  if (!maxVotes.value) return 0
  return (votes / maxVotes.value) * 100
}

const getVotePercentage = (votes) => {
  if (!totalVotes.value) return 0
  return ((votes / totalVotes.value) * 100).toFixed(1)
}

const getPartyColor = (index) => {
  const colors = ['#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', '#ef4444']
  return colors[index] || '#6b7280'
}

const fetchElectionResults = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await ElectionService.getElectionResults('TK2023')
    electionData.value = response
    kpiData.value = response.kpis
  } catch (err) {
    error.value = 'Failed to load election results. Please try again later.'
  } finally {
    loading.value = false
  }
}

const formatNumber = (number) => {
  return new Intl.NumberFormat('nl-NL').format(number)
}

onMounted(() => {
  fetchElectionResults()
})
</script>

<style scoped>
.kpi-description {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.bar-section-header{
  margin: 0 0 20px 0;
  display: flex;
  flex-direction: row;
  height: 100%;
  align-items: center;
}
.containerIcon {
  display: inline-flex;
}
.election-results {
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  padding: 80px 0 60px;
  background: linear-gradient(208deg, #3B82F6 0%, #8B5CF6 50%, rgba(249, 115, 22, 0.73) 100%);
  color: white;
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

/* Content Section */
.content-section {
  padding: 60px 0;
  background: #f8fafc;
  min-height: 60vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Results Container */
.results-container {
  display: flex;
  justify-content: center;
}

.results-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  width: 100%;
  max-width: 900px;
}

.results-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.results-subtitle {
  font-size: 14px;
  color: rgba(51, 51, 51, 0.7);
  margin-bottom: 32px;
}

/* Table Styling */
.table-container {
  overflow-x: auto;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.results-table th  {
  background: linear-gradient(135deg, #f8fafc, #e2e8f0);
  color: #374151;
  padding: 16px;
  text-align: left;
  font-weight: 700;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  border-bottom: 2px solid #d1d5db;
  top: 0;
  z-index: 10;
}

.table-row {
  border-bottom: 1px solid #e5e7eb;
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background-color: #f8fafc;
}

.table-row:last-child {
  border-bottom: none;
}

.results-table td {
  padding: 16px;
  color: #374151;
}

.party-name {
  font-weight: 600;
  color: #1f2937;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.vote-count {
  font-family: 'Segoe UI', monospace;
  font-weight: 600;
  color: #059669;
  text-align: right;
}

/* Loading State */
.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: #64748b;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #3B82F6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  font-size: 16px;
  margin: 0;
}

/* Error State */
.error-state {
  text-align: center;
  padding: 80px 20px;
}

.error-message {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 24px;
  max-width: 500px;
  margin: 0 auto;
}

.error-message h3 {
  color: #dc2626;
  margin-bottom: 8px;
}

.error-message p {
  color: #991b1b;
  margin: 0;
}

/* KPI Cards Styling */
.kpi-section {
  margin-bottom: 32px;
  z-index: 1;
}

.kpi-section-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 24px;
  text-align: center;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.kpi-card {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.kpi-card:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  padding: 20px 24px;
}

.kpi-card:hover::before {
  opacity: 1;
}

.kpi-card.winner {
  border-left: 4px solid #10b981;
}

.kpi-card.votes {
  border-left: 4px solid #3b82f6;
}

.kpi-card.parties {
  border-left: 4px solid #8b5cf6;
}

.kpi-card.majority {
  border-left: 4px solid #f59e0b;
}

.kpi-icon {
  font-size: 24px;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.kpi-card:hover .kpi-icon {
  transform: scale(1.2);
  font-size: 28px;
}

.kpi-content h3 {
  margin: 0 0 4px 0;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.kpi-value {
  margin: 0 0 2px 0;
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  transition: all 0.2s ease;
}

.kpi-card:hover .kpi-value {
  font-size: 24px;
}

.kpi-detail {
  margin: 0;
  font-size: 11px;
  color: #6b7280;
}

.kpi-card:hover .kpi-detail {
  font-size: 12px;
}

/* Single Scrollable Table Layout */
.single-table-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  margin-bottom: 40px;
}

.table-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem 2rem;
  text-align: center;
}

.table-header h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.table-header p {
  margin: 0;
  font-size: 0.95rem;
  opacity: 0.9;
}

.scrollable-table {
  max-height: 500px;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Custom Scrollbar Styling */
.scrollable-table::-webkit-scrollbar {
  width: 8px;
}

.scrollable-table::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.scrollable-table::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 4px;
}

.scrollable-table::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #5a67d8, #6b46c1);
}

/* Table Column Widths */
.rank-col {
  width: 10%;
  text-align: center;
}

.party-col {
  width: 35%;
  text-align: right;
}

.votes-col {
  width: 25%;
  text-align: right;
}

.percentage-col {
  width: 15%;
  text-align: right;
}

.seats-col {
  width: 15%;
  text-align: center;
}

/* Table Row with Percentage Background */
.table-row {
  position: relative;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f1f5f9;
}

.table-row::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: var(--vote-percentage, 0%);
  background: linear-gradient(90deg, 
    var(--party-color, #3b82f6) 0%, 
    rgba(255, 255, 255, 0.1) 100%);
  opacity: 0.25;
  transition: all 0.4s ease;
  z-index: 1;
}

.table-row:hover {
  background-color: rgba(248, 250, 252, 0.8);
  transform: translateX(3px);
}

.table-row:hover::before {
  opacity: 0.4;
  transform: scaleX(1.02);
}

.table-row td {
  position: relative;
  z-index: 2;
  padding: 14px 16px;
  font-size: 0.9rem;
}

/* Individual Cell Styles */
.rank {
  font-weight: 800;
  color: #374151;
  text-align: center;
  background: rgba(249, 250, 251, 0.9);
  font-size: 0.85rem;
}

.party-name {
  font-weight: 700;
  color: #1f2937;
  font-size: 0.95rem;
}

.vote-count {
  font-family: 'Segoe UI', monospace;
  font-weight: 600;
  color: #059669;
  text-align: right;
}

.percentage {
  font-weight: 700;
  color: #7c3aed;
  text-align: right;
  font-size: 0.85rem;
}

.seat-count {
  font-weight: 700;
  color: #dc2626;
  text-align: center;
  background: rgba(254, 242, 242, 0.9);
  border-radius: 6px;
  padding: 4px 8px;
}

/* Simple Bar Chart */
.bar-chart-section {
  background: #f8fafc;
  padding: 24px;
  border-radius: 12px;
  border-top: 3px solid #3b82f6;
}

.bar-chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.simple-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.simple-bar-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.bar-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.party-short {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.vote-percentage {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
}

.bar-track {
  height: 20px;
  background: #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

.bar-progress {
  height: 100%;
  border-radius: 10px;
  transition: width 0.8s ease;
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .kpi-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    margin: 0 16px 32px 16px;
  }
  
  .kpi-card {
    padding: 20px;
  }
  
  .kpi-section-title {
    font-size: 24px;
    margin: 0 16px 20px 16px;
  }
  
  .table-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .bar-chart-section {
    padding: 16px;
  }
  
  .bar-chart-title {
    font-size: 16px;
  }
  
  .results-card {
    padding: 24px;
    margin: 0 16px;
  }
  
  .results-table th,
  .results-table td {
    padding: 12px 8px;
    font-size: 14px;
  }
  
  .results-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 28px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .results-table th,
  .results-table td {
    padding: 10px 6px;
    font-size: 13px;
  }
}
</style>

