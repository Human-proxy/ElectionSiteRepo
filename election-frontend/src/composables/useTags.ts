import {computed, ref} from "vue";
import {getTags} from "@/services/useTagsService";

export function useTags() {
    const tags = ref([]);
    const error = ref<string | null>(null);


    const readTags = async ()=> {
        try {
            const data = await getTags();
            tags.value = Array.isArray(data) ? data : (data.content ?? []);
console.log(tags.value);
        }
     catch (err) {
         error.value = err ?? "Error er ging iets mis bij het laden.";

     }
    };


     return {
             tags,
             error,
             readTags,
     };
}