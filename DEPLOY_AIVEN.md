# 🚀 Hướng Dẫn Deploy LanhCare với Aiven PostgreSQL

## 📋 Tổng Quan

Hướng dẫn này sẽ giúp bạn:
1. Tạo **PostgreSQL Database miễn phí** trên Aiven
2. Kết nối Spring Boot app với Aiven database
3. Deploy app lên **Render** (hoặc platform khác)

**Aiven Free Tier:**
- ✅ PostgreSQL miễn phí
- ✅ 5GB storage
- ✅ Không bị xóa sau 90 ngày (như Render)
- ✅ SSL được cấu hình sẵn

---

## 🔧 BƯỚC 1: Tạo Tài Khoản Aiven

1. Truy cập: **https://aiven.io**
2. Click **"Start Free"** hoặc **"Get Started"**
3. Đăng ký bằng GitHub, Google, hoặc Email
4. Xác nhận email nếu cần

---

## 📦 BƯỚC 2: Tạo PostgreSQL Service

### 2.1. Vào Console
Sau khi đăng nhập, bạn sẽ thấy Aiven Console.

### 2.2. Tạo Service mới
1. Click **"Create service"** hoặc **"+ Create a new service"**
2. Chọn **"PostgreSQL"**

### 2.3. Cấu hình Service

| Setting | Giá trị |
|---------|---------|
| **Service name** | `lanhcare-db` |
| **Cloud provider** | Chọn bất kỳ (AWS, Google Cloud, Azure) |
| **Region** | Chọn gần Việt Nam nhất (Singapore, Tokyo, Hong Kong) |
| **Plan** | **Free** (Hobbyist - Free trial hoặc Free forever) |

### 2.4. Click "Create service"

⏳ Đợi khoảng 2-3 phút để service được tạo.

---

## 🔑 BƯỚC 3: Lấy Connection Information

### 3.1. Vào Service vừa tạo
Click vào service `lanhcare-db` để xem chi tiết.

### 3.2. Tìm Connection Information
Trong tab **"Overview"**, bạn sẽ thấy:

```
Host: pg-xxxxxxxx-xxxxxxxx.aiven.io
Port: 12345
Database: defaultdb
User: avnadmin
Password: xxxxxxxxxxxxxxxxxx
```

### 3.3. Tạo JDBC URL
Format JDBC URL cho Spring Boot:

```
jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require
```

**Ví dụ:**
```
jdbc:postgresql://pg-abc123-def456.aiven.io:12345/defaultdb?sslmode=require
```

⚠️ **Quan trọng:** Nhớ thêm `?sslmode=require` vào cuối URL!

---

## 🖥️ BƯỚC 4: Deploy Spring Boot lên Render

### 4.1. Tạo Web Service trên Render
1. Vào [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"Web Service"**
3. Kết nối với GitHub repo `lanhcare`

### 4.2. Cấu hình

| Setting | Value |
|---------|-------|
| **Name** | `lanhcare-api` |
| **Region** | Oregon hoặc Singapore |
| **Runtime** | `Docker` |
| **Dockerfile Path** | `./Dockerfile` |
| **Plan** | Free |

### 4.3. Thêm Environment Variables

Click **"Advanced"** → **"Add Environment Variable"**:

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://HOST:PORT/defaultdb?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | `avnadmin` |
| `SPRING_DATASOURCE_PASSWORD` | `your-aiven-password` |
| `APP_JWT_SECRET` | Chuỗi ngẫu nhiên 64 ký tự |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `SPRING_SQL_INIT_MODE` | `never` |

### 4.4. Click "Create Web Service"

---

## ✅ BƯỚC 5: Kiểm Tra

### 5.1. Theo dõi build logs
- Build sẽ mất khoảng 5-10 phút lần đầu
- Tìm dòng: `Started LanhCareApplication in X seconds`

### 5.2. Test endpoints

| Endpoint | URL |
|----------|-----|
| **Health** | `https://lanhcare-api.onrender.com/actuator/health` |
| **Swagger** | `https://lanhcare-api.onrender.com/swagger-ui.html` |

---

## 🔧 Cấu Hình Bổ Sung (Tùy chọn)

### Giới hạn Connection Pool
Aiven Free tier có giới hạn connections. Thêm biến môi trường:

| Key | Value |
|-----|-------|
| `DB_POOL_SIZE` | `3` |

### Xem Database Logs
Trong Aiven Console → Service → Tab **"Logs"**

### Kết nối từ Local
Dùng DBeaver, pgAdmin, hoặc command line:

```bash
psql "postgres://avnadmin:PASSWORD@HOST:PORT/defaultdb?sslmode=require"
```

---

## 🚨 Troubleshooting

### Connection Refused
```
✗ Kiểm tra IP whitelist trong Aiven
✓ Aiven cho phép tất cả IPs mặc định, nhưng kiểm tra lại trong:
  Service → Overview → "Allowed IP addresses"
```

### SSL Handshake Error
```
✗ Thiếu sslmode=require trong URL
✓ Đảm bảo URL có: ?sslmode=require
```

### Too Many Connections
```
✗ Aiven Free tier giới hạn connections
✓ Giảm DB_POOL_SIZE xuống 3 hoặc 2
```

### Schema Not Found
```
✗ App chưa tạo tables
✓ Đặt SPRING_JPA_HIBERNATE_DDL_AUTO=update để tự động tạo
```

---

## 📊 So Sánh: Aiven vs Render PostgreSQL

| Feature | Aiven Free | Render Free |
|---------|------------|-------------|
| **Storage** | 5 GB | 256 MB |
| **Expiry** | Không hết hạn | Xóa sau 90 ngày |
| **SSL** | Bắt buộc ✅ | Optional |
| **Backups** | Có | Không |
| **Regions** | Nhiều lựa chọn | Giới hạn |

---

## 🔗 Quick Reference

### Aiven JDBC URL Format
```
jdbc:postgresql://<HOST>:<PORT>/<DATABASE>?sslmode=require

# Ví dụ:
jdbc:postgresql://pg-abc123.aiven.io:12345/defaultdb?sslmode=require
```

### Environment Variables cho Render
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://pg-xxx.aiven.io:12345/defaultdb?sslmode=require
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=your-password
APP_JWT_SECRET=your-64-char-secret
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_SQL_INIT_MODE=never
DB_POOL_SIZE=3
```

---

## 📞 Hỗ Trợ

- **Aiven Docs:** https://docs.aiven.io
- **Aiven Console:** https://console.aiven.io
- **Render Docs:** https://render.com/docs

---

**Chúc bạn deploy thành công! 🎉**
