# 🏥 HƯỚNG DẪN CHẠY LANHCARE VỚI DOCKER

## 📋 MỤC LỤC
1. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
2. [Bước 1: Clone source code](#bước-1-clone-source-code)
3. [Bước 2: Cài đặt Docker Desktop](#bước-2-cài-đặt-docker-desktop)
4. [Bước 3: Chạy project với Docker](#bước-3-chạy-project-với-docker)
5. [Bước 4: Kết nối MySQL Workbench](#bước-4-kết-nối-mysql-workbench)
6. [Bước 5: Truy cập ứng dụng](#bước-5-truy-cập-ứng-dụng)
7. [Các lệnh thường dùng](#các-lệnh-thường-dùng)
8. [Xử lý lỗi thường gặp](#xử-lý-lỗi-thường-gặp)

---

## 🖥️ YÊU CẦU HỆ THỐNG

Trước khi bắt đầu, đảm bảo máy tính của bạn có:

- ✅ **Hệ điều hành:** Windows 10/11, macOS, hoặc Linux
- ✅ **RAM:** Tối thiểu 4GB (khuyến nghị 8GB)
- ✅ **Dung lượng ổ đĩa:** Còn trống tối thiểu 5GB
- ✅ **Kết nối Internet:** Để tải Docker images

**Phần mềm cần cài đặt:**
- Docker Desktop (hướng dẫn cài ở bước 2)
- Git (để clone source code)
- MySQL Workbench (tùy chọn, để xem database)

---

## 📥 BƯỚC 1: CLONE SOURCE CODE

### **Cách 1: Sử dụng Git Command**

Mở Terminal (hoặc Git Bash trên Windows) và chạy:

```bash
git clone https://github.com/your-username/lanhcare.git
cd lanhcare
```

### **Cách 2: Download ZIP**

1. Vào trang GitHub của project
2. Click nút **Code** → **Download ZIP**
3. Giải nén file ZIP vào thư mục bạn muốn
4. Mở Terminal/Command Prompt tại thư mục đã giải nén

---

## 🐳 BƯỚC 2: CÀI ĐẶT DOCKER DESKTOP

### **Windows:**

1. Truy cập: https://www.docker.com/products/docker-desktop/
2. Download **Docker Desktop for Windows**
3. Chạy file cài đặt
4. Làm theo hướng dẫn cài đặt
5. Khởi động lại máy tính (nếu được yêu cầu)
6. Mở **Docker Desktop** từ Start Menu
7. Đợi cho đến khi thấy "Docker Desktop is running"

### **macOS:**

1. Truy cập: https://www.docker.com/products/docker-desktop/
2. Download **Docker Desktop for Mac**
3. Kéo file Docker vào thư mục Applications
4. Mở Docker từ Applications
5. Chấp nhận quyền truy cập khi được yêu cầu

### **Linux (Ubuntu/Debian):**

```bash
# Cài đặt Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Cài đặt Docker Compose
sudo apt install docker-compose

# Thêm user vào group docker (để không cần sudo)
sudo usermod -aG docker $USER
```

**✅ Kiểm tra cài đặt thành công:**

Mở Terminal và chạy:
```bash
docker --version
docker-compose --version
```

Nếu hiển thị version number → Cài đặt thành công! ✅

---

## 🚀 BƯỚC 3: CHẠY PROJECT VỚI DOCKER

Đây là bước quan trọng nhất! Có 2 cách để chạy:

### **Cách 1: Sử dụng Script (Dễ nhất - Windows)**

1. Mở thư mục project `lanhcare`
2. **Double-click** vào file: **`start.bat`**
3. Một cửa sổ Command Prompt sẽ mở ra
4. Đợi khoảng **3-5 phút** (lần đầu sẽ lâu hơn vì phải tải images)
5. Khi thấy dòng **"Services are starting up!"** → Thành công!

### **Cách 2: Sử dụng Terminal/Command Line**

Mở Terminal tại thư mục project và chạy:

```bash
docker-compose up -d --build
```

**📝 Giải thích lệnh:**
- `docker-compose up`: Khởi động các services
- `-d`: Chạy ở chế độ background (detached)
- `--build`: Build lại app image

### **⏱️ Quá trình khởi động:**

Khi chạy, Docker sẽ thực hiện các bước sau:

1. ⬇️ **Tải MySQL image** (~500MB) - Mất 1-2 phút
2. ⬇️ **Tải Java images** (~400MB) - Mất 1-2 phút
3. 🔨 **Build Spring Boot application** - Mất 2-3 phút
4. 🗄️ **Khởi động MySQL container**
5. 🌱 **Khởi động Spring Boot container**
6. ✅ **Tạo database schema và insert data mẫu**

**Tổng thời gian lần đầu:** ~5-7 phút
**Lần sau:** ~30 giây (không cần tải lại)

### **✅ Kiểm tra đã chạy thành công:**

Chạy lệnh:
```bash
docker-compose ps
```

Bạn sẽ thấy 2 containers đang chạy:
- `lanhcare-mysql` - Status: Up (healthy)
- `lanhcare-app` - Status: Up (healthy)

**Hoặc xem logs:**
```bash
docker-compose logs -f app
```

Khi thấy dòng `Started LanhCareApplication in X seconds` → **Thành công!** ✅

---

## 🗄️ BƯỚC 4: KẾT NỐI MYSQL WORKBENCH

Sau khi Docker containers đã chạy, bạn có thể kết nối vào database để xem dữ liệu.

### **4.1. Cài đặt MySQL Workbench (nếu chưa có)**

Tải tại: https://dev.mysql.com/downloads/workbench/

### **4.2. Tạo Connection trong MySQL Workbench**

**Bước 1:** Mở MySQL Workbench

**Bước 2:** Click vào dấu **➕** bên cạnh "MySQL Connections"

**Bước 3:** Điền thông tin kết nối:

#### **⭐ OPTION 1: User `lanhcare` (Khuyến nghị)**

```
Connection Name:    LanhCare Docker
Hostname:          127.0.0.1
Port:              3306
Username:          lanhcare
Password:          [Click "Store in Vault..." → Nhập: lanhcare123]
Default Schema:    health_app_db
```

#### **OPTION 2: User `root`**

```
Connection Name:    LanhCare Docker (Root)
Hostname:          127.0.0.1
Port:              3306
Username:          root
Password:          [Click "Store in Vault..." → Nhập: rootpassword]
Default Schema:    health_app_db
```

**⚠️ LƯU Ý QUAN TRỌNG:**
- ✅ Hostname phải là `127.0.0.1` (KHÔNG dùng `localhost`)
- ✅ Username/Password KHÔNG có khoảng trắng
- ✅ Nhớ click "Store in Vault..." để lưu password

**Bước 4:** Click **"Test Connection"**

Nếu thành công, sẽ thấy thông báo:
```
Successfully made the MySQL connection
```

**Bước 5:** Click **OK** để lưu connection

**Bước 6:** **Double-click** vào connection vừa tạo để kết nối

### **4.3. Import dữ liệu mẫu**

Sau khi kết nối thành công vào MySQL Workbench:

**Bước 1:** Chạy script import data

Double-click file: **`import-all-data.bat`**

Hoặc từ command line:
```bash
.\import-all-data.bat
```

**Bước 2:** Đợi script hoàn tất (khoảng 10-20 giây)

**Bước 3:** Refresh trong MySQL Workbench và xem dữ liệu

**1. Xem danh sách tables:**
```sql
SHOW TABLES;
```

Bạn sẽ thấy 16 tables:
- account
- service_plan
- transaction
- fcmtoken
- user_health_profile
- food_type
- food_item
- meal_log
- nutrient
- food_nutrient
- icd11_chapter
- icd11_code
- icd11_translation
- dietary_restriction
- hospital
- medical_specialty

**2. Xem dữ liệu mẫu:**

```sql
-- Xem tất cả accounts
SELECT * FROM account;

-- Xem các gói dịch vụ
SELECT * FROM service_plan;

-- Đếm số lượng records trong mỗi table
SELECT 
    'account' as table_name, COUNT(*) as records FROM account
UNION ALL
SELECT 'service_plan', COUNT(*) FROM service_plan
UNION ALL
SELECT 'food_item', COUNT(*) FROM food_item;
```

### **4.4. Nếu gặp lỗi "Access Denied"**

Chạy file: **`fix-mysql-access.bat`** trong thư mục project, sau đó thử lại.

Hoặc chạy lệnh:
```bash
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'rootpassword'; FLUSH PRIVILEGES;"
```

---

## 🌐 BƯỚC 5: TRUY CẬP ỨNG DỤNG

Sau khi containers đã chạy:

### **🔗 Các URL quan trọng:**

- **Trang chủ:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health
- **API Info:** http://localhost:8080/actuator/info

### **📊 Kiểm tra Application đang chạy:**

**Cách 1: Sử dụng trình duyệt**

Mở trình duyệt và truy cập: http://localhost:8080/actuator/health

Nếu thấy `"status":"UP"` → Application đang chạy tốt! ✅

**Cách 2: Sử dụng Command Line**

```bash
curl http://localhost:8080/actuator/health
```

### **👥 Test Accounts có sẵn:**

| Email | Password | Role |
|-------|----------|------|
| admin@lanhcare.com | password123 | ADMIN |
| user1@lanhcare.com | password123 | USER |
| user2@lanhcare.com | password123 | USER |
| doctor@lanhcare.com | password123 | DOCTOR |
| nutritionist@lanhcare.com | password123 | NUTRITIONIST |

**⚠️ Lưu ý:** Password đã được mã hóa BCrypt trong database.

---

## 🛠️ CÁC LỆNH THƯỜNG DÙNG

### **Xem Logs**

```bash
# Xem logs tất cả services
docker-compose logs -f

# Xem logs app only
docker-compose logs -f app

# Xem logs MySQL only
docker-compose logs -f mysql
```

### **Dừng Services**

```bash
# Dừng tất cả containers (giữ data)
docker-compose down

# Dừng và XÓA tất cả data
docker-compose down -v
```

### **Restart Services**

```bash
# Restart tất cả
docker-compose restart

# Restart chỉ app
docker-compose restart app
```

### **Rebuild Application**

```bash
# Rebuild và restart app (giữ database)
docker-compose up -d --build app
```

### **Reset Hoàn Toàn**

**Windows:** Double-click file **`reset.bat`**

**Command Line:**
```bash
docker-compose down -v
docker-compose up -d --build
```

⚠️ **Lưu ý:** Lệnh này sẽ XÓA tất cả dữ liệu!

### **Kiểm tra trạng thái**

```bash
# Xem containers đang chạy
docker ps

# Xem chi tiết containers
docker-compose ps
```

---

## 🐛 XỬ LÝ LỖI THƯỜNG GẶP

### **❌ Lỗi 1: "Port 3306 đã được sử dụng"**

**Nguyên nhân:** Đã có MySQL khác đang chạy trên máy

**Giải pháp 1:** Tắt MySQL đang chạy
```bash
# Windows: Mở Services → Tắt MySQL
# macOS/Linux: 
sudo service mysql stop
```

**Giải pháp 2:** Đổi port trong `docker-compose.yml`
```yaml
ports:
  - "3307:3306"  # Đổi 3306 thành 3307
```

### **❌ Lỗi 2: "Port 8080 đã được sử dụng"**

**Giải pháp:** Đổi port trong `docker-compose.yml`
```yaml
ports:
  - "8081:8080"  # Đổi 8080 thành 8081
```

### **❌ Lỗi 3: Docker không khởi động được**

**Kiểm tra:**
1. Docker Desktop có đang chạy không?
2. Trong Docker Desktop → Settings → Resources → Đảm bảo đủ RAM (tối thiểu 2GB)

### **❌ Lỗi 4: Build bị lỗi "Cannot download dependencies"**

**Nguyên nhân:** Lỗi kết nối mạng hoặc Maven repository

**Giải pháp:**
```bash
# Xóa và rebuild
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### **❌ Lỗi 5: Application không start**

**Kiểm tra logs:**
```bash
docker-compose logs app
```

**Các lỗi thường gặp:**
- Database chưa ready → Đợi thêm 30s
- Out of memory → Tăng memory cho Docker Desktop

### **❌ Lỗi 6: "Access denied for user 'root'"**

**Giải pháp:** Chạy file **`fix-mysql-access.bat`**

Hoặc:
```bash
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'rootpassword'; FLUSH PRIVILEGES;"
```

### **❌ Lỗi 7: Không có dữ liệu trong database**

**Giải pháp:**

**Cách 1:** Reset và chạy lại
```bash
docker-compose down -v
docker-compose up -d --build
```

**Cách 2:** Import data thủ công
```bash
# Windows
.\import-data.bat

# Linux/macOS
docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db < src/main/resources/init-data.sql
```

---

## 📚 TÀI LIỆU THAM KHẢO

- **README.md** - Tổng quan project
- **README-DOCKER.md** - Chi tiết Docker setup
- **SETUP-SUMMARY.md** - Tổng kết các bước setup
- **ARCHITECTURE.md** - Kiến trúc hệ thống

---

## 💡 TIPS HỮU ÍCH

### **1. Kiểm tra nhanh xem đã chạy được chưa:**
```bash
# Check containers
docker ps

# Check health
curl http://localhost:8080/actuator/health
```

### **2. Xem data nhanh từ command line:**
```bash
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT email, role FROM account;"
```

### **3. Backup database:**
```bash
docker exec lanhcare-mysql mysqldump -uroot -prootpassword health_app_db > backup.sql
```

### **4. Restore database:**
```bash
docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db < backup.sql
```

---

## 🎯 CHECKLIST - ĐẢM BẢO MỌI THỨ HOẠT ĐỘNG

Dùng checklist này để đảm bảo setup thành công:

- [ ] Docker Desktop đã được cài đặt và đang chạy
- [ ] Source code đã được clone về
- [ ] Đã chạy `docker-compose up -d --build`
- [ ] 2 containers đang chạy (check với `docker ps`)
- [ ] Health check trả về UP: http://localhost:8080/actuator/health
- [ ] MySQL Workbench kết nối thành công với `127.0.0.1:3306`
- [ ] Có thể xem 16 tables trong database
- [ ] Tables `account` và `service_plan` có dữ liệu

**Nếu tất cả đều ✅ → Chúc mừng! Setup thành công!** 🎉

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề không giải quyết được:

1. Kiểm tra lại từng bước ở trên
2. Xem logs: `docker-compose logs -f`
3. Thử reset: `docker-compose down -v && docker-compose up -d --build`

---

**Chúc bạn code vui vẻ! Happy Coding! 💻🚀**

---

*Tài liệu này được cập nhật lần cuối: 29/11/2024*
