<template>
    <div class="wrapper">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>Loading map...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="error-state">
            <div class="error-message">
                <h3>Error loading map</h3>
                <p>{{ error }}</p>
                <button @click="initializeMap" class="retry-button">Try Again</button>
            </div>
        </div>

        <!-- Map Container -->
        <div v-else class="map-wrapper">
            <div ref="mapContainer" class="map-container"></div>
            
            <!-- Municipality Data Panel -->
            <div v-if="electionData" class="data-panel">
                <div class="panel-header">
                    <h3>{{ electionData.name }}</h3>
                    <button @click="electionData = null" class="close-button">✕</button>
                </div>
                
                <!-- Municipality Party Votes -->
                <div class="municipality-section">
                    <h4>Party Votes in {{ electionData.municipalityName || electionData.name }}</h4>
                    <div v-if="electionData.error" class="error-message-small">
                        {{ electionData.error }}
                    </div>
                    <div v-else-if="municipalityParties.length === 0" class="no-data-message">
                        No party data available for this municipality.
                    </div>
                    <div v-else class="party-votes">
                        <div v-for="party in municipalityParties" :key="party.name" class="party-item">
                            <div class="party-color-indicator" :style="{ backgroundColor: party.color }"></div>
                            <span class="party-name">{{ party.shortcode || party.name }}</span>
                            <div class="party-stats">
                                <span class="party-votes">{{ party.votes.toLocaleString() }} votes</span>
                                <span class="party-percentage" v-if="party.percentage">({{ party.percentage.toFixed(1) }}%)</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'
import proj4 from 'proj4'
import { onMounted, ref, watch, computed } from 'vue'
import { ElectionService } from '@/services/electionService'
import GeoJsonNetherlands from '@/assets/geojson-netherlands.json'

// Define the Dutch RD coordinate system (EPSG:28992)
proj4.defs("EPSG:28992", "+proj=sterea +lat_0=52.15616055555555 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.417,50.3319,465.552,-0.398957,0.343988,-1.8774,4.0725 +units=m +no_defs")

// Helper function to convert RD coordinates to WGS84
function convertRDtoWGS84(coordinates) {
    if (Array.isArray(coordinates[0])) {
        // Recursive call for nested arrays
        return coordinates.map(coord => convertRDtoWGS84(coord))
    } else {
        // Convert single coordinate pair [x, y] to [lng, lat]
        const [lng, lat] = proj4('EPSG:28992', 'EPSG:4326', coordinates)
        return [lng, lat]
    }
}

const electionData = ref(null)
const loading = ref(true)
const error = ref('')
const map = ref(null)
const mapContainer = ref(null)
const highlightedMunicipality = ref(null)
const geoJsonLayer = ref(null)
const municipalityColors = ref(new Map()) // Store winning party color for each municipality
const legendControl = ref(null)
const partyLegend = ref(new Map()) // Store unique parties with their colors

const initializeMap = async () => {
    try {
        // Check if map container ref exists
        if (!mapContainer.value) {
            throw new Error('Map container not found')
        }

        map.value?.remove()  // Remove existing map if any

        map.value = L.map(mapContainer.value, {
            center: [52.3676, 4.9041], // Amsterdam, Netherlands coordinates
            zoom: 7,
            minZoom: 7,
        })

        // Add CartoDB Positron tile layer (clean, minimal style without city labels)
        L.tileLayer('https://{s}.basemaps.cartocdn.com/light_nolabels/{z}/{x}/{y}{r}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
            subdomains: 'abcd',
            maxZoom: 20
        }).addTo(map.value)

        // Convert GeoJSON coordinates from EPSG:28992 to WGS84
        const convertedFeatures = GeoJsonNetherlands.features.map(feature => ({
            ...feature,
            geometry: {
                ...feature.geometry,
                coordinates: convertRDtoWGS84(feature.geometry.coordinates)
            }
        }))

        geoJsonLayer.value = L.geoJSON(convertedFeatures, {
            style: (feature) => getFeatureStyle(feature),
            onEachFeature: (feature, layer) => {
                if (feature.properties && feature.properties.name) {
                    // Tooltip for municipality name
                    layer.bindTooltip(`<strong>${feature.properties.name}</strong>`, {
                        direction: 'top',
                        permanent: false,
                        sticky: true,
                        offset: [0, -10],
                        className: 'municipality-tooltip'
                    });

                    // Mouse events
                    layer.on('mouseover', function() {
                        this.openTooltip();
                        if (highlightedMunicipality.value !== feature.properties.name) {
                            this.setStyle({ 
                                fillOpacity: 0.7,
                                fillBorderColor: 'black',
                            });
                        }
                    });
                    layer.on('mouseout', function() {
                        this.closeTooltip();
                        if (highlightedMunicipality.value !== feature.properties.name) {
                            this.setStyle(getFeatureStyle(feature));
                        }
                    });
                    // Click event for highlighting
                    layer.on('click', function() {
                        handleMunicipalityClick(feature.properties.name);
                    });
                }
            }
        }).addTo(map.value)

        // Fetch all municipality colors and populate legend
        await fetchAllMunicipalityColors()
        
        // Add legend to map after colors are loaded
        addLegend()
        
        // Update map colors now that data is loaded
        if (geoJsonLayer.value) {
            geoJsonLayer.value.eachLayer((layer) => {
                const feature = layer.feature
                if (feature && feature.properties && feature.properties.name) {
                    layer.setStyle(getFeatureStyle(feature))
                }
            })
        }

        loading.value = false
    } catch (err) {
        error.value = err.message || 'Failed to load map. Please try again.'
        loading.value = false
    }
}

// Function to get the style for a feature based on whether it's highlighted
function getFeatureStyle(feature) {
    const isHighlighted = highlightedMunicipality.value === feature.properties.name
    const municipalityName = feature.properties.name
    const winningColor = municipalityColors.value.get(municipalityName)

    // Use winning party color if available, otherwise default blue
    const baseColor = winningColor || '#3b82f6'
    
    return {
        fillColor: isHighlighted ? '#fbbf24' : baseColor, // Yellow for highlighted, party color for normal
        fillOpacity: isHighlighted ? 0.8 : 0.7,
        color: isHighlighted ? '#374151' : '#9ca3af', // Dark gray for highlighted, light gray for borders
        weight: isHighlighted ? 3 : 1,
    }
}

// Function to handle municipality click
function handleMunicipalityClick(municipalityName) {
    // Toggle highlighting: if clicking the same municipality, unhighlight it
    if (highlightedMunicipality.value === municipalityName) {
        highlightedMunicipality.value = null
    } else {
        highlightedMunicipality.value = municipalityName
    }
    
    // Update all layers with new styles
    if (geoJsonLayer.value) {
        geoJsonLayer.value.eachLayer((layer) => {
            const feature = layer.feature
            if (feature && feature.properties && feature.properties.name) {
                layer.setStyle(getFeatureStyle(feature))
            }
        })
    }

    const municipalityData = fetchMunicipalityData(highlightedMunicipality.value)
}

// Fetch colors for all municipalities in bulk (single request instead of 633)
async function fetchAllMunicipalityColors() {
    try {
        // Fetch all municipality winners in one request
        const response = await fetch(`${import.meta.env.VITE_API_URL}/elections/TK2023/municipalities/winners`)
        if (!response.ok) {
            throw new Error(`Failed to fetch municipality winners: ${response.status}`)
        }
        
        const winners = await response.json()
        
        // Process the winners data
        Object.entries(winners).forEach(([municipalityName, partyInfo]) => {
            if (partyInfo && partyInfo.color) {
                municipalityColors.value.set(municipalityName, partyInfo.color)
                partyLegend.value.set(partyInfo.partyName, partyInfo.color)
            }
        })
    } catch (err) {
        // Error fetching municipality colors - map will use default styling
        // Don't set error.value here as it would prevent the map from loading
        console.warn('Failed to load municipality colors, using default styling:', err.message)
    }
}

// Add legend control to map
function addLegend() {
    if (!map.value) return
    
    legendControl.value = L.control({ position: 'bottomleft' })
    
    legendControl.value.onAdd = function() {
        const div = L.DomUtil.create('div', 'info legend')
        div.innerHTML = '<h4>Winning Parties</h4>'
        
        // Sort parties alphabetically
        const sortedParties = [...partyLegend.value.entries()].sort((a, b) => 
            a[0].localeCompare(b[0])
        )
        
        sortedParties.forEach(([party, color]) => {
            div.innerHTML += `
                <div class="legend-item">
                    <span class="legend-color" style="background-color: ${color}; border-radius: 50%; width: 16px; height: 16px; display: inline-block; border: 2px solid rgba(0,0,0,0.15);"></span>
                    <span class="legend-label">${party}</span>
                </div>
            `
        })
        
        return div
    }
    
    legendControl.value.addTo(map.value)
}

async function fetchMunicipalityData(municipalityName) {
    if (!municipalityName) {
        electionData.value = null
        return
    }

    try {
        const data = await ElectionService.getMunicipalityResults(municipalityName)
        
        if (!data) {
            electionData.value = { 
                name: municipalityName, 
                partyResults: [], 
                error: 'No data available for this municipality' 
            }
            return
        }

        electionData.value = data
        
        // Find winning party (highest votes) and store its color
        if (data.partyResults && data.partyResults.length > 0) {
            const winningParty = data.partyResults[0] // Already sorted by votes from backend
            if (winningParty.color) {
                municipalityColors.value.set(municipalityName, winningParty.color)
                
                // Update the map layer for this municipality
                if (geoJsonLayer.value) {
                    geoJsonLayer.value.eachLayer((layer) => {
                        const feature = layer.feature
                        if (feature && feature.properties && feature.properties.name === municipalityName) {
                            layer.setStyle(getFeatureStyle(feature))
                        }
                    })
                }
            }
        }
    } catch (err) {
        electionData.value = { 
            name: municipalityName, 
            partyResults: [], 
            error: err.message || 'Failed to fetch data' 
        }
    }
}

// Computed property to get municipality party results
const municipalityParties = computed(() => {
    if (!electionData.value || !electionData.value.partyResults) {
        return []
    }
    
    // Return party results sorted by votes (already sorted from backend)
    return electionData.value.partyResults.map(party => ({
        name: party.partyName,
        shortcode: party.partyShortcode,
        votes: party.totalVotes,
        percentage: party.percentage,
        color: party.color || '#808080'
    }))
})

// Watch for when mapContainer ref becomes available
watch(mapContainer, (newVal) => {
    if (newVal && !map.value) {
        // Container is now available, initialize the map
        initializeMap()
    }
})

// Watch for changes in highlighted municipality and update styles
watch(highlightedMunicipality, () => {
    if (geoJsonLayer.value) {
        geoJsonLayer.value.eachLayer((layer) => {
            const feature = layer.feature
            if (feature && feature.properties && feature.properties.name) {
                layer.setStyle(getFeatureStyle(feature))
            }
        })
    }
})

// Start the loading process
onMounted(() => {
    // Set a timeout to change loading state and trigger the v-else condition
    setTimeout(() => {
        loading.value = false
    }, 100)
})

</script>

<style lang="css" scoped>
.wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 50px;
    margin-bottom: 50px;
    min-height: 500px;
}

.map-container {
    width: 100%;
    max-width: 1200px;
    height: 800px;
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

/* Loading State */
.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #e2e8f0;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 16px;
}

.loading-state p {
    color: #64748b;
    font-size: 16px;
    margin: 0;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* Error State */
.error-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 60px 40px;
}

.error-message {
    text-align: center;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 15px;
    padding: 32px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border-left: 4px solid #ef4444;
}

.error-message h3 {
    color: #dc2626;
    margin-bottom: 12px;
    font-size: 18px;
}

.error-message p {
    color: #64748b;
    margin-bottom: 20px;
}

.retry-button {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
}

.retry-button:hover {
    background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

/* Map and data panel layout */
.map-wrapper {
    display: flex;
    gap: 20px;
    width: 100%;
    max-width: 1400px;
}

.map-container {
    flex: 1;
    z-index: 0;
}

/* Data Panel Styles */
.data-panel {
    width: 400px;
    background: white;
    border-radius: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    max-height: 800px;
    overflow-y: auto;
}

.panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    color: white;
}

.panel-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
}

.close-button {
    background: none;
    border: none;
    color: white;
    font-size: 18px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background-color 0.2s;
}

.close-button:hover {
    background-color: rgba(255, 255, 255, 0.2);
}

/* Municipality Section */
.municipality-section {
    padding: 20px;
    border-bottom: 1px solid #e5e7eb;
}

.municipality-section h4 {
    margin: 0 0 15px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
}

.party-votes {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.party-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    background: #f8fafc;
    border-radius: 8px;
    border-left: 4px solid #3b82f6;
}

.party-color-indicator {
    width: 14px;
    height: 14px;
    border-radius: 50%;
    margin-right: 10px;
    flex-shrink: 0;
    border: 2px solid rgba(0, 0, 0, 0.1);
}

.party-name {
    font-weight: 500;
    color: #374151;
    flex: 1;
}

.party-stats {
    display: flex;
    align-items: center;
    gap: 8px;
}

.party-votes {
    font-weight: 600;
    color: #1f2937;
}

.party-percentage {
    font-size: 13px;
    color: #6b7280;
    font-weight: 500;
}

/* Actions */
.actions {
    padding: 20px;
    border-bottom: 1px solid #e5e7eb;
}

.polling-bureaus-btn {
    width: 100%;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    border: none;
    padding: 12px 16px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
}

.polling-bureaus-btn:hover:not(:disabled) {
    background: linear-gradient(135deg, #059669 0%, #047857 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.polling-bureaus-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* Polling Bureaus Section */
.polling-bureaus-section {
    padding: 20px;
}

.polling-bureaus-section h4 {
    margin: 0 0 15px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
}

.bureau-item {
    margin-bottom: 20px;
    padding: 15px;
    background: #f9fafb;
    border-radius: 8px;
    border-left: 4px solid #10b981;
}

.bureau-item h5 {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
    color: #059669;
}

.bureau-parties {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.bureau-party {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 8px;
    background: white;
    border-radius: 4px;
    font-size: 13px;
}

.bureau-party .party-name {
    color: #4b5563;
}

.bureau-party .party-votes {
    color: #1f2937;
    font-weight: 500;
}

/* Error and no-data messages */
.error-message-small {
    padding: 12px;
    background: #fef2f2;
    border: 1px solid #fecaca;
    border-radius: 8px;
    color: #dc2626;
    font-size: 14px;
    text-align: center;
}

.no-data-message {
    padding: 12px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    color: #6b7280;
    font-size: 14px;
    text-align: center;
}

/* Map styling for better highlighting effects */
.map-container :deep(.leaflet-interactive) {
    transition: all 0.2s ease-in-out;
    cursor: pointer;
}

.map-container :deep(.leaflet-interactive:hover) {
    filter: brightness(1.1);
}

/* Responsive Design */
@media (max-width: 768px) {
    .wrapper {
        margin: 20px;
        min-height: 400px;
    }
    
    .map-container {
        height: 400px;
    }
    
    .loading-state,
    .error-message {
        padding: 40px 24px;
    }
}

/* Tooltip styling voor gemeente naam */
.municipality-tooltip {
    background: white;
    color: #1e293b;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    font-size: 15px;
    font-weight: 500;
    padding: 6px 14px;
    border: 1px solid #e5e7eb;
}

/* Legend styling */
.legend {
    background: white;
    padding: 12px 16px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    font-size: 13px;
    line-height: 1.5;
    max-height: 400px;
    overflow-y: auto;
}

.legend h4 {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
    border-bottom: 2px solid #e5e7eb;
    padding-bottom: 6px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 6px;
}

.legend-color {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    border: 2px solid rgba(0,0,0,0.15);
    flex-shrink: 0;
}

.legend-label {
    color: #374151;
    font-size: 12px;
}
</style>
