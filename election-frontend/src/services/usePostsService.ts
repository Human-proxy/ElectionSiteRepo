export async function getPosts(page = 0, size = 10) {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/posts?page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch posts');
    return res.json();
}
export async function getPostsByTag(tag, page = 0, size = 10) {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/posts/search?tag=${encodeURIComponent(tag)}&page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch Tagged posts');
    return res.json();
}

// Use axios instance so Authorization header is sent for protected endpoints
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import http from './http';

export async function deletePost(id: number) {
    // Use absolute URL so baseURL is ignored and we align with VITE_API_URL
    return http.delete(`${import.meta.env.VITE_API_URL}/v1/posts/${id}`).then(r => r.status);
}
