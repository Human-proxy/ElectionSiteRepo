<template>
  <Teleport to="body">
    <div v-if="open" class="modal-overlay" @keydown.esc.prevent.stop="onCancel" tabindex="-1">
      <div class="modal-backdrop" @click="onCancel"></div>
      <div class="modal-card" role="dialog" aria-modal="true" :aria-label="title">
        <h3 class="modal-title">{{ title }}</h3>
        <p class="modal-message">{{ message }}</p>
        <div class="modal-actions">
          <button class="btn btn-cancel" @click="onCancel">{{ cancelText || 'Annuleren' }}</button>
          <button class="btn btn-danger" @click="onConfirm">{{ confirmText || 'Verwijderen' }}</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Bevestig actie' },
  message: { type: String, default: 'Weet je het zeker?' },
  confirmText: { type: String, default: 'Ja' },
  cancelText: { type: String, default: 'Nee' }
})

const emit = defineEmits(['confirm','cancel'])

function onCancel() { emit('cancel') }
function onConfirm() { emit('confirm') }
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 10000;
}
.modal-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.35);
}
.modal-card {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: min(520px, 90vw);
  background: #fff;
  border-radius: 12px;
  padding: 20px 22px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.25);
}
.modal-title {
  font-weight: 700;
  font-size: 18px;
  margin: 0 0 6px;
}
.modal-message {
  color: #374151;
  margin: 0 0 16px;
}
.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
.btn {
  padding: 8px 14px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  font-weight: 600;
}
.btn-cancel {
  background: #f3f4f6;
}
.btn-danger {
  background: #ef4444;
  color: #fff;
  border-color: #ef4444;
}
.btn-danger:hover {
  background: #dc2626;
  border-color: #dc2626;
}
</style>

