# 🚀 Hướng Dẫn Deploy LanhCare Lên Render

## 📋 Tổng Quan

Hướng dẫn này sẽ giúp bạn deploy **LanhCare API** (Spring Boot) và **PostgreSQL Database** lên Render.

**Thời gian ước tính:** 15-20 phút

---

## 🔧 Chuẩn Bị

### 1. Tài khoản cần thiết
- [ ] **GitHub Account** - để lưu trữ source code
- [ ] **Render Account** - [Đăng ký tại đây](https://render.com)

### 2. Push code lên GitHub
```bash
# Khởi tạo git (nếu chưa có)
git init

# Thêm file .gitignore
echo "target/" >> .gitignore
echo "*.class" >> .gitignore
echo ".idea/" >> .gitignore
echo "*.iml" >> .gitignore

# Add và commit
git add .
git commit -m "Initial commit - LanhCare API"

# Thêm remote và push
git remote add origin https://github.com/YOUR_USERNAME/lanhcare.git
git branch -M main
git push -u origin main
```

---

## 📦 BƯỚC 1: Tạo PostgreSQL Database Trên Render

### 1.1. Vào Render Dashboard
1. Đăng nhập vào [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"PostgreSQL"**

### 1.2. Cấu hình Database
| Field | Value |
|-------|-------|
| **Name** | `lanhcare-db` |
| **Database** | `lanhcare_db` |
| **User** | `lanhcare_user` |
| **Region** | `Singapore (Southeast Asia)` ← Gần Việt Nam nhất |
| **Plan** | `Free` (hoặc `Starter` cho production) |

### 1.3. Click "Create Database"

### 1.4. Lưu lại thông tin kết nối
Sau khi tạo xong, vào tab **"Info"** và lưu lại:
- **Internal Database URL** (dùng cho Render services)
- **External Database URL** (dùng để connect từ local)
- **PSQL Command** (dùng để test connection)

⚠️ **Lưu ý:** Database Free tier sẽ bị xóa sau 90 ngày không hoạt động.

---

## 🖥️ BƯỚC 2: Tạo Web Service Trên Render

### 2.1. Tạo Web Service mới
1. Click **"New +"** → **"Web Service"**
2. Chọn **"Build and deploy from a Git repository"**
3. Kết nối với GitHub và chọn repository `lanhcare`

### 2.2. Cấu hình Web Service

| Field | Value |
|-------|-------|
| **Name** | `lanhcare-api` |
| **Region** | `Singapore` |
| **Branch** | `main` |
| **Runtime** | `Docker` |
| **Dockerfile Path** | `./Dockerfile` |
| **Plan** | `Free` (hoặc `Starter` cho production) |

### 2.3. Cấu hình Environment Variables

Click **"Advanced"** → **"Add Environment Variable"** và thêm:

#### Database Connection
| Key | Value |
|-----|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://HOST:PORT/DATABASE` ← Lấy từ DB Info |
| `SPRING_DATASOURCE_USERNAME` | `lanhcare_user` |
| `SPRING_DATASOURCE_PASSWORD` | `YOUR_DB_PASSWORD` ← Lấy từ DB Info |

⚠️ **Quan trọng:** Chuyển đổi URL từ format `postgres://` sang `jdbc:postgresql://`

Ví dụ:
- **Render cung cấp:** `postgres://user:pass@host:5432/db`
- **Bạn cần nhập:** `jdbc:postgresql://host:5432/db`

#### Application Settings
| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `APP_JWT_SECRET` | Nhập một chuỗi ngẫu nhiên 64 ký tự |
| `APP_JWT_EXPIRATION_MS` | `86400000` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `SPRING_SQL_INIT_MODE` | `never` |

#### API Keys (Tùy chọn)
| Key | Value |
|-----|-------|
| `ICD_CLIENT_ID` | `fa000cb5-fc34...` |
| `ICD_CLIENT_SECRET` | `KovmlRFwaFB0...` |
| `TRANSLATE_API_KEY` | `ta_19e60b...` |

#### Google OAuth2 (Tùy chọn)
| Key | Value |
|-----|-------|
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` | `your-client-id` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | `your-secret` |

### 2.4. Click "Create Web Service"

---

## 🔄 BƯỚC 3: Deploy và Kiểm Tra

### 3.1. Theo dõi quá trình build
- Render sẽ tự động build Docker image và deploy
- Quá trình này mất khoảng **5-10 phút** lần đầu

### 3.2. Kiểm tra logs
- Vào tab **"Logs"** để xem trạng thái
- Tìm dòng: `Started LanhCareApplication in X seconds`

### 3.3. Test API
Sau khi deploy thành công, bạn sẽ có URL như:
```
https://lanhcare-api.onrender.com
```

Test các endpoint:
- **Health Check:** `https://lanhcare-api.onrender.com/actuator/health`
- **Swagger UI:** `https://lanhcare-api.onrender.com/swagger-ui.html`
- **API Docs:** `https://lanhcare-api.onrender.com/v3/api-docs`

---

## 🎯 CÁCH NHANH: Sử dụng render.yaml (Blueprint)

Nếu bạn muốn tự động hóa hoàn toàn:

### 1. Đảm bảo file `render.yaml` có trong repository

### 2. Truy cập Render Dashboard
1. Click **"New +"** → **"Blueprint"**
2. Chọn repository chứa `render.yaml`
3. Render sẽ tự động tạo cả Database và Web Service

### 3. Cấu hình các secrets thủ công
Sau khi Blueprint tạo xong, vào từng service và update các Environment Variables có `sync: false`

---

## 📊 Sau Khi Deploy

### Import Sample Data (Tùy chọn)
Nếu muốn import data mẫu:

1. **Lấy connection string** từ Database Info (External URL)

2. **Sử dụng psql:**
```bash
psql "postgresql://USER:PASS@HOST:PORT/DATABASE?sslmode=require"
```

3. **Import từ file:**
```bash
psql "YOUR_CONNECTION_STRING" -f data.sql
```

### Monitor Application
- **Logs:** Tab "Logs" trong Web Service
- **Metrics:** Tab "Metrics" (nếu dùng paid plan)
- **Health:** `/actuator/health` endpoint

---

## ⚠️ Lưu Ý Quan Trọng

### Free Tier Limitations
| Resource | Limitation |
|----------|------------|
| **Web Service** | Sleep sau 15 phút không hoạt động, cold start ~30s |
| **Database** | 256MB storage, xóa sau 90 ngày inactive |
| **Bandwidth** | 100GB/month |

### Production Recommendations
1. Sử dụng **Starter plan** ($7/month) cho Web Service
2. Sử dụng **Starter plan** ($7/month) cho Database
3. Thêm **Custom Domain** cho branding
4. Enable **Auto-Deploy** từ GitHub

### Security Checklist
- [ ] Đổi `APP_JWT_SECRET` thành chuỗi ngẫu nhiên mạnh
- [ ] Không commit secrets vào Git
- [ ] Sử dụng HTTPS (Render cung cấp miễn phí)
- [ ] Disable Swagger UI trong production nếu cần

---

## 🔧 Troubleshooting

### Build Failed
```
Check Dockerfile syntax và dependencies trong pom.xml
```

### Database Connection Failed
```
1. Kiểm tra SPRING_DATASOURCE_URL đúng format jdbc:postgresql://
2. Kiểm tra username/password
3. Đảm bảo sử dụng Internal URL nếu kết nối trong Render
```

### Application Crashed
```
1. Xem logs để tìm stack trace
2. Kiểm tra memory usage (Free tier = 512MB)
3. Kiểm tra các environment variables
```

### Cold Start Slow
```
Free tier sẽ sleep sau 15 phút không hoạt động.
Request đầu tiên sau sleep sẽ mất ~30 giây.
Upgrade lên Starter plan để tránh vấn đề này.
```

---

## 📞 Hỗ Trợ

- **Render Docs:** https://render.com/docs
- **Render Community:** https://community.render.com
- **Render Status:** https://status.render.com

---

**Chúc bạn deploy thành công! 🎉**
