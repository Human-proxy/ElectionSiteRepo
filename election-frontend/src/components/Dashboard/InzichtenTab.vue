<template>
  <div class="insights-container w-[65%] h-full flex flex-wrap align-center justify-center">
    <div v-if="loading" class="w-full text-center py-6">Loading...</div>
    <div v-else-if="error" class="w-full text-center text-red-600 py-6">kan bronnen niet laden. Probeer het later opnieuw</div>
    <template v-else>
      <header class="flex justify-between items-center w-full ml-6">
        <p class="text-xl font-bold">
          <IconMarker /> Hoe Stemde Jouw Regio? 🗺️
        </p>
        <p id="goto-map-text">
          Bekijk kaart <span id="goto-map-arrow" class="relative transition duration-150"><IconArrowRight /></span>
        </p>
      </header>
      <div v-if="top4.length === 0" class="kieskring-container rounded-xl flex flex-wrap bg-white border w-[100%] border-gray-200 shadow-sm">
        Geen gegevens beschikbaar.
      </div>
      <div v-else
          v-for="(c, i) in top4"
          :key="c.constituencyId || c.id || i"
          class="kieskring-container cursor-pointer rounded-xl flex flex-wrap bg-white border w-[45%] border-gray-200 shadow-sm"
      >
        <div class="flex justify-between items-center w-full">
          <h4 class="font-bold">
            {{ c.constituencyName || c.name || c.abbreviatedName || '—' }}
          </h4>
          <p 
            class="rounded-xl text-sm px-2 mb-3 flex justify-center text-white font-semibold"
            :style="{ backgroundColor: c.topPartyColor || '#6B7280' }"
          >
            {{ getPartyName(c) }}
          </p>
        </div>

        <div class="precentage-container w-full flex flex-wrap">
          <h4 class="w-[75%] text-sm mb-2">Opkomst grootste partij</h4>
          <p class="w-[24%] flex justify-end">{{ calcPercent(c) }}</p>

          <div class="precentage-bar w-full rounded-xl h-2 bg-gray-200">
            <div
                class="h-full rounded-l-xl"
                :style="{ 
                  width: calcPercentRaw(c) + '%',
                  backgroundColor: c.topPartyColor || '#2563eb'
                }"
            ></div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style>
.kieskring-container {
  margin: 1rem;
  padding: 1.2rem;
}

#goto-map-arrow {
  display: inline-block;
  transition: transform 0.2s ease;
}

#goto-map-arrow:hover {
  cursor: pointer;
  transform: translateX(6px);
}

#goto-map-text:hover #goto-map-arrow {
  transform: translateX(6px);
}
#goto-map-text:hover {
  cursor: pointer;
}
</style>

<script setup>
import { ref, onMounted } from 'vue'
import IconMarker from "@/components/icons/IconMarker.vue";
import IconArrowRight from "@/components/icons/IconArrowRight.vue";

const top4 = ref([])
const loading = ref(true)
const error = ref(null)

function abbreviateParty(str) {
  if (!str) return null
  // take everything before the first space (e.g. "PVV (Partij..." -> "PVV")
  const parts = String(str).trim().split(/\s+/)
  return parts.length ? parts[0] : str
}

function getPartyName(c) {
  // Updated to match new DTO structure from backend
  const name =
    c?.topPartyName ||           // New DTO field
    c?.biggestParty?.abbreviatedName ||
    c?.biggestParty?.name ||
    c?.partyAbbrev ||
    c?.partyName ||
    c?.party ||
    c?.topParty ||
    '—'

  // abbreviate by removing everything after first space to match your request
  return abbreviateParty(name) || '—'
}

function calcPercentRaw(c) {
  // Updated to match new DTO structure from backend
  // The backend now sends topPartyPercentage directly
  if (c?.topPartyPercentage != null) {
    const p = Number(c.topPartyPercentage)
    if (!isNaN(p) && isFinite(p)) return Math.max(0, Math.min(100, Math.round(p)))
  }

  // Fallback: calculate from votes if percentage not available
  const votes =
    c?.topPartyVotes ?? c?.votesForBiggestParty ?? c?.biggestPartyVotes ?? c?.votes ?? null
  const total =
    c?.totalVotesCast ?? c?.totalVotes ?? c?.totalEligibleVoters ?? c?.totalEligible ?? c?.eligibleVoters ?? c?.eligible ?? null

  if (votes != null && total != null && Number(total) > 0) {
    const p = (Number(votes) / Number(total)) * 100
    if (!isNaN(p) && isFinite(p)) return Math.max(0, Math.min(100, Math.round(p)))
  }

  // Final fallback to other percentage fields
  const pField =
    c?.percentageForBiggestParty ??
    c?.biggestPartyPercentage ??
    c?.biggestPartyShare ??
    c?.turnoutPercentage ??
    null
  if (pField != null) {
    const p = Number(pField)
    if (!isNaN(p) && isFinite(p)) return Math.max(0, Math.min(100, Math.round(p)))
  }

  return 0
}

function calcPercent(c) {
  const raw = calcPercentRaw(c)
  return raw > 0 ? `${raw}%` : '—'
}

async function fetchTop4() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/election/top4`)
    if (!res.ok) {
      const text = await res.text().catch(() => res.statusText)
      throw new Error(res.status + ' ' + res.statusText + ' — ' + text)
    }

    // robust parsing: try json(), otherwise fallback to text then parse
    let data = null
    try {
      data = await res.json()
    } catch (jsonErr) {
      // response may be invalid JSON; read raw text and attempt parse to give helpful error
      const raw = await res.text().catch(() => '')
      try {
        data = JSON.parse(raw)
      } catch (finalErr) {
        throw new Error('Failed to parse JSON from /api/v1/election/top4: ' + finalErr.message + '\nResponse body:\n' + raw)
      }
    }

    top4.value = Array.isArray(data) ? data.slice(0, 4) : []
  } catch (e) {
    error.value = e?.message || String(e)
    top4.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchTop4)
</script>
