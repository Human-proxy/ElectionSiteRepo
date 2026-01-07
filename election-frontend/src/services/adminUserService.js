import http from './http';

/**
 * AdminUserService
 * Provides admin operations for users: list & delete.
 */
export const AdminUserService = {
  /** Fetch all users (array of UserResponse). */
  list() {
    return http.get('/api/admin/users').then(r => r.data);
  },
  /** Delete a user by numeric id. */
  delete(id) {
    return http.delete(`/api/admin/users/${id}`);
  }
};
