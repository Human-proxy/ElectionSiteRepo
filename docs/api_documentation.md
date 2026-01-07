# API Documentation

---

#  Authentication & Accounts
**Base URLs:** `/api/auth`, `/api/verification`, `/api/password-reset`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| POST | `/api/auth/register` | `{ "username": "user1", "email": "test@example.com", "password": "securePass123" }` | ❌ | Register new account (email verification required). |
| POST | `/api/auth/login` | `{ "username": "user1", "password": "securePass123" }` | ❌ | Login and receive JWT. |
| GET | `/api/auth/me` | *None* | ✅ | Get logged-in user details. |
| POST | `/api/verification/verify` | `{ "username": "user1", "code": "1234" }` | ❌ | Verify email and activate account. |
| POST | `/api/verification/resend` | `{ "username": "user1" }` | ❌ | Resend verification code. |
| POST | `/api/password-reset/request` | `{ "email": "test@example.com" }` | ❌ | Request password reset email. |
| GET | `/api/password-reset/validate/{token}` | **Path:** token(UUID) | ❌ | Validate reset token. |
| POST | `/api/password-reset/reset` | `{ "token": "uuid...", "newPassword": "newPass123" }` | ❌ | Reset password. |

---

# User Profile Management
**Base URL:** `/api/profile`
*Requires:* `Authorization: Bearer <token>`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/profile` | *None* | ✅ | Retrieve user profile data. |
| PUT | `/api/profile` | `{ "username": "new_name", "email": "new@mail.com" }` | ✅ | Update username/email (returns new JWT if username changes). |
| PUT | `/api/profile/image` | `{ "profileImageUrl": "https://example.com/img.png" }` | ✅ | Update avatar image URL. |
| PUT | `/api/profile/password` | `{ "currentPassword": "oldPass", "newPassword": "newPass" }` | ✅ | Change password. |
| POST | `/api/profile/visit` | `{ "pagePath": "/dashboard" }` | ✅ | Track profile visits. |
| DELETE | `/api/profile` | *None* | ✅ | Delete account permanently. |

---

# Election Quiz
**Base URL:** `/api/quiz`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/quiz/questions` | **Query:** `?year=2023` | ❌ | Retrieve quiz questions. |
| POST | `/api/quiz/result` | `{ "year": "2023", "partyId": "GL-PVDA", "region": "Amsterdam", "dataType": "votes" }` | ❌ | Calculate quiz result. |
| POST | `/api/quiz/export` | Same as `/result` payload | ❌ | Export CSV file. |

---

# Election Data — Municipalities
**Base URL:** `/api`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/elections/TK2023/municipalities/{id}` | **Path:** id | ❌ | Municipality details (TK2023). |
| GET | `/api/elections/{electionId}/municipalities` | **Path:** electionId | ❌ | List all municipalities. |
| GET | `/api/elections/{electionId}/municipalities/{name}/results` | **Path:** electionId, name | ❌ | Results for specific municipality. |
| GET | `/api/elections/{electionId}/municipalities/winners` | **Path:** electionId | ❌ | Winning party per municipality. |

---

#  Election Data — Statistics & Charts
**Base URL:** `/api`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/party-data` | *None* | ❌ | Full party dataset. |
| GET | `/api/parties/homepage` | **Query:** `?electionId=TK2023` | ❌ | Only parties with seats > 0. |
| GET | `/api/elections/compare` | **Query:** `?year1=2017&year2=2023` | ❌ | Compare two years. |
| GET | `/api/election/top4` | **Query:** `?electionId=TK2023` | ❌ | Top 4 largest constituencies. |
| GET | `/api/elections` | *None* | ❌ | List available elections. |
| GET | `/api/elections/metadata/{electionId}` | **Path:** electionId | ❌ | Metadata for election. |

---

#  Community — Posts & Comments
**Base URLs:** `/api/posts`, `/api/comment`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/posts` | **Query:** `?page=0&size=10` | ❌ | Get posts page. |
| GET | `/api/posts/{id}` | **Path:** id | ❌ | Get specific post. |
| POST | `/api/posts` | `{ "title": "My Post", "content": "Hello world" }` | ✅ | Create a new post. |
| GET | `/api/comment/find` | **Query:** `?postId=1` | ❌ | Comments for post. |
| GET | `/api/comment/count` | **Query:** `?postId=1` | ❌ | Count comments. |
| POST | `/api/comment` | `{ "content": "Nice!", "post": { "id": 1 } }` | ✅ | Add comment. |
| DELETE | `/api/comment/{id}` | **Path:** id | ❌ | Delete comment. |

---

#  Admin Operations
**Base URL:** `/api/admin`

| Method | Endpoint | Payload / Params | Auth | Description |
|-------|----------|------------------|:---:|-------------|
| GET | `/api/admin/users` | *None* | ✅ | List all users. |
| DELETE | `/api/admin/users/{id}` | **Path:** id | ✅ | Force delete user. |

---

