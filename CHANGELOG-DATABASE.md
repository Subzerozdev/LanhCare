# 📝 CHANGELOG - DATABASE SETUP

## 29/11/2024 - Cập nhật cách import data

### ✅ **Thay đổi:**

**TRƯỚC:**
- Sử dụng file `init-data.sql` được mount vào MySQL container
- MySQL tự động execute khi khởi tạo database lần đầu
- **VẤN ĐỀ:** Encoding UTF-8 bị lỗi, tiếng Việt hiển thị sai

**SAU:**
- Sử dụng script `import-all-data.bat` để import data
- Import từng table một với UTF-8 encoding đúng
- **ƯU ĐIỂM:** Tiếng Việt hiển thị 100% đúng, có thể chạy lại bất cứ lúc nào

### 📁 **Files đã xóa:**

- ❌ `src/main/resources/init-data.sql` - File SQL cũ (không dùng)
- ❌ `src/main/resources/init-data-fixed.sql` - File SQL đã fix (thay bằng .bat)

### 📁 **Files mới:**

- ✅ `import-all-data.bat` - Script import tất cả data với UTF-8
- ✅ `import-vietnamese-data.bat` - Script import từ file SQL (backup)

### 🔧 **Cấu hình đã thay đổi:**

**File: `docker-compose.yml`**
- Đã xóa volume mount: `./src/main/resources/init-data.sql:/docker-entrypoint-initdb.d/init-data.sql`
- Database giờ khởi động clean, không có data
- Cần chạy `import-all-data.bat` để import data

### 📖 **Cách sử dụng mới:**

**Bước 1:** Start Docker containers
```bash
docker-compose up -d --build
```

**Bước 2:** Import data
```bash
.\import-all-data.bat
```

**Bước 3:** Xem data trong MySQL Workbench
- Tất cả tiếng Việt hiển thị đúng! ✅

### ⚠️ **LƯU Ý:**

- Data **KHÔNG** được import tự động khi start containers
- **PHẢI** chạy `import-all-data.bat` sau khi start
- Có thể chạy lại script bao nhiêu lần cũng được (sẽ truncate và import lại)

---

## Tại sao thay đổi?

1. **UTF-8 Encoding:** File SQL mount vào Docker gặp vấn đề encoding, không đảm bảo UTF-8
2. **Flexibility:** Script .bat cho phép import/re-import dễ dàng
3. **Vietnamese Support:** Đảm bảo 100% tiếng Việt hiển thị đúng
4. **Debugging:** Dễ debug hơn khi import từng table

---

*Cập nhật bởi: Docker Setup Team*
*Ngày: 29/11/2024*
