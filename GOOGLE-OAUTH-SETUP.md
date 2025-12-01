# 🔑 Google OAuth2 Setup Guide

## 📋 Hướng Dẫn Lấy Google OAuth Credentials

### Bước 1: Tạo Google Cloud Project

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Click **"Select a project"** → **"New Project"**
3. Đặt tên: `LanhCare` (hoặc tên bạn muốn)
4. Click **"Create"**

### Bước 2: Enable Google+ API

1. Trong project vừa tạo, vào **"APIs & Services"** → **"Library"**
2. Tìm **"Google+ API"** hoặc **"Google Sign-In"**
3. Click **"Enable"**

### Bước 3: Configure OAuth Consent Screen

1. Vào **"APIs & Services"** → **"OAuth consent screen"**
2. Chọn **"External"** → Click **"Create"**
3. Điền thông tin:
   - **App name**: LanhCare
   - **User support email**: your-email@gmail.com
   - **Developer contact**: your-email@gmail.com
4. Click **"Save and Continue"**
5. **Scopes**: Skip (click "Save and Continue")
6. **Test users**: Thêm email test của bạn
7. Click **"Save and Continue"**

### Bước 4: Tạo OAuth 2.0 Client ID

1. Vào **"APIs & Services"** → **"Credentials"**
2. Click **"Create Credentials"** → **"OAuth client ID"**
3. Chọn **"Web application"**
4. Đặt tên: `LanhCare Web Client`
5. **Authorized JavaScript origins**:
   ```
   http://localhost:3000
   http://localhost:3001
   https://your-production-domain.com
   ```
6. **Authorized redirect URIs**:
   ```
   http://localhost:8080/api/auth/oauth2/callback/google
   http://localhost:3000/auth/callback
   https://your-production-domain.com/auth/callback
   ```
7. Click **"Create"**

### Bước 5: Copy Credentials

Bạn sẽ thấy popup với:
- **Client ID**: `xxxxxxxxxxxx.apps.googleusercontent.com`
- **Client Secret**: `xxxxxxxxxxxxxxxxxxxxxxxx`

**LƯU Ý:** Copy và giữ an toàn!

---

## ⚙️ Cấu Hình Backend

### Cập nhật `application.properties`

Mở file: `src/main/resources/application.properties`

Tìm và thay thế:

```properties
# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=PASTE_YOUR_CLIENT_ID_HERE
spring.security.oauth2.client.registration.google.client-secret=PASTE_YOUR_CLIENT_SECRET_HERE
```

**Ví dụ:**
```properties
spring.security.oauth2.client.registration.google.client-id=123456789-abcdefghijklmnop.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=GOCSPX-AbCdEfGhIjKlMnOpQrStUvWxYz
```

### Restart Application

```bash
stop.bat
start.bat
```

---

## 🌐 Cấu Hình Frontend (Next.js)

### Bước 1: Install Google OAuth Package

```bash
npm install @react-oauth/google
```

### Bước 2: Wrap App với GoogleOAuthProvider

```typescript
// app/layout.tsx hoặc _app.tsx
import { GoogleOAuthProvider } from '@react-oauth/google';

export default function RootLayout({ children }) {
  return (
    <html>
      <body>
        <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
          {children}
        </GoogleOAuthProvider>
      </body>
    </html>
  );
}
```

### Bước 3: Create Login Component

```typescript
// components/GoogleLoginButton.tsx
'use client';

import { GoogleLogin } from '@react-oauth/google';
import { useRouter } from 'next/navigation';

export default function GoogleLoginButton() {
  const router = useRouter();

  const handleSuccess = async (credentialResponse: any) => {
    try {
      // Send ID token to backend
      const response = await fetch('http://localhost:8080/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          idToken: credentialResponse.credential 
        }),
      });

      const data = await response.json();
      
      // Save JWT token
      localStorage.setItem('token', data.accessToken);
      localStorage.setItem('user', JSON.stringify(data));
      
      // Redirect to dashboard
      router.push('/dashboard');
    } catch (error) {
      console.error('Google login failed:', error);
      alert('Login failed!');
    }
  };

  const handleError = () => {
    console.error('Google Login Failed');
  };

  return (
    <GoogleLogin
      onSuccess={handleSuccess}
      onError={handleError}
      useOneTap
    />
  );
}
```

### Bước 4: Use in Login Page

```typescript
// app/login/page.tsx
import GoogleLoginButton from '@/components/GoogleLoginButton';

export default function LoginPage() {
  return (
    <div>
      <h1>Login</h1>
      
      {/* Regular login form */}
      <form>{/* ... */}</form>
      
      {/* Google Login */}
      <div>
        <p>Or login with Google:</p>
        <GoogleLoginButton />
      </div>
    </div>
  );
}
```

---

## 🧪 Testing

### Test từ Backend (cURL)

**Lưu ý:** Bạn cần có Google ID Token thật. Cách dễ nhất là test qua frontend.

### Test từ Frontend

1. Start backend: `start.bat`
2. Start Next.js: `npm run dev`
3. Truy cập: http://localhost:3000/login
4. Click nút "Sign in with Google"
5. Chọn tài khoản Google
6. Xem console/network tab để debug

### Expected Flow

```
User clicks Google Login
    ↓
Google OAuth popup
    ↓
User authorizes
    ↓
Google returns ID Token
    ↓
Frontend sends token to: POST /api/auth/google
    ↓
Backend verifies token with Google
    ↓
Backend creates/finds user account
    ↓
Backend returns JWT token
    ↓
Frontend saves token
    ↓
User logged in! ✅
```

---

## 🔍 Troubleshooting

### Error: "Invalid Google ID token"

**Nguyên nhân:**
- Client ID trong `application.properties` không đúng
- Token đã expired
- Token từ project khác

**Giải pháp:**
- Kiểm tra lại Client ID
- Đảm bảo uses same Google Cloud project
- Get fresh token

### Error: "redirect_uri_mismatch"

**Nguyên nhân:**
- Redirect URI không match với Google Console

**Giải pháp:**
- Vào Google Console → Credentials
- Thêm chính xác URL vào "Authorized redirect URIs"
- Đợi vài phút để update

### Error: "Access blocked: This app's request is invalid"

**Nguyên nhân:**
- Chưa configure OAuth consent screen
- App chưa được verify

**Giải pháp:**
- Complete OAuth consent screen setup
- Add test users
- Hoặc publish app (production)

---

## 📝 Environment Variables (Production)

### Backend (.env hoặc application-prod.properties)

```properties
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

### Frontend (.env.local)

```bash
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your-client-id
NEXT_PUBLIC_API_URL=https://api.your-domain.com
```

---

## 🔒 Security Best Practices

### ✅ DO:
- ✅ Giữ bí mật Client Secret
- ✅ Use environment variables
- ✅ Verify tokens server-side
- ✅ Use HTTPS in production
- ✅ Whitelist only necessary redirect URIs

### ❌ DON'T:
- ❌ Commit credentials to Git
- ❌ Expose Client Secret to frontend
- ❌ Trust tokens without verification
- ❌ Allow wildcard redirect URIs

---

## 📚 Additional Resources

- [Google OAuth Documentation](https://developers.google.com/identity/protocols/oauth2)
- [Google Sign-In for Web](https://developers.google.com/identity/sign-in/web)
- [@react-oauth/google Docs](https://www.npmjs.com/package/@react-oauth/google)

---

## ✅ Checklist

- [ ] Created Google Cloud Project
- [ ] Enabled Google+ API
- [ ] Configured OAuth Consent Screen
- [ ] Created OAuth Client ID
- [ ] Copied Client ID & Secret
- [ ] Updated `application.properties`
- [ ] Restarted backend
- [ ] Installed `@react-oauth/google`
- [ ] Wrapped app with GoogleOAuthProvider
- [ ] Created GoogleLoginButton component
- [ ] Tested login flow
- [ ] ✅ Google Login Working!

---

**Nếu gặp vấn đề, check:**
1. Backend logs: `logs.bat`
2. Browser console (F12)
3. Network tab để xem request/response
4. Google Console để verify credentials

**Good luck! 🚀**
