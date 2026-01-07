/**
 * Auth service: wraps login/register endpoints.
 * Keeps components slim and testable.
 */
import http from "./http";

export const AuthService = {

  /**
   * Log in with either email OR username.
   * @param {{ identifier: string, password: string }} data
   * @returns {Promise<{token:string, tokenType:string, expiresInSeconds:number, user:object}>}
   */
  login({ identifier, password }) {
    return http.post("/api/auth/login", { identifier, password }).then(r => r.data);
  },


  /**
   * @param {{username:string,email:string,password:string,confirmPassword:string}} data
   * @returns {Promise<{token:string,user:object}>}
   */
  register(data) {
    return http.post("/api/auth/register", data).then((r) => r.data);
  },

  /**  get current user from backend */
  me() {
    return http.get("/api/auth/me").then((r) => r.data);
  },

  /**
   * Reactivate a soft-deleted account.
   * @returns {Promise<any>}
   */
  reactivateAccount() {
    return http.post("/api/auth/reactivate").then((r) => r.data);
  },
};
