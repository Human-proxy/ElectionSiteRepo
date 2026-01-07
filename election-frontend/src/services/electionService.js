import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL

export class ElectionService {
    static async getElectionResults(electionId = 'TK2023') {
        try {
            const response = await axios.get(`${API_BASE_URL}/elections/${electionId}/results`);
            return response.data;
        } catch (error) {
            throw error;
        }
    }

    static async getElectionResultsLegacy() {
        try {
            const response = await axios.get(`${API_BASE_URL}/party-data`);
            return response.data;
        } catch (error) {
            throw error;
        }
    }
    static async getMunicipalityData(municipalityName) {
        try {
            const response = await axios.get(`${API_BASE_URL}/elections/TK2023/municipalities/${municipalityName}`);
            return response.data;
        } catch (error) {
            throw error;
        }
    }

    static async getMunicipalityResults(municipalityName) {
        try {
            const response = await axios.get(`${API_BASE_URL}/elections/TK2023/municipalities/${municipalityName}/results`);
            return response.data;
        } catch (error) {
            throw error;
        }
    }
}
