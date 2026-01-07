import {computed, ref} from "vue";
import {getPosts} from "@/services/usePostsService.ts";
import {getPostsByTag} from "@/services/usePostsService";
export function usePosts() {
    const posts = ref([]);
    const loading = ref(true)
    const error = ref<string | null>(null);
    const currentPage = ref(0);
    const totalPages = ref(1);

    const readPost = async (page = 0) => {
        loading.value = true;
        error.value = null;
        try {
            const data = await getPosts(page, 10);
            posts.value = Array.isArray(data) ? data : (data.content ?? []);
            console.log(posts.value);
            currentPage.value = data.number;
            totalPages.value = data.totalPages;
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        } catch (err) {
            error.value = err ?? "Error er ging iets mis bij het laden.";
        } finally {
            loading.value = false
        }
    };

    const readPostsByTag = async (tag: string, page = 0) => {
        loading.value = true;
        error.value = null;

        try {
            const data = await getPostsByTag(tag, page, 10);
            posts.value = Array.isArray(data) ? data : (data.content ?? []);
            currentPage.value = data.number;
            totalPages.value = data.totalPages;
            console.log(posts.value);

            window.scrollTo({ top: 0, behavior: "smooth" });
        } catch (err) {
            error.value = err ?? "Error er ging iets mis bij het laden.";
        } finally {
            loading.value = false;
        }
    };

    const isEmpty = computed(() => { return !loading.value && !error.value && posts.value.length === 0});

    return {
        posts,
        loading,
        error,
        isEmpty,
        currentPage,
        totalPages,
        readPost,
        readPostsByTag,
    };
}