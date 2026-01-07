/**
 * Tiny auth storage helper around localStorage.
 * Stores:
 *  - 'auth.token' : the JWT string
 *  - 'auth.user'  : the user object from backend
 *
 * Emits a global 'auth-changed' event whenever auth is updated/cleared.
 */

/** @returns {string|null} JWT token or null */
export function getToken() {
  return localStorage.getItem("auth.token");
}

/** @returns {object|null} user object or null */
export function getUser() {
  const raw = localStorage.getItem("auth.user");
  try { return raw ? JSON.parse(raw) : null; } catch { return null; }
}

/** @returns {boolean} whether a token exists */
export function isLoggedIn() {
  return Boolean(getToken());
}

/**
 * Persist token + user in localStorage and notify the app.
 * @param {{token:string, user:object}} payload
 */
export function setAuth(payload) {
  if (payload?.token) localStorage.setItem("auth.token", payload.token);
  if (payload?.user)  localStorage.setItem("auth.user", JSON.stringify(payload.user));
  window.dispatchEvent(new Event("auth-changed"));
}

/** Clear all auth state and notify the app. */
export function clearAuth() {
  localStorage.removeItem("auth.token");
  localStorage.removeItem("auth.user");
  window.dispatchEvent(new Event("auth-changed"));
}

/** Backwards-compatible helpers */
export const setUser = (user) => setAuth({ token: getToken(), user });
export const clearUser = () => clearAuth();
