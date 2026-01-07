import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL + "/v1/profile";

export class ProfileService {
  /**
   * Get the current user's profile
   * @param {string} token - JWT token
   * @returns {Promise<Object>} User profile data
   */
  static async getProfile(token) {
    try {
      const response = await axios.get(API_BASE_URL, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Update the user's profile image
   * @param {string} token - JWT token
   * @param {string} profileImageUrl - Base64 encoded image or URL
   * @returns {Promise<Object>} Updated user data
   */
  static async updateProfileImage(token, profileImageUrl) {
    try {
      const response = await axios.put(
        `${API_BASE_URL}/image`,
        { profileImageUrl },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Update the user's profile information
   * @param {Object} profileData - Profile data to update (username, email)
   * @returns {Promise<Object>} Updated user data
   */
  static async updateProfile(profileData) {
    try {
      const token = localStorage.getItem('auth.token');
      if (!token) {
        throw new Error('No authentication token found');
      }

      const response = await axios.put(
        API_BASE_URL,
        profileData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Track a visited page for learning progress
   * @param {string} token - JWT token
   * @param {string} pagePath - Path of the visited page
   * @returns {Promise<Object>} Updated user data with visitedPages
   */
  static async trackPageVisit(token, pagePath) {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/visit`,
        { pagePath },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Change the user's password
   * @param {string} token - JWT token
   * @param {string} currentPassword - Current password
   * @param {string} newPassword - New password
   * @returns {Promise<Object>} API response
   */
  static async changePassword(token, currentPassword, newPassword) {
    try {
      const response = await axios.put(
        `${API_BASE_URL}/password`,
        { currentPassword, newPassword },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Delete the user's account
   * @param {string} token - JWT token
   * @returns {Promise<Object>} API response
   */
  static async deleteAccount(token) {
    try {
      const response = await axios.delete(API_BASE_URL, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Compress an image file to reduce size
   * @param {File} file - Image file to compress
   * @param {number} size - Target size in pixels (default: 200)
   * @param {number} quality - JPEG quality 0-1 (default: 0.7)
   * @returns {Promise<string>} Base64 encoded compressed image
   */
  static compressImage(file, size = 200, quality = 0.7) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = (e) => {
        const img = new Image();
        img.onload = () => {
          // Create canvas and resize to square
          const canvas = document.createElement('canvas');
          canvas.width = size;
          canvas.height = size;
          
          // Draw image on canvas (will auto-crop to square)
          const ctx = canvas.getContext('2d');
          ctx.drawImage(img, 0, 0, size, size);
          
          // Convert to compressed base64
          resolve(canvas.toDataURL('image/jpeg', quality));
        };
        
        img.onerror = reject;
        img.src = e.target.result;
      };
      
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
}
