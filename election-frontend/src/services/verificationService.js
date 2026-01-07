import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL + '/verification';

export const VerificationService = {
  /**
   * Verify email with 4-digit code using username (PUBLIC - no auth required)
   * @param {string} username - Username from registration
   * @param {string} code - 4-digit verification code
   * @returns {Promise<{token: string, expiresIn: number, user: object}>}
   */
  async verifyEmail(username, code) {
    const response = await axios.post(
      `${API_URL}/verify`,
      { username, code }
    );
    return response.data;
  },

  /**
   * Resend verification code (generates new code, invalidates old one)
   * @param {string} username - Username from registration
   * @returns {Promise<{success: boolean, message: string, expiresAt: string}>}
   */
  async resendCode(username) {
    const response = await axios.post(
      `${API_URL}/resend`,
      { username }
    );
    return response.data;
  }
};
