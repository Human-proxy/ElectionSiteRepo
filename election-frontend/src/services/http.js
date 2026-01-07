/**
 * Axios instance for backend calls.
 * - Adds Bearer token header if present
 * - Clears auth on 401 and notifies app
 */
import axios from "axios";
import { getToken, clearAuth } from "@/utils/auth";

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  withCredentials: false,
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err?.response?.status === 401) {
      clearAuth();
      window.dispatchEvent(new Event("auth-changed"));
    }
    return Promise.reject(err);
  }
);

export default http;
