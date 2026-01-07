<template>
  <div class="community-tab">
    <!-- Header with Icon and Navigation -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold flex items-center gap-2">
        <!-- MessageCircle Icon -->
        <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z">
          </path>
        </svg>
        Wat Beweegt Studenten? 💬
      </h3>

      <!-- Navigation Controls -->
      <div v-if="!loading && !error && posts.length > 0" class="flex items-center gap-2">
        <button @click="previousPost" :disabled="currentIndex === 0"
          class="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
        </button>

        <span class="text-sm text-gray-600 min-w-[60px] text-center">
          {{ currentIndex + 1 }} / {{ posts.length }}
        </span>

        <button @click="nextPost" :disabled="currentIndex === posts.length - 1"
          class="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-500">Posts laden...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-12">
      <p class="text-red-500">{{ error }}</p>
    </div>

    <!-- No Posts State -->
    <div v-else-if="posts.length === 0" class="text-center py-12">
      <p class="text-gray-500">Nog geen posts beschikbaar.</p>
    </div>

    <!-- Forum Content Box - Show current post -->
    <div v-else
      @click="goToPostDetail(posts[currentIndex].id)"
      class="forum-content-box bg-white rounded-lg border border-gray-200 p-8 mb-6 hover:shadow-md transition-all duration-300 cursor-pointer group">
      <div class="space-y-5 text-left">
        <!-- Post Title with Icon -->
        <div class="flex items-start gap-3">
          <h3 class="font-semibold text-lg group-hover:text-blue-600 transition-colors leading-relaxed flex-1 ml-8">
            {{ posts[currentIndex].title }}
          </h3>
        </div>

        <!-- Post Content -->
        <p class="text-gray-600 leading-relaxed" style="padding-left: 32px;">
          {{ posts[currentIndex].content }}
        </p>

        <!-- Post Meta Info -->
        <div class="flex flex-wrap items-center gap-3 text-sm text-gray-500 pl-8 mt-6">
          <span class="font-medium">@{{ posts[currentIndex].author?.username || 'Anonymous' }}</span>
          <span>•</span>
          <span>{{ getTimeAgo(posts[currentIndex].created) }}</span>
          <div class="flex items-center gap-5 ml-2">
            <!-- Replies -->
            <div class="flex items-center gap-1.5">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z">
                </path>
              </svg>
              <span>{{ getCommentCount(posts[currentIndex].id) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Community CTA Box -->
    <div class="cta-box bg-gradient-to-r from-purple-50 to-orange-50 border-2 border-purple-200 rounded-lg p-8">
      <div class="text-center">
        <h3 class="text-xl font-bold text-gray-900 mb-4 mt-5">
          Word Onderdeel van de Community! 🚀
        </h3>
        <p class="text-gray-600 mb-6 leading-relaxed text-lg">
          Stel vragen, deel je mening, en leer van andere studenten.
        </p>
        <div class="flex flex-col sm:flex-row gap-3 justify-center mb-4">
          <!-- Join de Community Button -->
          <RouterLink to="/registreren"
            class="join-button bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 text-white px-6 py-3 rounded-lg font-medium flex items-center justify-center gap-2 transition-all duration-300">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z">
              </path>
            </svg>
            Join de Community
          </RouterLink>

          <!-- Stel een vraag Button -->
          <button
            @click="startDiscussion"
            class="ask-button bg-white border-2 border-gray-300 hover:border-purple-400 hover:bg-gray-50 text-gray-700 px-6 py-3 rounded-lg font-medium flex items-center justify-center gap-2 transition-all duration-300">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z">
              </path>
            </svg>
            Start een discussie 
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// Router
const router = useRouter()

// Reactive data
const posts = ref([])
const loading = ref(true)
const error = ref(null)
const currentIndex = ref(0)
const commentCounts = ref({})

// Maximum number of posts to show in carousel
const MAX_POSTS = 5

// Fetch posts from backend
const fetchPosts = async () => {
  try {
    loading.value = true
    error.value = null

    const response = await fetch(`${import.meta.env.VITE_API_URL}/posts?page=0&size=${MAX_POSTS}`)

    if (!response.ok) {
      throw new Error('Failed to fetch posts')
    }

    const data = await response.json()
    // Spring Data returns a Page object with content array
    posts.value = data.content || []
    
    // Stop loading immediately so posts are visible
    loading.value = false
    
    // Fetch comment counts in the background (non-blocking)
    fetchCommentCounts()
  } catch (err) {
    console.error('Error fetching posts:', err)
    error.value = 'Kon posts niet laden. Probeer het later opnieuw.'
    loading.value = false
  }
}

// Fetch comment counts for all posts (parallel for better performance)
const fetchCommentCounts = async () => {
  const promises = posts.value.map(async (post) => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/comment/count?postId=${post.id}`)
      if (response.ok) {
        return { id: post.id, count: await response.json() }
      }
      return { id: post.id, count: 0 }
    } catch (err) {
      console.error(`Error fetching comment count for post ${post.id}:`, err)
      return { id: post.id, count: 0 }
    }
  })
  
  const results = await Promise.all(promises)
  const counts = {}
  results.forEach(result => {
    counts[result.id] = result.count
  })
  commentCounts.value = counts
}

// Get comment count for a specific post
const getCommentCount = (postId) => {
  return commentCounts.value[postId] || 0
}

// Navigate to post detail page
const goToPostDetail = (postId) => {
  router.push(`/detail/${postId}`)
}

const startDiscussion = () => {
  router.push('/forum/post')
}

// Navigation functions
const nextPost = () => {
  if (currentIndex.value < posts.value.length - 1) {
    currentIndex.value++
  }
}

const previousPost = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

// Time ago function
const getTimeAgo = (timestamp) => {
  if (!timestamp) {
    return 'Recent geplaatst'
  }
  
  const now = new Date()
  let postDate
  
  // Handle different timestamp formats
  if (typeof timestamp === 'number') {
    // Unix timestamp in milliseconds
    postDate = new Date(timestamp)
  } else if (typeof timestamp === 'string') {
    // ISO string or other date string
    postDate = new Date(timestamp)
  } else if (timestamp instanceof Date) {
    postDate = timestamp
  } else {
    return 'Recent geplaatst'
  }
  
  // Check if date is valid
  if (isNaN(postDate.getTime())) {
    return 'Recent geplaatst'
  }
  
  const diffInMs = now - postDate
  const diffInMinutes = Math.floor(diffInMs / (1000 * 60))
  const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60))
  const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24))
  
  if (diffInMinutes < 1) {
    return 'Zojuist geplaatst'
  } else if (diffInMinutes < 60) {
    return `${diffInMinutes} ${diffInMinutes === 1 ? 'minuut' : 'minuten'} geleden`
  } else if (diffInHours < 24) {
    return `${diffInHours} ${diffInHours === 1 ? 'uur' : 'uur'} geleden`
  } else if (diffInDays < 7) {
    return `${diffInDays} ${diffInDays === 1 ? 'dag' : 'dagen'} geleden`
  } else {
    // Format date as DD-MM-YYYY for posts older than 7 days
    const day = postDate.getDate().toString().padStart(2, '0')
    const month = (postDate.getMonth() + 1).toString().padStart(2, '0')
    const year = postDate.getFullYear()
    return `${day}-${month}-${year}`
  }
}

// Fetch posts when component is mounted
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.community-tab {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>