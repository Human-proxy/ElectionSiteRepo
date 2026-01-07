<template>
  <layout-component>
    <div class="chart-container">
      <h1 class="text-xl mt-4">Verkiezingen vergelijken</h1>
      <p class="text-gray-500 mt-2 mb-4">Vergelijk de zetelverdeling tussen twee Tweede Kamerverkiezingen</p>

      <div class="controls flex justify-center">
        <div style="padding:1.5rem;"
             class="select-year-container p-6 w-full flex border rounded-[20px] justify-center border-black my-2">
          <div class="w-[39%]">
            <p>Verkiezing 1</p>
            <select class="w-[89.9%]" v-model="year1" @change="loadData">
              <option value="2017">2017</option>
              <option value="2021">2021</option>
              <option value="2023">2023</option>
            </select>
            <IconDropdown />
          </div>

          <div class="w-[39%]">
            <p>Verkiezing 2</p>
            <select class="w-[94.9%]" v-model="year2" @change="loadData">
              <option value="2017">2017</option>
              <option value="2021">2021</option>
              <option value="2023">2023</option>
            </select>
            <IconDropdown />
          </div>
          <div class="FilterButton w-[20%] relative filter-wrapper">
            <button>Filter</button>
            <div class="dropdown">
              <div v-if="comparisonData">
          <party-filter-component v-for="party in comparisonData.parties " :key="party.partyId" v-model="filterSelections[party.partyId]" :party="party"></party-filter-component>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="border-black border rounded-[20px] chart-inner" style="padding:1.5rem;">
        <h4>Zetelverdeling</h4>
        <div v-if="loading" class="flex justify-center items-center h-full">
          <p>Data laden...</p>
        </div>
        <div v-if="chartData === null && !loading" class="flex justify-center items-center h-full">
          <p>Geen data beschikbaar voor de geselecteerde jaren.</p>
        </div>
        <Bar style="padding: 1.2rem;" v-if="chartData" :data="chartData" :options="chartOptions"/>
      </div>

      <!-- If there's an error or no data, show a friendly message -->
      <div v-if="errorMessage" class="no-data-message mt-4 p-4 rounded border border-red-300 text-red-700">
        {{ errorMessage }}
      </div>

      <div v-else-if="comparisonData && comparisonData.parties && comparisonData.parties.length === 0"
           class="no-data-message mt-4 p-4 rounded border border-gray-300 text-gray-700">
        Geen data gevonden voor de geselecteerde jaren.
      </div>

      <div v-else-if="comparisonData" style="padding: 1rem;" class="comparison-table rounded-[20px] border border-black">
        <h3>Overzicht per Partij</h3>
        <table>
          <thead>
          <tr>
            <th>Partij</th>
            <th>{{ comparisonData.year1 }}</th>
            <th>{{ comparisonData.year2 }}</th>
            <th>Verschil</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="party in filteredParties" :key="party.partyId">
            <td>
              <span class="party-name">{{ party.partyName }}</span>
            </td>
            <td>{{ party.seatsYear1 }} zetel<span v-if="party.seatsYear1 !== 1">s</span></td>
            <td>{{ party.seatsYear2 }} zetel<span v-if="party.seatsYear2 !== 1">s</span></td>
            <td>
              <span
                  :class="{
                  positive: party.difference > 0,
                  negative: party.difference < 0,
                  neutral: party.difference === 0,
                }"
              >
                <template v-if="party.difference > 0">▲ +{{ party.difference }}</template>
                <template v-else-if="party.difference < 0">▼ {{ party.difference }}</template>
                <template v-else>– 0</template>
              </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </layout-component>
</template>

<script setup>
import {computed, ref, watch} from "vue";
import {Bar} from "vue-chartjs";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import IconDropdown from "@/components/icons/IconDropdown.vue";
import PartyFilterComponent from "@/components/VergelijkingPagina/PartyFilterComponent.vue";
import Post from "@/components/Forum/Post.vue";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const year1 = ref(2021);
const year2 = ref(2023);
const chartData = ref(null);
const comparisonData = ref(null);
const errorMessage = ref('');
const loading = ref(false);
const filterSelections = ref({});

const filteredParties = computed(() => {
  if (!comparisonData.value) return [];
  return comparisonData.value.parties.filter(
      p => filterSelections.value[p.partyId] !== false
  );
});
watch(filterSelections, () => {
  if (!comparisonData.value) return;

  const parties = filteredParties.value;

  chartData.value = {
    labels: parties.map(p => p.partyName),
    datasets: [
      {
        label: `${comparisonData.value.year1}`,
        data: parties.map((p) => p.seatsYear1),
        backgroundColor: "rgba(139, 92, 246, 0.8)",
      },
      {
        label: `${comparisonData.value.year2}`,
        data: parties.map((p) => p.seatsYear2),
        backgroundColor: "rgba(249, 115, 22, 0.9)",
      },
    ],
  };
}, { deep: true });
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: "bottom",
    },
  },
};

async function loadData() {
  loading.value = true;
  errorMessage.value = '';
  comparisonData.value = null;
  chartData.value = null;

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/elections/compare?year1=${year1.value}&year2=${year2.value}`);
    if (!res.ok) {
      errorMessage.value = `Fout bij ophalen data: ${res.status} ${res.statusText}`;
      loading.value = false;
      return;
    }

    const data = await res.json();

    // If API returns no parties, set empty result and show message
    if (!data || !data.parties || data.parties.length === 0) {
      comparisonData.value = { year1: year1.value, year2: year2.value, parties: [] };
      loading.value = false;
      return;
    }

    // Chart data
    chartData.value = {
      labels: data.parties.map((p) => p.partyName),
      datasets: [
        {
          label: `${data.year1}`,
          data: data.parties.map((p) => p.seatsYear1),
          backgroundColor: "rgba(139, 92, 246, 0.8)",
        },
        {
          label: `${data.year2}`,
          data: data.parties.map((p) => p.seatsYear2),
          backgroundColor: "rgba(249, 115, 22, 0.9)",
        },
      ],
    };

    // Table data
    comparisonData.value = data;
    filterSelections.value = {};
    for (const party of data.parties) {
      filterSelections.value[party.partyId] = true;
    }
  } catch (e) {
    errorMessage.value = 'Er is een fout opgetreden bij het ophalen van de vergelijking.';
  } finally {
    loading.value = false;
  }
}

// Laad standaard data bij opstart
loadData();
</script>

<style scoped>
.filter-wrapper .dropdown {
  display: none;
}

.filter-wrapper:hover .dropdown {
  display: block;
}
.dropdown {
  position: absolute;
  color: black;
  background-color: rgba(240, 240, 253, 0.83);
  max-width: 100%;
  min-width: 100%;
}
.FilterButton {
  background-color: rgba(249, 115, 22, 0.9);
  color: white;
  font-weight: bold;
  align-content: center;
  text-align: center;
  border-radius: 15px;
  margin: 15px;
}
.FilterButton:hover {
  background-color: rgba(158, 72, 12, 0.9);
}
.chart-container {
  background: #fff;
  border-radius: 1rem;
  padding: 1rem;

  width: 1200px;
  max-width: 100%;
  min-height: 600px;
  margin: 0 auto 2rem;
}

.controls {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.comparison-table {
  margin-top: 2rem;
}

.comparison-table h3 {
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}

th {
  text-align: left;
  padding: 0.5rem;
}

td {
  padding: 0.5rem;
  border-top: 1px solid #eee;
}

.party-name {
  font-weight: 600;
  color: #111827; /* slightly darker text for clarity */
}
.chart-inner {
  height: 600px; /* keep the inner chart area large enough for the canvas */
}

.positive {
  color: #198754; /* groen */
}

.negative {
  color: #dc3545; /* rood */
}

.neutral {
  color: #6c757d; /* grijs */
}
/* ensure the canvas doesn't overflow the container */
.chart-container canvas {
  width: 100% !important;
  height: 100% !important;
}

.no-data-message {
  text-align: center;
}
</style>