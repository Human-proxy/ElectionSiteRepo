<template>
  <div class="seats-visualization">
    <div class="seats-container">
      <p class="seats-label">150 Zetels Tweede Kamer</p>
      <div class="seats-grid">
        <div v-for="i in 150" :key="i" 
          :class="[
            'seat',
            i <= 34 ? 'seat-blue' : 
            i <= 58 ? 'seat-green' : 
            i <= 75 ? 'seat-orange' : 
            i <= 90 ? 'seat-purple' : 
            'seat-gray'
          ]"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.seats-visualization {
  background-color: #f9fafb;
  border-radius: 12px;
  padding: 40px;
  margin-top: 32px;
}

.seats-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.seats-label {
  font-weight: 500;
  color: #111827;
  font-size: 16px;
}

.seats-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 8px;
}

.seat {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.seat-blue {
  background-color: #3b82f6;
}

.seat-green {
  background-color: #10b981;
}

.seat-orange {
  background-color: #f97316;
}

.seat-purple {
  background-color: #8b5cf6;
}

.seat-gray {
  background-color: #d1d5db;
}
</style>
