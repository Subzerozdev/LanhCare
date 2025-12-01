# ⚠️ KNOWN ISSUE - Code Mismatch

## 🔴 Vấn Đề Hiện Tại

Project hiện tại có một số mismatch giữa **Entity definitions** (đã tồn tại) và **Services** (mới tạo):

### UserHealthProfile Entity vs Service

**Entity có:**
- `weightKg`
- `heightCm`  
- `bmiValue`
- `healthGoals`

**Service đang dùng:**
- `currentWeightKg` ❌
- `targetWeightKg` ❌
- `medicalConditions` ❌
- `allergies` ❌

## 🔧 Giải Pháp Nhanh

### Option 1: Disable Health Profile Features (Nhanh nhất)

Comment out Health Profile service và controller để project có thể chạy:

**Files cần comment:**
1. `src/main/java/com/lanhcare/service/HealthProfileService.java`
2. `src/main/java/com/lanhcare/controller/HealthProfileController.java`

**Cách làm:**
- Thêm `/* ... */` ở đầu và cuối file
- Hoặc xóa 2 files này tạm thời

### Option 2: Fix Entity để Match với Service (Khuyến khích)

Update `UserHealthProfile.java` entity:

```java
// Thêm các fields này
@Column(name = "current_weight_kg", precision = 5, scale = 2)
private BigDecimal currentWeightKg;

@Column(name = "target_weight_kg", precision = 5, scale = 2)
private BigDecimal targetWeightKg;

@Column(name = "medical_conditions", columnDefinition = "TEXT")
private String medicalConditions;

@Column(name = "allergies", columnDefinition = "TEXT")
private String allergies;

// Đổi tên field cũ
// weightKg -> rename hoặc xóa
```

### Option 3: Fix Service để Match với Entity

Update `HealthProfileService.java` để sử dụng:
- `weightKg` thay vì `currentWeightKg`
- `heightCm` (OK)
- Xóa references tới `targetWeightKg`, `medicalConditions`, `allergies`

## 🚀 Cách Chạy Project (Tạm Thời)

### Bước 1: Disable Problematic Services

```bash
# Rename files tạm thời
mv src/main/java/com/lanhcare/controller/HealthProfileController.java src/main/java/com/lanhcare/controller/HealthProfileController.java.bak
mv src/main/java/com/lanhcare/service/HealthProfileService.java src/main/java/com/lanhcare/service/HealthProfileService.java.bak
```

### Bước 2: Build và Run

```bash
docker-compose up -d --build
```

### Bước 3: Test

Các endpoints này sẽ hoạt động:
- ✅ `/api/auth/*` - Authentication
- ✅ `/api/accounts/*` - Accounts
- ✅ `/api/foods/*` - Foods
- ✅ `/api/meal-logs/*` - Meal logs
- ❌ `/api/health-profiles/*` - DISABLED

## ✅ Sau Khi Fix

Khi đã fix entity hoặc service:

1. Rename files trở lại (remove `.bak`)
2. Rebuild: `docker-compose up -d --build`
3. Test health profile endpoints

## 📝 Root Cause

Vấn đề xảy ra vì:
1. Entity `UserHealthProfile` đã tồn tại từ trước với structure cụ thể
2. Khi tạo service mới, tôi thiết kế dựa trên requirements chứ không check entity hiện tại
3. Cần align giữa 2 layers này

## 🎯 Recommended Solution

**Tôi khuyến khích Option 2:**
1. Update entity để match với service design (có nhiều fields hơn, tốt hơn)
2. Run migration nếu cần
3. Rebuild project

Hoặc nếu muốn giữ entity cũ, thì chọn Option 3 và simplify service.

## 💡 Next Steps

1. Chọn một trong 3 options trên
2. Apply changes
3. Run `docker-compose up -d --build`
4. Test qua Swagger UI

---

**Xin lỗi vì inconvenience này! Tôi sẽ hỗ trợ fix nếu cần.** 🙏
