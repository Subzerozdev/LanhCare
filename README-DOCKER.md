# LanhCare - Docker Setup Guide

## 📋 Yêu Cầu

- Docker Desktop phải được cài đặt và đang chạy
- Ram tối thiểu: 4GB
- Dung lượng ổ đĩa: ~2GB

## 🚀 Cách Sử Dụng

### Khởi động lần đầu

1. **Đảm bảo Docker Desktop đang chạy**

2. **Chạy script start.bat:**
   ```bash
   start.bat
   ```
   Hoặc sử dụng lệnh:
   ```bash
   docker-compose up -d --build
   ```

3. **Đợi khoảng 2-3 phút** để:
   - MySQL khởi động và tạo database
   - Spring Boot build và start
   - Data mẫu được insert

4. **Kiểm tra logs:**
   ```bash
   docker-compose logs -f app
   ```

### Kết nối MySQL Workbench

1. Mở MySQL Workbench
2. Tạo connection mới với thông tin:
   - **Connection Name:** LanhCare Docker
   - **Hostname:** localhost
   - **Port:** 3306
   - **Username:** root
   - **Password:** rootpassword
   - **Default Schema:** health_app_db

3. Test Connection và Connect

## 🎯 Endpoints và Thông Tin

### Spring Boot Application
- URL: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health (nếu có Spring Actuator)

### MySQL Database
- Host: localhost
- Port: 3306
- Database: health_app_db
- Username: root
- Password: rootpassword

### Default Test Accounts
Tất cả accounts đều có password: `password123`

| Email | Role | Fullname |
|-------|------|----------|
| admin@lanhcare.com | ADMIN | Administrator |
| user1@lanhcare.com | USER | Nguyễn Văn A |
| user2@lanhcare.com | USER | Trần Thị B |
| doctor@lanhcare.com | DOCTOR | BS. Lê Văn C |
| nutritionist@lanhcare.com | NUTRITIONIST | Chuyên viên dinh dưỡng D |

## 📊 Sample Data

Database đã được populate với data mẫu cho:
- ✅ 5 Accounts
- ✅ 4 Service Plans
- ✅ 8 Food Types
- ✅ 15 Food Items với nutrient information
- ✅ 12 Nutrients
- ✅ 2 User Health Profiles
- ✅ 6 Meal Logs
- ✅ 4 Hospitals với địa chỉ TP.HCM
- ✅ 5 Medical Specialties
- ✅ 3 ICD11 Chapters
- ✅ 3 ICD11 Codes với Vietnamese translations
- ✅ 2 Dietary Restrictions

## 🛠️ Các Lệnh Hữu Dụng

### Xem logs
```bash
# Tất cả services
docker-compose logs -f

# Chỉ xem logs của app
docker-compose logs -f app

# Chỉ xem logs của MySQL
docker-compose logs -f mysql
```

### Stop services
```bash
stop.bat
# hoặc
docker-compose down
```

### Restart services
```bash
docker-compose restart
```

### Reset database (XÓA TẤT CẢ DATA)
```bash
reset.bat
# hoặc
docker-compose down -v
docker-compose up -d --build
```

### Rebuild app (không xóa database)
```bash
docker-compose up -d --build app
```

## 🐛 Troubleshooting

### Port đã được sử dụng
Nếu port 3306 hoặc 8080 đã được sử dụng, chỉnh sửa `docker-compose.yml`:
```yaml
ports:
  - "3307:3306"  # Thay 3306 thành 3307
  # hoặc
  - "8081:8080"  # Thay 8080 thành 8081
```

### App không kết nối được database
1. Kiểm tra MySQL đã chạy chưa:
   ```bash
   docker-compose ps
   ```
2. Xem logs MySQL:
   ```bash
   docker-compose logs mysql
   ```
3. Restart services:
   ```bash
   docker-compose restart
   ```

### Tables chưa có data
1. Kiểm tra file `init-data.sql` đã được mount:
   ```bash
   docker exec -it lanhcare-mysql ls /docker-entrypoint-initdb.d/
   ```
2. Reset database:
   ```bash
   reset.bat
   ```

### Build failed
1. Xóa target folder:
   ```bash
   rmdir /s /q target
   ```
2. Rebuild:
   ```bash
   docker-compose up -d --build
   ```

## 📁 Cấu Trúc Files

```
lanhcare/
├── docker-compose.yml          # Orchestration config
├── Dockerfile                  # App build config
├── .dockerignore              # Exclude files from build
├── start.bat                  # Start script
├── stop.bat                   # Stop script
├── reset.bat                  # Reset script
└── src/main/resources/
    ├── application.properties  # App configuration
    └── init-data.sql          # Sample data
```

## 💡 Tips

- Nên dùng `start.bat` và `stop.bat` cho tiện
- Kiểm tra logs thường xuyên khi develop
- Chỉ dùng `reset.bat` khi cần reset database hoàn toàn
- Data sẽ được persist ngay cả khi stop containers (trừ khi dùng reset)
