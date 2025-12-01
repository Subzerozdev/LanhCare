# 📚 LANHCARE - TÀI LIỆU HƯỚNG DẪN

## 🎯 BẮT ĐẦU TẠI ĐÂY

Nếu bạn **mới clone project** và chưa biết bắt đầu từ đâu:

### 👉 **[HƯỚNG DẪN TIẾNG VIỆT - BẮT ĐẦU TẠI ĐÂY!](HUONG-DAN-CHAY-DOCKER.md)**

File này hướng dẫn **từng bước chi tiết**:
- Clone source code
- Cài đặt Docker
- Chạy project
- Kết nối MySQL Workbench
- Xử lý lỗi thường gặp

⏱️ **Thời gian đọc:** 15 phút
📖 **Độ khó:** Dễ - Dành cho người mới bắt đầu

---

## 📖 CÁC TÀI LIỆU KHÁC

### **⚡ Quick Start (MỚI!)**

#### 🚀 [QUICK-START.md](QUICK-START.md) - **BẮT ĐẦU TẠI ĐÂY!** 🔥
- Test API trong 5 phút
- Swagger UI guide
- Example requests
- Next.js integration code

⏱️ Thời gian: 5 phút | 📖 Độ khó: Dễ

#### 📚 [API-DOCUMENTATION.md](API-DOCUMENTATION.md) - **API Guide Đầy Đủ**
- Tất cả endpoints
- Authentication guide
- Google OAuth setup
- Next.js integration examples
- cURL examples

⏱️ Thời gian: 20 phút | 📖 Độ khó: Trung bình

#### 🎉 [IMPLEMENTATION-SUMMARY.md](IMPLEMENTATION-SUMMARY.md) - **Chi Tiết Triển Khai**
- Tổng kết 3 phases đã hoàn thành
- Danh sách tất cả files
- Testing instructions
- Statistics

⏱️ Thời gian: 10 phút | 📖 Độ khó: Trung bình

---

### **1. Quick Reference**

#### 🇻🇳 [CHEAT-SHEET.md](CHEAT-SHEET.md)
- Các lệnh thường dùng
- Quick commands
- URLs quan trọng
- Test accounts

⏱️ Thời gian: 2 phút | 📖 Độ khó: Dễ

#### 🔐 [CREDENTIALS.md](CREDENTIALS.md)
- MySQL credentials
- Test accounts
- Database connection info
- Security notes

⏱️ Thời gian: 2 phút | 📖 Độ khó: Dễ

---

### **2. Setup & Configuration**

#### 📦 [README.md](README.md)
- Tổng quan project
- Quick start guide
- Tech stack
- Feature list

⏱️ Thời gian: 5 phút | 📖 Độ khó: Dễ

#### 🐳 [README-DOCKER.md](README-DOCKER.md)
- Docker setup chi tiết (English)
- Troubleshooting guide
- Docker commands
- Configuration options

⏱️ Thời gian: 10 phút | 📖 Độ khó: Trung bình

#### ✅ [SETUP-SUMMARY.md](SETUP-SUMMARY.md)
- Tổng kết các bước đã setup
- Files đã tạo
- Sample data overview
- Next steps

⏱️ Thời gian: 5 phút | 📖 Độ khó: Dễ

---

### **3. Architecture & Design**

#### 🏗️ [ARCHITECTURE.md](ARCHITECTURE.md)
- Docker architecture diagrams
- Data flow
- Network configuration
- Build process

⏱️ Thời gian: 8 phút | 📖 Độ khó: Trung bình

---

## 🛠️ CÁC SCRIPT TIỆN ÍCH

| Script | Mô tả | OS | Auto-run |
|--------|-------|-----|----------|
| `start.bat` | Khởi động Docker services | Windows | Yes |
| `stop.bat` | Dừng Docker services | Windows | Yes |
| `reset.bat` | Reset và rebuild | Windows | Yes |
| `logs.bat` | Xem logs interactively | Windows | Yes |
| `import-data.bat` | Import sample data | Windows | Yes |
| `check-data.bat` | Kiểm tra data trong DB | Windows | Yes |
| `fix-mysql-access.bat` | Fix MySQL access issues | Windows | Yes |

**Cách dùng:** Double-click vào file!

---

## 📋 WORKFLOW - NGƯỜI MỚI

### **Lần đầu tiên chạy project:**

1. ✅ Đọc [HUONG-DAN-CHAY-DOCKER.md](HUONG-DAN-CHAY-DOCKER.md)
2. ✅ Clone source code
3. ✅ Cài Docker Desktop
4. ✅ Chạy `start.bat`
5. ✅ Kết nối MySQL Workbench theo [CREDENTIALS.md](CREDENTIALS.md)
6. ✅ Mở [CHEAT-SHEET.md](CHEAT-SHEET.md) để tra cứu nhanh

### **Khi phát triển:**

1. 📖 Tham khảo [CHEAT-SHEET.md](CHEAT-SHEET.md) cho các lệnh
2. 🔍 Nếu gặp lỗi, xem phần Troubleshooting trong [HUONG-DAN-CHAY-DOCKER.md](HUONG-DAN-CHAY-DOCKER.md)
3. 🏗️ Muốn hiểu architecture, đọc [ARCHITECTURE.md](ARCHITECTURE.md)

### **Khi chia sẻ cho người khác:**

1. Chỉ họ đến file [HUONG-DAN-CHAY-DOCKER.md](HUONG-DAN-CHAY-DOCKER.md)
2. Chia sẻ [CREDENTIALS.md](CREDENTIALS.md) để biết thông tin đăng nhập

---

## 🎯 TÌM KIẾM THÔNG TIN NHANH

### **Tôi muốn...**

#### ...chạy project lần đầu
→ [HUONG-DAN-CHAY-DOCKER.md](HUONG-DAN-CHAY-DOCKER.md)

#### ...biết lệnh để restart app
→ [CHEAT-SHEET.md](CHEAT-SHEET.md)

#### ...biết password MySQL
→ [CREDENTIALS.md](CREDENTIALS.md)

#### ...kết nối MySQL Workbench
→ [HUONG-DAN-CHAY-DOCKER.md](HUONG-DAN-CHAY-DOCKER.md) - Bước 4

#### ...fix lỗi "Access Denied"
→ Chạy `fix-mysql-access.bat`

#### ...xem có bao nhiêu data
→ Chạy `check-data.bat`

#### ...reset lại từ đầu
→ Chạy `reset.bat`

#### ...hiểu kiến trúc Docker
→ [ARCHITECTURE.md](ARCHITECTURE.md)

#### ...biết đã setup những gì
→ [SETUP-SUMMARY.md](SETUP-SUMMARY.md)

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề:

1. ✅ Check [Troubleshooting section](HUONG-DAN-CHAY-DOCKER.md#xử-lý-lỗi-thường-gặp)
2. ✅ Xem logs: `docker-compose logs -f`
3. ✅ Thử reset: `reset.bat`
4. ✅ Google error message
5. ✅ Ask team members

---

## 🗂️ DANH SÁCH ĐẦY ĐỦ FILES

### **Documentation (8 files)**
- 📖 `INDEX.md` - File này
- 🇻🇳 `HUONG-DAN-CHAY-DOCKER.md` - **BẮT ĐẦU TẠI ĐÂY**
- ⚡ `CHEAT-SHEET.md` - Quick reference
- 🔐 `CREDENTIALS.md` - Login info
- 📦 `README.md` - Main README
- 🐳 `README-DOCKER.md` - Docker guide (EN)
- ✅ `SETUP-SUMMARY.md` - Setup summary
- 🏗️ `ARCHITECTURE.md` - Architecture diagrams

### **Docker Files (3 files)**
- `Dockerfile` - App build config
- `docker-compose.yml` - Services orchestration
- `.dockerignore` - Build context filter

### **Scripts (7 files)**
- `start.bat` - Start services
- `stop.bat` - Stop services
- `reset.bat` - Reset everything
- `logs.bat` - View logs
- `import-data.bat` - Import data
- `check-data.bat` - Check data
- `fix-mysql-access.bat` - Fix MySQL access

### **Code & Config**
- `pom.xml` - Maven dependencies
- `src/main/resources/application.properties` - App config
- `src/main/resources/init-data.sql` - Sample data
- `src/main/java/com/lanhcare/entity/` - 34 entity files

---

## ⭐ TIPS

💡 **Bookmark file này** để nhanh chóng tìm tài liệu cần thiết!

💡 **Print CHEAT-SHEET.md** ra giấy để tham khảo nhanh!

💡 **Chia sẻ HUONG-DAN-CHAY-DOCKER.md** cho teammates mới!

---

*Cập nhật lần cuối: 29/11/2024*
*LanhCare Health Tracking System © 2024*
