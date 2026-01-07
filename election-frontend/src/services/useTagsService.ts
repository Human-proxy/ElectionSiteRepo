export async function getTags() {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/tag/findTags`);
    if (!res.ok) throw new Error('Failed to fetch tags');
    return res.json();
}