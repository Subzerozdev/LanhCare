# ⚡ LanhCare API - Quick Start Guide

## 🎯 Bắt Đầu Trong 5 Phút

### Bước 1: Start Application
```bash
# Double-click hoặc chạy:
start.bat
```

Đợi 2-3 phút để services khởi động.

### Bước 2: Mở Swagger UI
Mở browser tại: **http://localhost:8080/swagger-ui.html**

### Bước 3: Test API

#### Cách 1: Dùng Swagger UI (Khuyến khích)

1. Mở http://localhost:8080/swagger-ui.html
2. Tìm section **"Authentication"**
3. Click vào **POST /api/auth/register**
4. Click **"Try it out"**
5. Nhập:
```json
{
  "email": "demo@lanhcare.com",
  "fullname": "Demo User",
  "password": "password123"
}
```
6. Click **Execute**
7. Copy `accessToken` từ response
8. Click nút **"Authorize"** (góc trên bên phải)
9. Nhập: `Bearer YOUR_TOKEN_HERE`
10. Click **Authorize**
11. Bây giờ bạn có thể test tất cả endpoints! ✅

#### Cách 2: Dùng Script
```bash
test-api.bat
```

#### Cách 3: Dùng cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"fullname\":\"Test User\",\"password\":\"password123\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@lanhcare.com\",\"password\":\"password123\"}"
```

**Get Foods (cần token):**
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/foods
```

---

## 📋 Available Test Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@lanhcare.com | password123 | ADMIN |
| user1@lanhcare.com | password123 | USER |
| user2@lanhcare.com | password123 | USER |
| doctor@lanhcare.com | password123 | DOCTOR |

**Note:** Passwords trong database đã được hash. Để test, bạn cần register user mới hoặc update password trong DB.

---

## 🔥 Quick API Examples

### 1️⃣ Authentication

**Register:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "fullname": "New User",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": 6,
  "email": "newuser@example.com",
  "fullname": "New User",
  "role": "USER"
}
```

### 2️⃣ Create Health Profile

```http
POST http://localhost:8080/api/health-profiles/accounts/6
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "dateOfBirth": "1995-05-15",
  "gender": "FEMALE",
  "heightCm": 165,
  "currentWeightKg": 60,
  "targetWeightKg": 55,
  "activityLevel": "MODERATE"
}
```

**Response:** (BMI tự động tính!)
```json
{
  "id": 3,
  "accountId": 6,
  "dateOfBirth": "1995-05-15",
  "age": 29,
  "gender": "FEMALE",
  "heightCm": 165,
  "currentWeightKg": 60,
  "targetWeightKg": 55,
  "bmi": 22.0,
  "bmiCategory": "Normal weight",
  "activityLevel": "MODERATE"
}
```

### 3️⃣ Search Foods

```http
GET http://localhost:8080/api/foods/search?name=cơm
Authorization: Bearer YOUR_TOKEN
```

### 4️⃣ Log a Meal

```http
POST http://localhost:8080/api/meal-logs/accounts/6
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "mealDate": "2024-11-29",
  "mealTime": "12:00:00",
  "mealType": "LUNCH",
  "foodItemId": 1,
  "servings": 1.5,
  "notes": "Lunch at home"
}
```

**Response:** (Calories tự động tính!)
```json
{
  "id": 7,
  "accountId": 6,
  "mealDate": "2024-11-29",
  "mealTime": "12:00:00",
  "mealType": "LUNCH",
  "foodItemId": 1,
  "foodItemName": "Cơm trắng",
  "servings": 1.5,
  "totalCalories": 345.0,
  "notes": "Lunch at home"
}
```

---

## 🌐 Integration với Next.js

### Setup

```bash
npm install axios
```

### API Client

```typescript
// lib/api.ts
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const auth = {
  register: (data: any) => api.post('/auth/register', data),
  login: (data: any) => api.post('/auth/login', data),
};

export const healthProfile = {
  create: (accountId: number, data: any) => 
    api.post(`/health-profiles/accounts/${accountId}`, data),
  get: (accountId: number) => 
    api.get(`/health-profiles/accounts/${accountId}`),
};

export const foods = {
  search: (name: string) => 
    api.get(`/foods/search?name=${name}`),
  getAll: () => 
    api.get('/foods'),
};

export const mealLogs = {
  create: (accountId: number, data: any) => 
    api.post(`/meal-logs/accounts/${accountId}`, data),
  getByDate: (accountId: number, date: string) => 
    api.get(`/meal-logs/accounts/${accountId}/date/${date}`),
};

export default api;
```

### Usage Example

```typescript
// app/login/page.tsx
'use client';

import { useState } from 'react';
import { auth } from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const { data } = await auth.login({ email, password });
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('user', JSON.stringify(data));
      router.push('/dashboard');
    } catch (error) {
      alert('Login failed!');
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

## 🔍 Troubleshooting

### Port 8080 đã được sử dụng
```bash
# Stop services
stop.bat

# Change port trong docker-compose.yml
ports:
  - "8081:8080"  # Changed from 8080:8080
```

### MySQL connection error
```bash
# Reset everything
reset.bat
```

### Swagger UI không load
- Đợi 2-3 phút sau khi start
- Check logs: `logs.bat`
- Check health: http://localhost:8080/actuator/health

### JWT Token expired
- Token hết hạn sau 24 giờ
- Login lại để lấy token mới

---

## 📚 Documentation

- **Full API Docs**: [API-DOCUMENTATION.md](API-DOCUMENTATION.md)
- **Implementation Details**: [IMPLEMENTATION-SUMMARY.md](IMPLEMENTATION-SUMMARY.md)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 🎯 Next Steps

1. ✅ Test tất cả endpoints qua Swagger UI
2. ✅ Integrate với Next.js frontend
3. ✅ Setup Google OAuth credentials
4. ✅ Deploy to production

---

## 🆘 Need Help?

1. Check Swagger UI documentation
2. View logs: `docker-compose logs -f app`
3. Check [API-DOCUMENTATION.md](API-DOCUMENTATION.md)
4. Review [IMPLEMENTATION-SUMMARY.md](IMPLEMENTATION-SUMMARY.md)

---

**Happy Coding! 🚀**
