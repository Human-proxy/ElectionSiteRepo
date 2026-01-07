import {ref} from "vue";
import standartAvatar from '@/assets/default-avatar.jpg'
export function useAvatar() {

    const pfp = ref();
    const avatarUrl = async (author) =>{
    if (!author) {
        pfp.value = standartAvatar
    }
    else if (author.deletedAt) {
        pfp.value = standartAvatar;
    }
    else if (author.profileImageUrl) {
        pfp.value = author.profileImageUrl;
    }
    else {
        pfp.value = `https://ui-avatars.com/api/?name=${encodeURIComponent(author.username)}&background=random`
    }

}
    return {
        pfp,
        avatarUrl
    }
}