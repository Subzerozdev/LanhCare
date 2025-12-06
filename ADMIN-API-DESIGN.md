# 🔐 Admin Module API - Complete Design Document

## 📋 Overview

Hệ thống API Admin cho LanhCare, bao gồm 5 modules chính:
1. **User Management** - Quản lý người dùng
2. **Hospital Management** - Quản lý phòng khám/bệnh viện
3. **Nutrition Management** - Quản lý dinh dưỡng
4. **Revenue & Transactions** - Quản lý doanh thu
5. **System Settings** - Cài đặt hệ thống

**Security:** Tất cả endpoints require `ROLE_ADMIN`

---

## 🔐 1. USER MANAGEMENT API

Base URL: `/api/admin/users`

### 1.1 Get All Users (Paginated)
```
GET /api/admin/users
```

**Query Parameters:**
- `page` (int, default=0) - Page number
- `size` (int, default=20) - Page size
- `search` (string, optional) - Search by email or fullname
- `role` (AccountRole, optional) - Filter by role (USER, ADMIN, DOCTOR, NUTRITIONIST)
- `status` (AccountStatus, optional) - Filter by status (ACTIVE, INACTIVE, SUSPENDED, DELETED)
- `sortBy` (string, default="id") - Sort field
- `sortDir` (string, default="ASC") - Sort direction

**Response:**
```json
{
  "status": 200,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "email": "admin@lanhcare.com",
        "fullname": "Admin User",
        "role": "ADMIN",
        "status": "ACTIVE",
        "createdAt": "2024-11-29T10:00:00",
        "transactionCount": 5,
        "totalSpent": 500000.00
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 100,
      "totalPages": 5
    }
  }
}
```

### 1.2 Get User Detail
```
GET /api/admin/users/{id}
```

**Response:**
```json
{
  "status": 200,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullname": "John Doe",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2024-11-29T10:00:00",
    "healthProfile": {
      "heightCm": 175.0,
      "currentWeightKg": 70.0,
      "bmi": 22.86,
      "activityLevel": "MODERATE"
    },
    "transactions": [
      {
        "id": 1,
        "amount": 100000.00,
        "status": "COMPLETED",
        "transactionDate": "2024-12-01T14:30:00"
      }
    ],
    "mealLogCount": 150
  }
}
```

### 1.3 Create User
```
POST /api/admin/users
```

**Request:**
```json
{
  "email": "newuser@example.com",
  "fullname": "New User",
  "password": "SecurePassword123",
  "role": "USER",
  "status": "ACTIVE"
}
```

**Response:**
```json
{
  "status": 201,
  "message": "User created successfully",
  "data": {
    "id": 10,
    "email": "newuser@example.com",
    "fullname": "New User",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

### 1.4 Update User
```
PUT /api/admin/users/{id}
```

**Request:**
```json
{
  "fullname": "Updated Name",
  "role": "DOCTOR",
  "status": "ACTIVE"
}
```

### 1.5 Change User Status
```
PATCH /api/admin/users/{id}/status
```

**Request:**
```json
{
  "status": "SUSPENDED"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "User status updated successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "status": "SUSPENDED"
  }
}
```

### 1.6 Delete User (Soft Delete)
```
DELETE /api/admin/users/{id}
```

**Response:**
```json
{
  "status": 200,
  "message": "User deleted successfully",
  "data": null
}
```

---

## 🏥 2. HOSPITAL MANAGEMENT API

Base URL: `/api/admin/hospitals`

### 2.1 Get All Hospitals (Paginated)
```
GET /api/admin/hospitals?page=0&size=20&status=ACTIVE
```

**Response:**
```json
{
  "status": 200,
  "message": "Hospitals retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Bệnh viện Chợ Rẫy",
        "address": "201B Nguyễn Chí Thanh, Q.5, TP.HCM",
        "latitude": 10.7546,
        "longitude": 106.6677,
        "status": "ACTIVE",
        "specialtyCount": 15
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 50,
      "totalPages": 3
    }
  }
}
```

### 2.2 Get Hospital Detail
```
GET /api/admin/hospitals/{id}
```

**Response:**
```json
{
  "status": 200,
  "message": "Hospital retrieved successfully",
  "data": {
    "id": 1,
    "name": "Bệnh viện Chợ Rẫy",
    "address": "201B Nguyễn Chí Thanh, Q.5, TP.HCM",
    "latitude": 10.7546,
    "longitude": 106.6677,
    "status": "ACTIVE",
    "specialties": [
      {
        "id": 1,
        "nameVn": "Tim mạch",
        "nameEn": "Cardiology",
        "status": "ACTIVE"
      }
    ]
  }
}
```

### 2.3 Create Hospital
```
POST /api/admin/hospitals
```

**Request:**
```json
{
  "name": "Bệnh viện Mới",
  "address": "123 Đường ABC, Q.1, TP.HCM",
  "latitude": 10.7769,
  "longitude": 106.7009,
  "status": "ACTIVE"
}
```

### 2.4 Update Hospital
```
PUT /api/admin/hospitals/{id}
```

**Request:**
```json
{
  "name": "Bệnh viện Updated",
  "address": "Updated Address",
  "latitude": 10.7769,
  "longitude": 106.7009,
  "status": "ACTIVE"
}
```

### 2.5 Update Hospital Status
```
PATCH /api/admin/hospitals/{id}/status
```

**Request:**
```json
{
  "status": "TEMPORARILY_CLOSED"
}
```

### 2.6 Delete Hospital
```
DELETE /api/admin/hospitals/{id}
```

### 2.7 Manage Specialties
```
POST /api/admin/hospitals/{hospitalId}/specialties
```

**Request:**
```json
{
  "nameVn": "Nội khoa",
  "nameEn": "Internal Medicine",
  "icdUri": null,
  "status": "ACTIVE"
}
```

```
DELETE /api/admin/hospitals/{hospitalId}/specialties/{specialtyId}
```

---

## 🍽️ 3. NUTRITION MANAGEMENT API

Base URL: `/api/admin/nutrition`

### 3.1 Food Type Management

#### Get All Food Types
```
GET /api/admin/nutrition/food-types
```

**Response:**
```json
{
  "status": 200,
  "message": "Food types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Carbohydrates",
      "isDeleted": false,
      "foodItemCount": 25
    }
  ]
}
```

#### Create Food Type
```
POST /api/admin/nutrition/food-types
```

**Request:**
```json
{
  "name": "Protein"
}
```

#### Update Food Type
```
PUT /api/admin/nutrition/food-types/{id}
```

#### Delete Food Type (Soft Delete)
```
DELETE /api/admin/nutrition/food-types/{id}
```

---

### 3.2 Food Item Management

#### Get All Food Items (Paginated)
```
GET /api/admin/nutrition/food-items?page=0&size=20&status=ACTIVE&foodTypeId=1
```

**Response:**
```json
{
  "status": 200,
  "message": "Food items retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Cơm trắng",
        "description": "100g cơm trắng",
        "calo": 130.00,
        "servingUnit": "g",
        "standardServingSize": 100.00,
        "status": "ACTIVE",
        "foodType": {
          "id": 1,
          "name": "Carbohydrates"
        },
        "imageUrl": null,
        "nutrientCount": 5
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 150,
      "totalPages": 8
    }
  }
}
```

#### Get Food Item Detail
```
GET /api/admin/nutrition/food-items/{id}
```

**Response:**
```json
{
  "status": 200,
  "message": "Food item retrieved successfully",
  "data": {
    "id": 1,
    "name": "Cơm trắng",
    "description": "100g cơm trắng",
    "calo": 130.00,
    "servingUnit": "g",
    "standardServingSize": 100.00,
    "status": "ACTIVE",
    "foodType": {
      "id": 1,
      "name": "Carbohydrates"
    },
    "dataSource": "USDA",
    "imageUrl": null,
    "nutrients": [
      {
        "nutrientName": "Protein",
        "amount": 2.7,
        "unit": "g"
      },
      {
        "nutrientName": "Carbs",
        "amount": 28.0,
        "unit": "g"
      }
    ]
  }
}
```

#### Create Food Item
```
POST /api/admin/nutrition/food-items
```

**Request:**
```json
{
  "name": "Phở bò",
  "description": "1 tô phở bò",
  "calo": 350.00,
  "servingUnit": "tô",
  "standardServingSize": 1.00,
  "foodTypeId": 1,
  "status": "ACTIVE",
  "dataSource": "LanhCare",
  "imageUrl": "https://example.com/pho.jpg"
}
```

#### Update Food Item
```
PUT /api/admin/nutrition/food-items/{id}
```

#### Update Food Item Status
```
PATCH /api/admin/nutrition/food-items/{id}/status
```

**Request:**
```json
{
  "status": "INACTIVE"
}
```

#### Delete Food Item
```
DELETE /api/admin/nutrition/food-items/{id}
```

---

### 3.3 Nutrient Management

#### Get All Nutrients
```
GET /api/admin/nutrition/nutrients
```

**Response:**
```json
{
  "status": 200,
  "message": "Nutrients retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Protein",
      "unit": "g"
    },
    {
      "id": 2,
      "name": "Carbohydrates",
      "unit": "g"
    }
  ]
}
```

#### Create Nutrient
```
POST /api/admin/nutrition/nutrients
```

**Request:**
```json
{
  "name": "Vitamin C",
  "unit": "mg"
}
```

#### Update Nutrient
```
PUT /api/admin/nutrition/nutrients/{id}
```

#### Delete Nutrient
```
DELETE /api/admin/nutrition/nutrients/{id}
```

---

## 💰 4. REVENUE & TRANSACTION API

Base URL: `/api/admin/revenue`

### 4.1 Get All Transactions (Paginated)
```
GET /api/admin/revenue/transactions?page=0&size=20&status=COMPLETED&startDate=2024-01-01&endDate=2024-12-31&userId=1
```

**Query Parameters:**
- `page`, `size` - Pagination
- `status` - Filter by TransactionStatus (PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED)
- `startDate`, `endDate` - Date range filter
- `userId` - Filter by user
- `servicePlanId` - Filter by service plan

**Response:**
```json
{
  "status": 200,
  "message": "Transactions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "userEmail": "user@example.com",
        "userName": "John Doe",
        "servicePlanName": "Premium Plan",
        "amount": 500000.00,
        "paymentMethod": "Credit Card",
        "status": "COMPLETED",
        "transactionDate": "2024-12-01T14:30:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 500,
      "totalPages": 25
    }
  }
}
```

### 4.2 Get Transaction Detail
```
GET /api/admin/revenue/transactions/{id}
```

**Response:**
```json
{
  "status": 200,
  "message": "Transaction retrieved successfully",
  "data": {
    "id": 1,
    "account": {
      "id": 1,
      "email": "user@example.com",
      "fullname": "John Doe"
    },
    "servicePlan": {
      "id": 1,
      "name": "Premium Plan",
      "price": 500000.00,
      "periodValue": 1,
      "periodUnit": "MONTH"
    },
    "amount": 500000.00,
    "paymentMethod": "Credit Card",
    "status": "COMPLETED",
    "transactionDate": "2024-12-01T14:30:00"
  }
}
```

### 4.3 Get Revenue Statistics
```
GET /api/admin/revenue/statistics?startDate=2024-01-01&endDate=2024-12-31
```

**Response:**
```json
{
  "status": 200,
  "message": "Revenue statistics retrieved successfully",
  "data": {
    "totalRevenue": 50000000.00,
    "totalTransactions": 500,
    "completedTransactions": 480,
    "pendingTransactions": 10,
    "failedTransactions": 10,
    "averageTransactionValue": 100000.00,
    "revenueByMonth": [
      {
        "month": "2024-01",
        "revenue": 4000000.00,
        "transactionCount": 40
      }
    ],
    "revenueByServicePlan": [
      {
        "servicePlanId": 1,
        "servicePlanName": "Premium Plan",
        "revenue": 30000000.00,
        "transactionCount": 300
      }
    ]
  }
}
```

### 4.4 Export Revenue Report
```
GET /api/admin/revenue/export?startDate=2024-01-01&endDate=2024-12-31&format=CSV
```

**Query Parameters:**
- `startDate`, `endDate` - Date range
- `format` - Export format (CSV, EXCEL, PDF)

**Response:**
- File download (CSV/EXCEL/PDF)

---

## ⚙️ 5. SYSTEM SETTINGS API

Base URL: `/api/admin/settings`

### 5.1 Get All Settings
```
GET /api/admin/settings
```

**Response:**
```json
{
  "status": 200,
  "message": "Settings retrieved successfully",
  "data": [
    {
      "id": 1,
      "key": "MAINTENANCE_MODE",
      "value": "false",
      "description": "Enable/disable maintenance mode",
      "updatedAt": "2024-12-01T10:00:00"
    },
    {
      "id": 2,
      "key": "MAX_UPLOAD_SIZE",
      "value": "10485760",
      "description": "Maximum file upload size in bytes",
      "updatedAt": "2024-12-01T10:00:00"
    }
  ]
}
```

### 5.2 Get Setting by Key
```
GET /api/admin/settings/{key}
```

**Response:**
```json
{
  "status": 200,
  "message": "Setting retrieved successfully",
  "data": {
    "id": 1,
    "key": "MAINTENANCE_MODE",
    "value": "false",
    "description": "Enable/disable maintenance mode",
    "updatedAt": "2024-12-01T10:00:00"
  }
}
```

### 5.3 Create/Update Setting
```
PUT /api/admin/settings/{key}
```

**Request:**
```json
{
  "value": "true",
  "description": "Enable/disable maintenance mode"
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Setting updated successfully",
  "data": {
    "id": 1,
    "key": "MAINTENANCE_MODE",
    "value": "true",
    "description": "Enable/disable maintenance mode",
    "updatedAt": "2024-12-06T13:00:00"
  }
}
```

### 5.4 Delete Setting
```
DELETE /api/admin/settings/{key}
```

---

## 🔒 Security Configuration

All Admin endpoints:
- Require JWT authentication
- Require `ROLE_ADMIN`
- Protected by `@PreAuthorize("hasRole('ADMIN')")`
- Return 403 Forbidden if not admin

**Already configured in `SecurityConfig.java`:**
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

---

## 📦 DTOs Structure

### Common Response Wrapper
```java
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
}
```

### Pagination Response
```java
public class PageResponse<T> {
    private List<T> content;
    private PageInfo pageable;
}

public class PageInfo {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
```

---

## ✅ Implementation Checklist

### Phase 1: User Management
- [ ] AdminUserController
- [ ] AdminUserService
- [ ] User DTOs (AdminUserResponse, AdminUserRequest, etc.)
- [ ] Extend AccountRepository with admin queries

### Phase 2: Hospital Management
- [ ] AdminHospitalController
- [ ] AdminHospitalService
- [ ] Hospital DTOs
- [ ] Extend HospitalRepository
- [ ] Create MedicalSpecialtyRepository (if not exists)

### Phase 3: Nutrition Management
- [ ] AdminNutritionController
- [ ] AdminNutritionService
- [ ] Nutrition DTOs
- [ ] Extend FoodItemRepository, FoodTypeRepository, NutrientRepository
- [ ] Create NutrientRepository (if not exists)

### Phase 4: Revenue & Transactions
- [ ] AdminRevenueController
- [ ] AdminRevenueService
- [ ] Revenue DTOs
- [ ] Extend TransactionRepository with statistics queries

### Phase 5: System Settings
- [ ] AdminSettingsController
- [ ] AdminSettingsService
- [ ] SystemSetting Entity (NEW)
- [ ] SystemSettingRepository (NEW)
- [ ] Settings DTOs

---

## 📝 Notes for Implementation

1. **Existing Entities:** Reuse Account, Hospital, FoodItem, FoodType, Transaction, Nutrient, MedicalSpecialty
2. **New Entity Required:** SystemSetting (for settings management)
3. **Repository Extensions:** Add custom queries for filtering, searching, statistics
4. **MapStruct:** Sử dụng cho DTO mapping (đã có trong project)
5. **Exception Handling:** Sử dụng GlobalExceptionHandler hiện có
6. **Pagination:** Sử dụng Spring Data Pageable
7. **Validation:** Jakarta Validation annotations

---

**Next Steps:**
1. Confirm design with you
2. Create Entity SystemSetting (if approved)
3. Generate all DTOs
4. Implement Controllers
5. Implement Services
6. Extend Repositories
7. Test with Swagger

