export async function getCommentTotal(id)  {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/comment/count?postId=${id}`);
    if (!res.ok) throw new Error('Failed to fetch amount of comments');
    return  res.json();
}