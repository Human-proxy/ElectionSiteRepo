<template>
  <div class="step" :id="id" :class="{ 'step-reverse': reverse }">
    <div class="step-visual">
      <div class="step-card">
        <div class="step-card-inner">
          <div class="step-content-center">
            <div class="step-icon" :class="`step-icon-${color}`">
              <IconComponent :name="iconName" class="icon-svg" />
            </div>
            
            <div class="step-header">
              <span class="step-badge">Stap {{ stepNumber }}</span>
              <h3 class="step-title">{{ title }}</h3>
              <p class="step-subtitle">{{ subtitle }}</p>
            </div>

            <slot name="visualization"></slot>
          </div>
        </div>
      </div>
    </div>

    <div class="step-details">
      <div class="step-details-header">
        <h3 class="details-title">{{ title }}</h3>
        <p class="details-description">{{ description }}</p>
      </div>

      <div class="details-list">
        <div v-for="(detail, index) in details" :key="index" class="detail-item">
          <svg class="detail-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="detail-text">{{ detail }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import IconComponent from './IconComponent.vue'

defineProps({
  stepNumber: Number,
  title: String,
  subtitle: String,
  description: String,
  details: Array,
  color: String,
  iconName: String,
  reverse: Boolean,
  id: String
})
</script>

<style scoped>
.step {
  display: flex;
  gap: 64px;
  align-items: center;
}

.step-reverse {
  flex-direction: row-reverse;
}

.step-visual {
  flex: 1;
  width: 100%;
}

.step-card {
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.5s;
}

.step-card:hover {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.step-card-inner {
  padding: 48px;
}

.step-content-center {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.step-icon {
  width: 112px;
  height: 112px;
  margin: 0 auto;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.step-card:hover .step-icon {
  transform: scale(1.1);
}

.step-icon-blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.step-icon-green {
  background: linear-gradient(135deg, #10b981, #059669);
}

.step-icon-purple {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.step-icon-orange {
  background: linear-gradient(135deg, #f97316, #ea580c);
}

.step-icon-yellow {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.icon-svg {
  width: 48px;
  height: 48px;
  color: white;
}

.step-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step-badge {
  display: inline-block;
  background-color: #f3f4f6;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 9999px;
  align-self: center;
}

.step-title {
  font-size: 24px;
  font-weight: 800;
  color: #111827;
}

.step-subtitle {
  font-size: 18px;
  color: #6b7280;
}

.step-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step-details-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.details-title {
  font-size: 30px;
  font-weight: 800;
  color: #111827;
}

.details-description {
  font-size: 20px;
  color: #6b7280;
  line-height: 1.75;
}

.details-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.detail-icon {
  width: 24px;
  height: 24px;
  color: #10b981;
  margin-top: 2px;
  flex-shrink: 0;
}

.detail-text {
  font-size: 16px;
  line-height: 1.75;
  color: #374151;
}

@media (max-width: 1024px) {
  .step {
    flex-direction: column;
    gap: 48px;
  }
  
  .step-reverse {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .step-card-inner {
    padding: 32px;
  }
}
</style>
