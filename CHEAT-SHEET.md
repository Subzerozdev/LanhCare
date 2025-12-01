# ⚡ LANHCARE - CHEAT SHEET

## 🚀 CÁC LỆNH NHANH

### Khởi động
```bash
docker-compose up -d --build
```
Hoặc double-click: **`start.bat`**

### Dừng lại
```bash
docker-compose down
```
Hoặc double-click: **`stop.bat`**

### Reset (Xóa hết data)
```bash
docker-compose down -v
docker-compose up -d --build
```
Hoặc double-click: **`reset.bat`**

---

## 📊 KIỂM TRA STATUS

### Xem containers đang chạy
```bash
docker ps
docker-compose ps
```

### Xem logs
```bash
# Tất cả
docker-compose logs -f

# Chỉ app
docker-compose logs -f app

# Chỉ MySQL
docker-compose logs -f mysql
```
Hoặc double-click: **`logs.bat`**

### Kiểm tra health
```bash
curl http://localhost:8080/actuator/health
```
Hoặc mở trình duyệt: http://localhost:8080/actuator/health

---

## 🗄️ THAO TÁC DATABASE

### Kết nối MySQL Workbench
```
Host:     127.0.0.1
Port:     3306
Username: lanhcare
Password: lanhcare123
Database: health_app_db
```

### Xem data từ command line
```bash
# Đếm records
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db \
  -e "SELECT COUNT(*) FROM account;"

# Xem accounts
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db \
  -e "SELECT email, role FROM account;"

# Xem tables
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db \
  -e "SHOW TABLES;"
```

### Import data
```bash
.\import-data.bat
```

### Check data
```bash
.\check-data.bat
```

### Fix MySQL access
```bash
.\fix-mysql-access.bat
```

---

## 🚀 TEST API

### Quick Test
```bash
.\test-api.bat
```

### Swagger UI
Mở browser: http://localhost:8080/swagger-ui.html

### Test Authentication

#### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"fullname\":\"Test User\",\"password\":\"password123\"}"
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

#### Test with Token
```bash
# Save your token first
set TOKEN=your-token-here

# Get Foods
curl -H "Authorization: Bearer %TOKEN%" http://localhost:8080/api/foods

# Get Current Account
curl -H "Authorization: Bearer %TOKEN%" http://localhost:8080/api/accounts/me
```

### API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| /api/auth/register | POST | Register user |
| /api/auth/login | POST | Login |
| /api/auth/google | POST | Google OAuth |
| /api/accounts/me | GET | Current user |
| /api/health-profiles/accounts/:id | POST/GET/PUT | Health profile |
| /api/foods | GET | List foods |
| /api/foods/search?name=... | GET | Search foods |
| /api/meal-logs/accounts/:id | POST/GET | Meal logs |

---

## 🔧 TROUBLESHOOTING

### Port bị chiếm
Sửa trong `docker-compose.yml`:
```yaml
# Đổi MySQL port
ports:
  - "3307:3306"

# Đổi App port  
ports:
  - "8081:8080"
```

### Rebuild app (giữ database)
```bash
docker-compose up -d --build app
```

### Restart chỉ app
```bash
docker-compose restart app
```

### Xóa cache và rebuild
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Vào MySQL shell
```bash
docker exec -it lanhcare-mysql mysql -uroot -prootpassword health_app_db
```

### Backup database
```bash
docker exec lanhcare-mysql mysqldump -uroot -prootpassword health_app_db > backup.sql
```

### Restore database
```bash
docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db < backup.sql
```

---

## 📍 CÁC URL QUAN TRỌNG

| URL | Mô tả |
|-----|-------|
| http://localhost:8080 | Trang chủ application |
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/actuator/info | Thông tin app |
| 127.0.0.1:3306 | MySQL Server |

---

## 👥 TEST ACCOUNTS

| Email | Password | Role |
|-------|----------|------|
| admin@lanhcare.com | password123 | ADMIN |
| user1@lanhcare.com | password123 | USER |
| user2@lanhcare.com | password123 | USER |
| doctor@lanhcare.com | password123 | DOCTOR |
| nutritionist@lanhcare.com | password123 | NUTRITIONIST |

---

## 📦 DOCKER SCRIPTS

| File | Mô tả |
|------|-------|
| `start.bat` | Khởi động tất cả services |
| `stop.bat` | Dừng tất cả services |
| `reset.bat` | Reset và rebuild từ đầu |
| `logs.bat` | Xem logs |
| `import-data.bat` | Import sample data |
| `check-data.bat` | Kiểm tra data trong DB |
| `fix-mysql-access.bat` | Fix lỗi MySQL access |

---

## 📚 TÀI LIỆU

| File | Nội dung |
|------|----------|
| `HUONG-DAN-CHAY-DOCKER.md` | 🇻🇳 Hướng dẫn tiếng Việt đầy đủ |
| `README-DOCKER.md` | 🇬🇧 English Docker guide |
| `SETUP-SUMMARY.md` | Tổng kết setup |
| `ARCHITECTURE.md` | Kiến trúc hệ thống |

---

**💡 TIP:** Lưu file này lại để tra cứu nhanh khi cần!
