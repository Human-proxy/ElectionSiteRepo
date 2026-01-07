import http from './http'; //  axios instance

export const QuizService = {
    /**
     * Fetches questions. If year is provided, fetches specific data for that year.
     */
    async getQuestions(year = '2023') {
        try {
            const response = await http.get(`/api/quiz/questions`, {
                params: { year }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching quiz questions:', error);
            throw error;
        }
    },

    /**
     * Submits the answers to get the result.
     * Payload: { year, partyId, region, dataType }
     */
    async getResult(payload) {
        try {
            const response = await http.post('/api/quiz/result', payload);
            return response.data;
        } catch (error) {
            console.error('Error fetching quiz result:', error);
            throw error;
        }
    },
    /**
     * Requests the backend to generate a CSV and triggers a browser download.
     */
    async exportResult(payload) {
        try {
            const response = await http.post('/api/quiz/export', payload, {
                responseType: 'blob' // Important: tells Axios to handle binary data
            });
            
            // Create a download link programmatically
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            
            // Extract filename from header if possible, otherwise default
            const contentDisposition = response.headers['content-disposition'];
            let fileName = 'resultaat.csv';
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
                if (fileNameMatch && fileNameMatch.length === 2) fileName = fileNameMatch[1];
            }
            
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            
            // Cleanup
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Export failed:', error);
            throw error;
        }
    }
};