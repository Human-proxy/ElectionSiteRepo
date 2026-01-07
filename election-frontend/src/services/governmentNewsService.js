import axios from 'axios';


const API_BASE_URL = import.meta.env.VITE_API_URL + "/government-news";

class GovernmentNewsService {
    async getRecentNews(limit = 20) {
        try {
            const response = await axios.get(API_BASE_URL, {
                params: { limit }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching government news:', error);
            throw error;
        }
    }
}

export default new GovernmentNewsService();
