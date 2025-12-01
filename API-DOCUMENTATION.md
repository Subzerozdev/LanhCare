# 📚 LanhCare API Documentation

## 🚀 Quick Start

### Base URL
```
Local Development: http://localhost:8080
Production: https://api.lanhcare.com
```

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
```
http://localhost:8080/v3/api-docs
```

---

## 🔐 Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Get JWT Token

**Option 1: Register**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "fullname": "John Doe",
  "password": "password123"
}
```

**Option 2: Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Option 3: Google OAuth**
```http
POST /api/auth/google
Content-Type: application/json

{
  "idToken": "<google-id-token>"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "fullname": "John Doe",
  "role": "USER"
}
```

---

## 📋 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login with email/password | ❌ |
| POST | `/api/auth/google` | Login with Google OAuth | ❌ |
| GET | `/api/auth/health` | Auth service health check | ❌ |

### Account Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/accounts/me` | Get current user's account | ✅ |
| GET | `/api/accounts/{id}` | Get account by ID | ✅ |
| GET | `/api/accounts` | Get all accounts | ✅ (ADMIN) |
| GET | `/api/accounts/active` | Get active accounts | ✅ (ADMIN) |
| PUT | `/api/accounts/{id}` | Update account | ✅ |
| DELETE | `/api/accounts/{id}` | Delete account | ✅ (ADMIN) |

### Health Profiles

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/health-profiles/accounts/{accountId}` | Create health profile | ✅ |
| GET | `/api/health-profiles/accounts/{accountId}` | Get health profile | ✅ |
| PUT | `/api/health-profiles/accounts/{accountId}` | Update health profile | ✅ |
| DELETE | `/api/health-profiles/accounts/{accountId}` | Delete health profile | ✅ |

### Food Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/foods` | Get all approved foods | ✅ |
| GET | `/api/foods/{id}` | Get food by ID | ✅ |
| GET | `/api/foods/search?name={name}` | Search foods by name | ✅ |
| GET | `/api/foods/types/{typeId}` | Get foods by type | ✅ |

### Meal Logging

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/meal-logs/accounts/{accountId}` | Create meal log | ✅ |
| GET | `/api/meal-logs/accounts/{accountId}` | Get all meal logs | ✅ |
| GET | `/api/meal-logs/accounts/{accountId}/date/{date}` | Get logs by date | ✅ |
| GET | `/api/meal-logs/accounts/{accountId}/range` | Get logs by date range | ✅ |
| PUT | `/api/meal-logs/{id}` | Update meal log | ✅ |
| DELETE | `/api/meal-logs/{id}` | Delete meal log | ✅ |

---

## 📝 Example Requests

### 1. Register and Login

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "fullname": "New User",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "password": "password123"
  }'
```

### 2. Create Health Profile

```bash
curl -X POST http://localhost:8080/api/health-profiles/accounts/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dateOfBirth": "1990-01-15",
    "gender": "MALE",
    "heightCm": 175.0,
    "currentWeightKg": 70.0,
    "targetWeightKg": 65.0,
    "activityLevel": "MODERATE",
    "medicalConditions": "None",
    "allergies": "Peanuts"
  }'
```

### 3. Search Foods

```bash
curl -X GET "http://localhost:8080/api/foods/search?name=cơm" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Log a Meal

```bash
curl -X POST http://localhost:8080/api/meal-logs/accounts/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "mealDate": "2024-11-29",
    "mealTime": "12:30:00",
    "mealType": "LUNCH",
    "foodItemId": 1,
    "servings": 1.5,
    "notes": "Lunch at office"
  }'
```

### 5. Get Meal Logs by Date Range

```bash
curl -X GET "http://localhost:8080/api/meal-logs/accounts/1/range?startDate=2024-11-01&endDate=2024-11-30" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔒 Google OAuth Setup

### 1. Get Google OAuth Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Go to Credentials → Create Credentials → OAuth 2.0 Client ID
5. Configure OAuth consent screen
6. Add authorized JavaScript origins:
   - `http://localhost:3000` (Next.js dev)
   - Your production domain
7. Copy Client ID and Client Secret

### 2. Update application.properties

```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

### 3. Next.js Integration Example

```typescript
// Install: npm install @react-oauth/google

import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';

function LoginPage() {
  const handleGoogleSuccess = async (credentialResponse) => {
    const response = await fetch('http://localhost:8080/api/auth/google', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken: credentialResponse.credential })
    });
    
    const data = await response.json();
    localStorage.setItem('token', data.accessToken);
    // Redirect to dashboard
  };

  return (
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      <GoogleLogin
        onSuccess={handleGoogleSuccess}
        onError={() => console.log('Login Failed')}
      />
    </GoogleOAuthProvider>
  );
}
```

---

## 🎯 Next.js Integration Guide

### 1. Create API Client

```typescript
// lib/api.ts
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export class ApiClient {
  private token: string | null = null;

  setToken(token: string) {
    this.token = token;
    localStorage.setItem('token', token);
  }

  async request(endpoint: string, options: RequestInit = {}) {
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      throw new Error(`API Error: ${response.statusText}`);
    }

    return response.json();
  }

  // Auth
  async login(email: string, password: string) {
    return this.request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
  }

  async register(email: string, fullname: string, password: string) {
    return this.request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ email, fullname, password }),
    });
  }

  // Health Profile
  async createHealthProfile(accountId: number, data: any) {
    return this.request(`/api/health-profiles/accounts/${accountId}`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Meal Logs
  async getMealLogs(accountId: number) {
    return this.request(`/api/meal-logs/accounts/${accountId}`);
  }

  async createMealLog(accountId: number, data: any) {
    return this.request(`/api/meal-logs/accounts/${accountId}`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }
}

export const api = new ApiClient();
```

### 2. Use in Components

```typescript
// app/login/page.tsx
'use client';

import { useState } from 'react';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const data = await api.login(email, password);
      api.setToken(data.accessToken);
      router.push('/dashboard');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
      />
      <button type="submit">Login</button>
    </form>
  );
}
```

---

## 🛡️ Security Notes

### JWT Token
- Token expires in 24 hours (configurable in `application.properties`)
- Store token securely (HttpOnly cookies recommended for production)
- Always use HTTPS in production

### Password Security
- Passwords are hashed using BCrypt
- Minimum password length: 6 characters
- Never store plain text passwords

### CORS Configuration
- Currently allows `localhost:3000` and `localhost:3001`
- Update `SecurityConfig.java` to add your production domain

---

## 📊 Response Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content (successful deletion) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (invalid/missing token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (e.g., email already exists) |
| 500 | Internal Server Error |

---

## 📞 Support

For issues or questions:
- Check [Swagger UI](http://localhost:8080/swagger-ui.html) for detailed endpoint documentation
- Review error responses for specific validation messages
- Check application logs for debugging

---

*Last updated: November 29, 2024*
