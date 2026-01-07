import { watch } from 'vue';
import { useRoute } from 'vue-router';
import { ProfileService } from '@/services/profileService';
import { getToken, isLoggedIn } from '@/utils/auth';

/**
 * Composable for tracking page visits across the application
 */
export function usePageTracking() {
  const route = useRoute();
  // Helper: check if current route is trackable using the matched route records
  function isTrackable() {
    return route.matched.some(r => r.meta && r.meta.trackProgress);
  }

  // Track current page on mount
  const trackCurrentPage = async () => {
    if (!isLoggedIn()) return;
  if (!isTrackable()) return;
    try {
      const token = getToken();
      await ProfileService.trackPageVisit(token, route.path);
      // Dispatch event to update LeerVoortgang component
      window.dispatchEvent(new Event('page-visited'));
    } catch (error) {
      // Silently ignore tracking errors
    }
  };

  // Watch for route changes and track new pages
  watch(() => route.path, async (newPath) => {
    if (!isLoggedIn()) return;
    if (!isTrackable()) return;
    try {
      const token = getToken();
      await ProfileService.trackPageVisit(token, newPath);
      // Dispatch event to update LeerVoortgang component
      window.dispatchEvent(new Event('page-visited'));
    } catch (error) {
      // Silently ignore tracking errors
    }
  });

  return {
    trackCurrentPage
  };
}
