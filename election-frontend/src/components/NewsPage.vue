<template>
    <LayoutComponent>
        <v-container fluid class="news-page-container">
            <!-- Hero Section -->
            <v-row class="hero-section">
                <v-col cols="12">
                    <div class="hero-content">
                        <h1 class="hero-title">Actueel Nieuws</h1>
                        <p class="hero-subtitle">Blijf op de hoogte van het laatste nieuws uit de politiek</p>
                    </div>
                </v-col>
            </v-row>

            <v-row>
                <v-col cols="12">
                    <div class="toggle-section">
                        <v-btn 
                            color="primary" 
                            @click="fetchNews" 
                            :loading="loading"
                            size="large"
                            variant="outlined"
                            class="refresh-btn"
                            rounded="lg"
                        >
                            <v-icon left>mdi-refresh</v-icon>
                            Vernieuwen
                        </v-btn>
                    </div>

                    <v-alert v-if="error" type="error" class="mb-4">
                        {{ error }}
                    </v-alert>

                    <v-progress-linear v-if="loading" indeterminate color="primary" class="mb-4"></v-progress-linear>

                    <!-- Government News -->
                    <v-row class="news-grid">
                        <v-col 
                            v-for="item in newsItems" 
                            :key="item.id" 
                            cols="12" 
                            sm="6"
                            md="6" 
                            lg="4"
                            xl="3"
                        >
                            <v-card elevation="0" class="news-card government-card" hover>
                                <div class="card-header government-header">
                                    <v-icon size="x-large" color="white">mdi-domain</v-icon>
                                </div>
                                
                                <v-card-title class="card-title">
                                    {{ item.title }}
                                </v-card-title>
                                
                                <v-card-subtitle class="chips-container">
                                    <v-chip 
                                        v-if="item.category" 
                                        size="small"
                                        color="indigo"
                                        variant="flat"
                                    >
                                        {{ item.category }}
                                    </v-chip>
                                </v-card-subtitle>

                                <v-card-text class="card-content">
                                    <p v-if="item.description" class="description-text">
                                        {{ truncateText(item.description, 150) }}
                                    </p>
                                    <div class="meta-info">
                                        <div class="meta-item">
                                            <v-icon size="small">mdi-calendar</v-icon>
                                            <span>{{ formatDate(item.publicationDate) }}</span>
                                        </div>
                                        <div class="meta-item" v-if="item.lastModified">
                                            <v-icon size="small">mdi-update</v-icon>
                                            <span>Bijgewerkt: {{ formatDate(item.lastModified) }}</span>
                                        </div>
                                    </div>
                                </v-card-text>

                                <v-card-actions class="card-actions">
                                    <v-btn 
                                        :href="item.url" 
                                        target="_blank" 
                                        variant="tonal"
                                        color="indigo"
                                        rounded="lg"
                                        block
                                        class="action-btn"
                                    >
                                        <span>Lees artikel</span>
                                        <v-icon class="btn-icon">mdi-open-in-new</v-icon>
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-col>
                    </v-row>

                    <v-alert v-if="!loading && newsItems.length === 0" type="info" class="mt-4">
                        Geen nieuws beschikbaar. Klik op "Vernieuwen" om data op te halen.
                    </v-alert>

                    <!-- News counter -->
                    <v-row v-if="newsItems.length > 0" class="mt-4">
                        <v-col cols="12" class="d-flex justify-center">
                            <v-chip 
                                color="primary" 
                                variant="tonal"
                                size="large"
                                prepend-icon="mdi-newspaper"
                            >
                                {{ newsItems.length }} nieuwsbericht{{ newsItems.length !== 1 ? 'en' : '' }}
                            </v-chip>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
        </v-container>
    </LayoutComponent>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import LayoutComponent from './LayoutComponent/LayoutComponent.vue';
import GovernmentNewsService from '../services/governmentNewsService.js';

const newsItems = ref([]);
const loading = ref(false);
const error = ref(null);
const currentLimit = ref(50); // Fetch more items at once

async function fetchNews() {
    try {
        loading.value = true;
        error.value = null;
        newsItems.value = await GovernmentNewsService.getRecentNews(currentLimit.value);
    } catch (err) {
        error.value = 'Failed to load data.';
        console.error(err);
    } finally {
        loading.value = false;
    }
}

function formatDate(dateString) {
    if (!dateString) return 'Geen datum';
    const date = new Date(dateString);
    return date.toLocaleDateString('nl-NL', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

onMounted(() => {
    fetchNews();
});
</script>

<style scoped>
.news-page-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 16px;
}

/* Hero Section */
.hero-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 24px;
    padding: 48px 24px;
    margin-bottom: 32px;
    box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.hero-content {
    text-align: center;
    color: white;
}

.hero-title {
    font-size: 3rem;
    font-weight: 800;
    margin-bottom: 12px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
    font-size: 1.25rem;
    opacity: 0.95;
    font-weight: 300;
}

/* Toggle Section */
.toggle-section {
    display: flex;
    justify-content: center;
    margin-bottom: 32px;
}

.refresh-btn {
    font-weight: 500;
    text-transform: none;
}

/* News Grid */
.news-grid {
    margin-top: 24px;
}

/* News Cards */
.news-card {
    height: 100%;
    display: flex;
    flex-direction: column;
    border-radius: 16px !important;
    overflow: hidden;
    border: 1px solid rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: white;
}

.news-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
    border-color: rgba(102, 126, 234, 0.3);
}

/* Parliament Cards */
.parliament-card .card-header {
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.parliament-card .card-header::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: rgba(255, 255, 255, 0.3);
}

/* Government Cards */
.government-card .image-container {
    position: relative;
    overflow: hidden;
}

/* Government Card Header */
.government-card .card-header {
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.government-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.government-card .card-header::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    opacity: 0.6;
}

/* Button Styling */
.action-btn {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    gap: 8px !important;
    font-weight: 600 !important;
    text-transform: none !important;
    letter-spacing: 0.3px !important;
}

.action-btn span {
    line-height: 1 !important;
}

.btn-icon {
    margin-left: 4px !important;
    font-size: 18px !important;
}

/* Card Content */
.card-title {
    font-size: 1.1rem;
    font-weight: 700;
    line-height: 1.4;
    padding: 16px 20px 12px;
    color: #1a1a1a;
}

.chips-container {
    padding: 0 20px 12px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.card-content {
    padding: 0 20px 16px;
    flex-grow: 1;
}

.description-text {
    color: #4a5568;
    line-height: 1.6;
    margin-bottom: 16px;
    font-size: 0.95rem;
}

.meta-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-top: auto;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #718096;
    font-size: 0.85rem;
}

.meta-item v-icon {
    opacity: 0.7;
}

.card-actions {
    padding: 16px 20px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
}

/* Responsive */
@media (max-width: 600px) {
    .hero-title {
        font-size: 2rem;
    }
    
    .hero-subtitle {
        font-size: 1rem;
    }
}
</style>
