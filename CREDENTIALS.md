# 🔐 LANHCARE - THÔNG TIN ĐĂNG NHẬP

**⚠️ Lưu ý:** File này chứa thông tin đăng nhập. KHÔNG commit lên Git nếu đây là production!

---

## 🗄️ MYSQL DATABASE

### **Kết nối từ MySQL Workbench**

#### Option 1: User `lanhcare` (Khuyến nghị)
```
Host:     127.0.0.1
Port:     3306
Username: lanhcare
Password: lanhcare123
Database: health_app_db
```

#### Option 2: User `root`
```
Host:     127.0.0.1  
Port:     3306
Username: root
Password: rootpassword
Database: health_app_db
```

### **Connection String cho Application**
```
jdbc:mysql://mysql:3306/health_app_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
```

---

## 👥 TEST ACCOUNTS

### Tất cả accounts đều có password: `password123`

| Email | Role | Fullname | Notes |
|-------|------|----------|-------|
| admin@lanhcare.com | ADMIN | Administrator | Full access |
| user1@lanhcare.com | USER | Nguyễn Văn A | Regular user |
| user2@lanhcare.com | USER | Trần Thị B | Regular user |
| doctor@lanhcare.com | DOCTOR | BS. Lê Văn C | Doctor role |
| nutritionist@lanhcare.com | NUTRITIONIST | Chuyên viên D | Nutritionist |

**🔐 Password Hash (BCrypt):**
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

---

## 🔧 THAY ĐỔI CREDENTIALS

### **Đổi MySQL Password**

Cần sửa ở 2 files:

**1. File `docker-compose.yml`:**
```yaml
# Line 9
MYSQL_ROOT_PASSWORD: your_new_password

# Line 22  
"-prootyour_new_password"

# Line 39
SPRING_DATASOURCE_PASSWORD: your_new_password
```

**2. File `src/main/resources/application.properties`:**
```properties
# Line 10
spring.datasource.password=your_new_password
```

Sau đó reset:
```bash
docker-compose down -v
docker-compose up -d --build
```

### **Đổi Test Account Passwords**

Cần update trong database hoặc tạo service để đổi password với BCrypt encoding.

---

## 🌐 APPLICATION URLs

| URL | Mô tả | Auth Required |
|-----|-------|---------------|
| http://localhost:8080 | Home | Yes (Spring Security default) |
| http://localhost:8080/actuator/health | Health Check | No |
| http://localhost:8080/actuator/info | App Info | No |

**⚠️ Spring Security Default User (nếu chưa config):**
- Username: `user`
- Password: Check logs để lấy generated password

---

## 📝 NOTES

- Đây là credentials cho môi trường **DEVELOPMENT**
- **KHÔNG** sử dụng credentials này cho **PRODUCTION**
- Đổi tất cả passwords trước khi deploy
- Sử dụng environment variables cho production
- Consider using Docker Secrets hoặc HashiCorp Vault

---

## 🔒 SECURITY CHECKLIST

Trước khi deploy production:

- [ ] Đổi tất cả MySQL passwords
- [ ] Đổi tất cả test account passwords
- [ ] Sử dụng environment variables thay vì hardcode
- [ ] Enable SSL/TLS cho MySQL connection
- [ ] Setup proper Spring Security configuration
- [ ] Implement JWT authentication
- [ ] Add rate limiting
- [ ] Setup firewall rules
- [ ] Regular security audits

---

*Last updated: 29/11/2024*
