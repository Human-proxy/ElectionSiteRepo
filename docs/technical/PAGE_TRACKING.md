# Page Tracking System

## Overview

The page tracking system automatically monitors which pages users visit in the application and persists this data to track their learning progress. This feature helps users see their exploration of the platform and motivates them to discover all available pages.

## Architecture

### Components

1. **Frontend (Vue.js)**
   - `usePageTracking` composable - Global route tracking
   - `LeerVoortgang.vue` component - Visual progress display
   - `ProfileService.js` - API communication
   - Router metadata - Page configuration

2. **Backend (Spring Boot)**
   - `ProfileController.java` - REST endpoints
   - `ProfileService.java` - Business logic
   - `UserEntity.java` - Database model with visited pages

### Database Structure

The system uses a separate table `user_visited_pages` to store visited pages:

```sql
CREATE TABLE user_visited_pages (
    user_id BIGINT NOT NULL,
    page_path VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Benefits of this approach:**
- Normalized database design
- Unlimited number of tracked pages
- Easy to query for analytics
- Scalable for future features

## How It Works

### 1. Route Configuration

Pages are configured in `/src/router/index.js` with metadata:

```javascript
const routes = [
    { 
        path: '/home', 
        component: HomePage, 
        meta: { 
            title: 'Home',           // Display name
            trackProgress: true      // Enable tracking
        } 
    },
    { 
        path: '/forum', 
        component: Forum, 
        meta: { 
            title: 'Forum', 
            trackProgress: true 
        } 
    },
    // Pages WITHOUT trackProgress are NOT tracked (e.g., login, register)
    { path: '/inloggen', component: Login },
]
```

### 2. Automatic Tracking

The `usePageTracking` composable is initialized in `App.vue`:

```javascript
// App.vue
import { usePageTracking } from '@/composables/usePageTracking';

const { trackCurrentPage } = usePageTracking();

onMounted(() => {
  trackCurrentPage(); // Track initial page
});
```

**Flow:**
1. User navigates to a page (e.g., `/home`)
2. `usePageTracking` detects route change via Vue Router
3. API call to `POST /api/profile/visit` with `{ pagePath: '/home' }`
4. Backend saves path to `user_visited_pages` table
5. Event `page-visited` is dispatched
6. `LeerVoortgang` component listens and refreshes data

### 3. Progress Display

The `LeerVoortgang.vue` component shows:
- **Progress bar**: Shows X/Y pages visited
- **Completed sections**: List of visited pages
- **Next sections**: Suggestions for unvisited pages

The component automatically fetches trackable routes:

```javascript
import { getTrackableRoutes } from '@/router';

const allPages = getTrackableRoutes(); // Auto-populates from router
```

### 4. Data Persistence

Visited pages are stored in the database and persist across:
- Page refreshes
- Logout/login sessions
- Different devices (same account)

## Adding New Tracked Pages

### Step 1: Create Your Component

```javascript
// src/components/NewPage.vue
<template>
  <div>Your new page content</div>
</template>
```

### Step 2: Add Route with Metadata

Edit `/src/router/index.js`:

```javascript
import NewPage from "@/components/NewPage.vue";

const routes = [
    // ... existing routes
    
    { 
        path: '/new-page',           // URL path
        component: NewPage,          // Component
        meta: { 
            title: 'New Page',       // Display name in progress tracker
            trackProgress: true      // IMPORTANT: Enable tracking
        } 
    },
]
```

**That's it!** The page is now automatically tracked. No other changes needed.

### What Happens Automatically

1. Route is accessible at `/new-page`
2. `getTrackableRoutes()` includes it in the progress list
3. Page visits are tracked and saved to database
4. Progress percentage updates automatically
5. "New Page" appears in completed sections when visited

## API Endpoints

### `GET /api/profile`
Fetch user profile including visited pages.

**Response:**
```json
{
  "id": "1",
  "username": "demo",
  "email": "demo@example.com",
  "profileImageUrl": "...",
  "createdAt": "2024-10-26T12:00:00",
  "visitedPages": ["/home", "/forum", "/results"],
  "roles": ["USER"]
}
```

### `POST /api/profile/visit`
Track a page visit.

**Request:**
```json
{
  "pagePath": "/home"
}
```

**Response:**
```json
{
  "id": "1",
  "username": "demo",
  "visitedPages": ["/home", "/forum", "/results", "/new-page"],
  // ... other user fields
}
```

**Features:**
- Automatically removes old `/` (root) entries
- Prevents duplicate entries (uses Set in Java)
- Returns updated user profile

## Configuration Reference

### Router Metadata Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `title` | String | Yes* | Display name in progress tracker |
| `trackProgress` | Boolean | Yes* | Enable tracking for this route |

*Required only for tracked pages

### Pages NOT Tracked

Pages without `trackProgress: true` are ignored:
- Login/Register pages
- Admin pages
- Error pages
- Redirect routes

**Example:**
```javascript
// These are NOT tracked
{ path: '/inloggen', component: Login },
{ path: '/', redirect: '/home' },
```

## Best Practices

### ✅ DO

- Add `trackProgress: true` to all main content pages
- Use descriptive `title` values (shown to users)
- Keep page paths simple and clean (e.g., `/forum` not `/forum-page-2024`)

### ❌ DON'T

- Track authentication pages (login/register)
- Track redirect routes
- Track admin-only pages
- Use duplicate titles for different pages

## Troubleshooting

### Page not showing in progress tracker

**Check:**
1. Route has `meta: { trackProgress: true }`
2. Route has a `title` in metadata
3. User is logged in
4. Browser console for errors

### Visits not persisting

**Check:**
1. User is authenticated (has valid JWT token)
2. Backend is running on `http://localhost:8080`
3. Database connection is working
4. Check backend logs for errors

### Progress shows 0/0

**Possible causes:**
- No routes have `trackProgress: true` metadata
- `getTrackableRoutes()` is not working
- Check browser console for errors


## File Structure

```
election-frontend/
├── src/
│   ├── components/
│   │   └── profile/
│   │       ├── LeerVoortgang.vue       # Progress display component
│   │       ├── ProfilePage.vue         # User profile page
│   │       └── ProfielInformatie.vue   # Profile information editor
│   ├── composables/
│   │   └── usePageTracking.js          # Tracking logic
│   ├── router/
│   │   └── index.js                    # Route configuration
│   ├── services/
│   │   └── profileService.js           # API calls
│   └── App.vue                         # Global tracking setup

election-backend/
├── src/main/java/nl/hva/.../
│   ├── controller/
│   │   └── ProfileController.java      # REST endpoints
│   ├── service/
│   │   └── ProfileService.java         # Business logic
│   └── entities/
│       └── UserEntity.java        # User model with visited pages
```

## Support

For questions or issues, contact the development team.
