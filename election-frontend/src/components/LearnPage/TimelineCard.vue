<template>
  <div class="timeline-card" :class="`timeline-card-${color}`">
    <div class="timeline-icon" :class="`timeline-icon-${color}`">
      <IconComponent :name="iconName" class="icon-svg" />
    </div>
    <h3 class="timeline-title">{{ title }}</h3>
    <p class="timeline-subtitle">{{ subtitle }}</p>
    <p class="timeline-text">{{ description }}</p>
  </div>
</template>

<script setup>
import IconComponent from './IconComponent.vue'

defineProps({
  title: String,
  subtitle: String,
  description: String,
  color: String,
  iconName: String
})
</script>

<style scoped>
.timeline-card {
  background-color: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.timeline-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s;
}

.timeline-card:hover .timeline-icon {
  transform: scale(1.1);
}

.timeline-icon-blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.timeline-icon-green {
  background: linear-gradient(135deg, #10b981, #059669);
}

.timeline-icon-orange {
  background: linear-gradient(135deg, #f97316, #ea580c);
}

.icon-svg {
  width: 24px;
  height: 24px;
  color: white;
}

.timeline-title {
  font-size: 20px;
  font-weight: 800;
  color: #111827;
}

.timeline-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 8px;
}

.timeline-text {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}
</style>
