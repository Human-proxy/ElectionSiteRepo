import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn, getUser } from '@/utils/auth'
import HomePage from "@/components/HomePage.vue";
import LayoutComponent from "@/components/LayoutComponent/LayoutComponent.vue";
import ElectionResults from "@/components/ElectionResults.vue";
import Inloggen from "@/components/auth/LoginPage.vue";
import Registreren from "@/components/auth/RegisterPage.vue";
import WachtwoordVergeten from "@/components/auth/WachtwoordVergeten.vue";
import WachtwoordResetten from "@/components/auth/WachtwoordResetten.vue";
import ReactivateAccount from "@/components/auth/ReactivateAccount.vue";
import forum from "@/components/Forum/Forum.vue"
import PostDiscussion from "@/components/Forum/PostDiscussion.vue";
import Detailpage from "@/components/Forum/Detailpage.vue";
import ProfilePage from "@/components/Profile/ProfilePage.vue";
import CompareChart from "@/components/VergelijkingPagina/CompareChart.vue";
import AdminUsersComponent from "@/components/admin/Users/AdminUsersComponent.vue";
import AdminForumsComponent from "@/components/admin/Forums/AdminForumsComponent.vue";
import QuizPage from "@/components/Quiz/QuizPage.vue";
import LearnSystemPage from "@/components/LearnSystemPage.vue";
import NewsPage from "@/components/NewsPage.vue";
import AboutPage from "@/components/AboutPage.vue";


const routes = [
    { path: '/', redirect: '/home' },
    { path: '/home', component: HomePage, meta: { title: 'Home', trackProgress: true } },
    { path: '/homepage', component: HomePage },
    { path: '/layout', component: LayoutComponent },
    { path: '/results', component: ElectionResults, meta: { title: 'Verkiezingsresultaten', trackProgress: true } },
    { path: '/election-results', component: ElectionResults },
    { path: '/leer', component: LearnSystemPage, meta: { title: 'Leer over het systeem', trackProgress: true } },
    { path: '/inloggen', component: Inloggen },
    { path: '/registreren', component: Registreren },
    { path: '/wachtwoord-vergeten', component: WachtwoordVergeten },
    { path: '/wachtwoord-resetten', component: WachtwoordResetten },
    { path: '/reactivate-account', component: ReactivateAccount, meta: { requiresAuth: true } },
    { path: '/forum', component: forum, meta: { title: 'Forum', trackProgress: true } },
    { path: '/forum/post', component: PostDiscussion, meta: { requiresAuth: true } },
    { path: '/detail/:id', component: Detailpage },
    { path: '/profiel', component: ProfilePage, meta: { title: 'Profiel', trackProgress: true, requiresAuth: true } },
    { path: '/vergelijk', component: CompareChart, meta: { title: 'Vergelijk', trackProgress: true } },
    { path: '/quiz', component: QuizPage, meta: { title: 'Verkiezingsquiz', trackProgress: true } },
    { path: '/nieuws', component: NewsPage, meta: { title: 'Nieuws', trackProgress: true } },
    { path: '/over-ons', component: AboutPage, meta: { title: 'Over Ons', trackProgress: true } },

    // admin routes:
    { path: '/beheerder', component: AdminUsersComponent, meta: { title: 'Gebruikers beheren', trackProgress: false, requiresAdmin: true } },
    { path: '/admin/users', component: AdminUsersComponent, meta: { title: 'Gebruikers beheren', trackProgress: false, requiresAdmin: true } },
    { path: '/admin/forums', component: AdminForumsComponent, meta: { title: 'Admin Forums (test)', trackProgress: false, requiresAdmin: true } },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        // If there's a saved position (browser back/forward), use it
        if (savedPosition) {
            return savedPosition
        }
        // If there's a hash, don't scroll - let the component handle it
        if (to.hash) {
            return false
        }
        // Otherwise scroll to top
        return { top: 0 }
    }
})

// Navigation guard to protect routes that require authentication and admin-only pages
router.beforeEach((to, from, next) => {
    // Admin-only routes: check first so we don't call next() twice
    if (to.meta?.requiresAdmin) {
        if (!isLoggedIn()) {
            next('/inloggen');
            return;
        }
        const user = getUser();
        const isAdmin = Boolean(user && Array.isArray(user.roles) && user.roles.includes('ADMIN'));
        if (!isAdmin) {
            next('/home');
            return;
        }
    }

    // If route requires authentication but the user is not logged in -> go to login
    if (to.meta?.requiresAuth && !isLoggedIn()) {
        next('/inloggen');
        return;
    }

    if ((to.path === '/inloggen' || to.path === '/registreren') && isLoggedIn()) {
            next('/home');
            return;
        }

    // Allow navigation
    next();
});

// Export helper function to get trackable routes 
export function getTrackableRoutes() {
    return routes
        .filter(route => route.meta?.trackProgress)
        .map(route => ({
            label: route.meta.title,
            path: route.path
        }));
}

export default router