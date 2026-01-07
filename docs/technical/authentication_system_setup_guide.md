# Authentication System
# Author: Aydin Maleki

This document explains how the login and register system is built and how to use it in both the backend (Spring Boot + Java) and the frontend (Vue 3 + Vuetify). 
It also includes the reasoning behind each choice so that the code is easy to understand and maintain.

---

## 1. Overview

The goal of this setup is to create a **secure login and registration system** using:

- **Spring Boot** for the backend  
- **JWT (JSON Web Token)** for authentication  
- **H2 (in-memory)** database for development  
- **Vue 3** with **Vuetify** for the frontend  

The user can register, log in, and access protected endpoints.  
Public pages (like the election results) stay open without a token.

---

## 2. Backend Structure

```
src/main/java/nl/hva/dederdekamer/
│
├─ election_backend/
│ ├─ controller/ # REST endpoints for login and register
│ ├─ dto/ # Request and response classes
│ ├─ entities/ # User and Role entities
│ ├─ repository/ # Spring Data repositories
│ ├─ service/ # Handles hashing, saving, validation
│ └─ security/ # JWT creation and validation
│
├─ config/
│ ├─ APIConfig.java # CORS settings for frontend (http://localhost:5173)
│ └─ SecurityConfig.java # Spring Security + JWT filter
│
└─ ElectionBackendApplication.java
```

---

## 3. Backend Explained

### 3.1 JWT (Json Web Token)

JWT is used to identify users after login.  
When a user logs in successfully, the backend returns a signed token.  
The frontend stores this token in `localStorage` and sends it with every request to protected routes.

- Each token is valid for **60 minutes** (configurable in `application.properties`).
- The backend checks the token signature and expiry before allowing access.

**Reason for choosing JWT:**
- Stateless (no sessions needed)
- Easy to verify
- Works perfectly with APIs and SPAs (single page apps)

---

### 3.2 CORS Configuration

**File:** `APIConfig.java`

Allows requests from the Vue development server (`http://localhost:5173`).  
Without this, the browser would block requests between frontend and backend.

```java
registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("*");
```

### 3.3 Security Configuration

**File:** `SecurityConfig.java`

- Disables CSRF for API use  
- Adds a JWT filter to check for tokens  
- Makes certain endpoints (like /api/auth/login and /api/auth/register) public  
- All other endpoints require a valid JWT

```java
.requestMatchers(
  "/api/auth/login",
  "/api/auth/register",
  "/h2-console/**",
  "/api/**"
).permitAll()
```

---

### 3.4 Database and Entities

Uses **H2 in-memory database**.  
That means every time you restart the backend, data resets — perfect for development and testing.

**User and Role entities** are managed by Spring Data JPA (Hibernate).

---

### 3.5 Passwords

Passwords are hashed using `BCryptPasswordEncoder`.  
This ensures plain text passwords are never stored in the database.

---

### 3.6 Auth Endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/auth/register | Creates a new user, hashes password, returns JWT |
| POST | /api/auth/login | Validates credentials, returns JWT |
| GET  | /api/auth/me | Returns current logged-in user (protected) |

Each successful call returns something like:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5...",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600,
  "user": {
    "id": 1,
    "username": "aydin",
    "email": "aydin@gmail.com",
    "role": "USER"
  }
}
```

---

## 4. Frontend Structure

```
src/
├─ services/
│  ├─ http.js            # Axios instance with JWT + 401 handling
│  └─ auth.service.js    # Login, register, and user endpoints
│
├─ utils/
│  └─ auth.js            # Store and retrieve token + user from localStorage
│
├─ components/auth/
│     ├─ LoginPage.vue      # Handles login form and logic
│     └─ RegisterPage.vue   # Handles register form and logic
│
```

---

## 5. Frontend Explained

### 5.1 http.js

This is the central HTTP client (using Axios).  
It automatically attaches the JWT to every request and listens for 401 errors.

**Why this design:**
- You don’t repeat headers or error handling everywhere  
- When a token expires, the app auto-logs out

```js
const token = getToken();
if (token) config.headers.Authorization = `Bearer ${token}`;
```

---

### 5.2 auth.service.js

Handles communication with backend endpoints.

```js
export const AuthService = {
  login({ identifier, password }) {
    return http.post("/api/auth/login", { identifier, password }).then(r => r.data);
  },
  register(data) {
    return http.post("/api/auth/register", data).then(r => r.data);
  },
  me() {
    return http.get("/api/auth/me").then(r => r.data);
  },
};
```

**Reason:**
Keeps components simple and readable.  
Easier to test and reuse in other pages.

---

### 5.3 auth.js (localStorage utility)

Stores and retrieves user and token data in the browser.

- `setAuth({token, user})` → saves both  
- `getToken()` → returns current token  
- `clearAuth()` → logs out user and clears storage  

Fires a global `"auth-changed"` event to update the navbar and pages.

**Why:**  
Centralized management of authentication state.  
Works even after page reloads.

---

### 5.4 Login Page

The login form accepts email or username (`identifier`) and password.

```js
const data = await AuthService.login({
  identifier: this.loginForm.identifier,
  password: this.loginForm.password,
});
setAuth({ token: data.token, user: data.user });
```

On success:
- JWT is saved in localStorage  
- Navbar updates (shows “Welcome, username”)  
- Redirects to the homepage

---

### 5.5 Register Page

Registration form sends:

```js
{
  username: "aydin",
  email: "aydin@gmsil.com",
  password: "Password123!",
  confirmPassword: "Password123!"
}
```

If registration succeeds:
- A JWT and user info are returned  
- The user is automatically logged in  
- A welcome message appears

**Reason for auto-login:**  
Better user experience (no need to log in again right after signing up)

---

### 5.6 Navbar Updates

The navbar checks `isLoggedIn()` and `getUser()` from `auth.js`.

**When logged in:**
- Login/Register buttons are hidden  
- “Logout” button and welcome text appear

**When logged out:**
- Returns to default state automatically (thanks to `"auth-changed"`)

---

## 6. Why These Choices

| Part | Reason |
|------|---------|
| JWT Auth | Lightweight, works without server sessions |
| Axios (http.js) | Cleaner request management + token auto-header |
| Service layer (auth.service.js) | Keeps components simple |
| LocalStorage utils (auth.js) | Easy persistent login |
| Validation on frontend & backend | Prevents bad input before and after request |
| Identifier field (email or username) | More user-friendly login experience |
| CORS config | Allows local Vue app to talk to backend safely |

---
