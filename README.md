# 🏥 LanhCare - Health Tracking System

Hệ thống quản lý sức khỏe và theo dõi wellness cho người dùng.

> 🎉 **[✨ START HERE - Bắt Đầu Ngay!](START-HERE.md)** - All you need to get started!
>
> 📖 **[🇻🇳 HƯỚNG DẪN TIẾNG VIỆT - Click vào đây!](HUONG-DAN-CHAY-DOCKER.md)** - Hướng dẫn chi tiết từ A-Z bằng tiếng Việt
>
> 📖 **[🇬🇧 English Docker Guide](README-DOCKER.md)** - Detailed Docker setup guide
>
> 🚀 **[⚡ Quick Start Guide](QUICK-START.md)** - Test API trong 5 phút
>
> 📚 **[📋 API Documentation](API-DOCUMENTATION.md)** - Complete API reference

## 🚀 Quick Start với Docker (Khuyến khích)

### Yêu cầu
- Docker Desktop đã cài đặt và đang chạy
- RAM: tối thiểu 4GB
- Dung lượng: ~2GB

### Khởi động trong 3 bước

1. **Mở Docker Desktop** và đảm bảo nó đang chạy

2. **Double-click file `start.bat`** hoặc chạy lệnh:
   ```bash
   docker-compose up -d --build
   ```

3. **Đợi 2-3 phút** và truy cập:
   - **API:** http://localhost:8080
   - **Health Check:** http://localhost:8080/actuator/health

### Kết nối MySQL Workbench

```
Host:     localhost
Port:     3306
Username: root
Password: rootpassword
Database: health_app_db
```

Sau khi kết nối, bạn sẽ thấy **16 tables** với **data mẫu đầy đủ**! ✅

---

## 📚 Documentation

- 📖 [API Documentation (Full Guide)](API-DOCUMENTATION.md) - **NEW!** 🔥
- 📖 [Hướng dẫn Docker chi tiết](README-DOCKER.md)
- 📋 **Swagger UI**: http://localhost:8080/swagger-ui.html (when running)
- 📋 **OpenAPI Spec**: http://localhost:8080/v3/api-docs

---

## 🎯 Tính Năng Chính

### ✅ Đã Hoàn Thành

#### Infrastructure & Database
- [x] JPA Entities (16 entities)
- [x] Docker Setup
- [x] MySQL Database
- [x] Sample Data
- [x] Health Check Endpoints

#### Phase 1: Foundation ✅
- [x] **Repository Layer** - JPA Repositories cho tất cả entities
- [x] **DTO Classes** - Request/Response DTOs với validation
- [x] **Exception Handling** - Global exception handler

#### Phase 2: Core Features ✅
- [x] **Service Layer** - Business logic & transaction management
- [x] **REST API Controllers**:
  - Account Management (CRUD)
  - Health Profile (với BMI calculation tự động)
  - Food & Meal Logging (với calorie tracking)

#### Phase 3: Security & Documentation ✅
- [x] **JWT Authentication** - Stateless token-based auth
- [x] **Login với Email/Password** - BCrypt password hashing
- [x] **Login với Google OAuth2** - Social login integration
- [x] **Role-based Authorization** - USER, ADMIN, DOCTOR, NUTRITIONIST
- [x] **Swagger/OpenAPI Documentation** - Interactive API docs
- [x] **CORS Configuration** - Ready for Next.js frontend

### 🎉 API Endpoints Available

| Category | Endpoints | Status |
|----------|-----------|--------|
| 🔐 Authentication | Login, Register, Google OAuth | ✅ LIVE |
| 👤 Account Management | CRUD operations | ✅ LIVE |
| 🏥 Health Profiles | BMI tracking | ✅ LIVE |
| 🍽️ Food Database | Search, filter | ✅ LIVE |
| 📊 Meal Logging | Calorie tracking | ✅ LIVE |

### 🚀 Next Steps (Optional)
- [ ] Email verification
- [ ] Password reset
- [ ] Profile pictures upload
- [ ] Meal recommendations
- [ ] Nutrition analytics dashboard

---

## 🗂️ Cấu Trúc Database

### Core Tables
- **Account** - Tài khoản người dùng (5 samples)
- **UserHealthProfile** - Hồ sơ sức khỏe (2 samples)
- **ServicePlan** - Gói dịch vụ (4 plans)
- **Transaction** - Giao dịch (2 samples)

### Food & Nutrition
- **FoodType** - Loại thực phẩm (8 types)
- **FoodItem** - Món ăn (15 items)
- **Nutrient** - Chất dinh dưỡng (12 nutrients)
- **FoodNutrient** - Chi tiết dinh dưỡng
- **MealLog** - Nhật ký bữa ăn (6 logs)

### Medical
- **ICD11Chapter** - Chương bệnh (3 chapters)
- **ICD11Code** - Mã bệnh (3 codes)
- **ICD11Translation** - Bản dịch tiếng Việt
- **Hospital** - Bệnh viện (4 hospitals in HCM)
- **MedicalSpecialty** - Chuyên khoa (5 specialties)
- **DietaryRestriction** - Hạn chế ăn uống (2 restrictions)

### Other
- **FCMToken** - Push notification tokens

---

## 🛠️ Tech Stack

- **Framework:** Spring Boot 4.0.0
- **Java:** 21
- **Database:** MySQL 8.0
- **ORM:** JPA/Hibernate
- **Security:** Spring Security
- **Validation:** Spring Validation
- **DevTools:** Lombok, Spring DevTools
- **Containerization:** Docker & Docker Compose

---

## 🎮 Test Accounts

Tất cả accounts có password: `password123`

| Email | Role | Mô tả |
|-------|------|-------|
| admin@lanhcare.com | ADMIN | Administrator |
| user1@lanhcare.com | USER | Nguyễn Văn A |
| user2@lanhcare.com | USER | Trần Thị B |
| doctor@lanhcare.com | DOCTOR | BS. Lê Văn C |
| nutritionist@lanhcare.com | NUTRITIONIST | Chuyên viên D |

---

## 📦 Scripts Tiện Ích

| Script | Mô tả |
|--------|-------|
| `start.bat` | Khởi động tất cả services |
| `stop.bat` | Dừng tất cả services |
| `reset.bat` | Reset database và rebuild |

---

## 🔧 Development

### Xem logs
```bash
# All services
docker-compose logs -f

# App only
docker-compose logs -f app

# MySQL only
docker-compose logs -f mysql
```

### Rebuild app (giữ nguyên database)
```bash
docker-compose up -d --build app
```

### Access MySQL trong container
```bash
docker exec -it lanhcare-mysql mysql -uroot -prootpassword health_app_db
```

---

## 📝 Ghi Chú

- Database data sẽ được **persist** ngay cả khi stop containers
- Chỉ mất data khi chạy `reset.bat` hoặc `docker-compose down -v`
- Hibernate sẽ tự động tạo/update tables từ entities (ddl-auto=update)
- Init data script chạy một lần khi database được tạo lần đầu

---

## 📞 Support

Nếu gặp vấn đề, xem [Troubleshooting Guide](README-DOCKER.md#-troubleshooting) trong README-DOCKER.md

---

## 📄 License

LanhCare Health Tracking System © 2024
