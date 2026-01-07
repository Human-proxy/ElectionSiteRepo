import {computed, ref} from "vue";
import {getCommentTotal} from "@/services/useCommentCountService";

export function useCommentCount()
{
    const numberOfComments = ref();

    const commentTotal = async (id) => {
        try {
            try {
                const data = await getCommentTotal(id);
                numberOfComments.value = data;
            } catch (err) {
                console.error(err);
                numberOfComments.value = 0;
            }

        } catch (err) {
            console.error(err);
        }
    }
    return {
        numberOfComments,
        commentTotal
    };
}
