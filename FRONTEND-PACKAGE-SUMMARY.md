# 📦 LANHCARE BACKEND - PACKAGE FOR NEXT.JS FRONTEND

## 🎯 **MỤC ĐÍCH**

Package này chứa **TẤT CẢ** thông tin cần thiết để Frontend Developer có thể integrate với Backend LanhCare.

---

## 📋 **DANH SÁCH FILE CẦN CHIA SẺ**

### **1. Main Documentation Files** ⭐

| File | Mô tả | Độ ưu tiên |
|------|-------|------------|
| **NEXTJS-INTEGRATION-GUIDE.md** | 🔥 **BẮT BUỘC** - Guide đầy đủ cho Next.js | ⭐⭐⭐⭐⭐ |
| **API-DOCUMENTATION.md** | API docs cho endpoints cũ | ⭐⭐⭐⭐ |
| **ADMIN-API-DESIGN.md** | Admin API specs chi tiết | ⭐⭐⭐⭐ |
| **ADMIN-MODULE-COMPLETE.md** | Completion report & features | ⭐⭐⭐ |

### **2. Quick Reference**

| Thông tin | Giá trị |
|-----------|---------|
| **Backend URL** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **Admin Email** | `admin@lanhcare.com` |
| **Admin Password** | `password123` |
| **Database** | MySQL 8.0 (port 3307) |

---

## 🚀 **QUICK START CHO FRONTEND DEV**

### **Bước 1: Đọc Documentation**
```
1. ĐỌC NGAY: NEXTJS-INTEGRATION-GUIDE.md ⭐⭐⭐⭐⭐
2. Tham khảo: API-DOCUMENTATION.md
3. Chi tiết admin API: ADMIN-API-DESIGN.md
```

### **Bước 2: Copy Code Templates**

Từ `NEXTJS-INTEGRATION-GUIDE.md`, copy các files sau vào Next.js project:

#### **TypeScript Types** (copy từ section 4)
```
types/
├── common.ts          ⭐ API Response, Pagination, Enums
├── user.ts            ⭐ User types
├── hospital.ts        Hospital types
├── nutrition.ts       Nutrition types
├── revenue.ts         Revenue & transaction types
└── settings.ts        System settings types
```

#### **API Services** (copy từ section 5)
```
services/
├── auth.service.ts       ⭐ Login/logout
├── user.service.ts       ⭐ User management
├── hospital.service.ts   Hospital management
├── nutrition.service.ts  Nutrition management
├── revenue.service.ts    Revenue & transactions
└── settings.service.ts   System settings
```

#### **Core Setup** (copy từ section 3)
```
lib/
└── api-client.ts      ⭐ Axios client với interceptors
```

#### **Context** (copy từ section 7)
```
context/
└── AuthContext.tsx    ⭐ Authentication state
```

### **Bước 3: Environment Setup**
Tạo `.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_API_TIMEOUT=30000
```

### **Bước 4: Test Connection**
```typescript
// Test login
import { authService } from '@/services/auth.service';

const response = await authService.login({
  email: 'admin@lanhcare.com',
  password: 'password123'
});

console.log('Token:', response.data.accessToken);
```

---

## 📡 **TOÀN BỘ API ENDPOINTS**

### **Public APIs (không cần token)**
```
POST /api/auth/register     - Đăng ký
POST /api/auth/login        - Đăng nhập
POST /api/auth/google       - Google OAuth
GET  /api/auth/health       - Health check
```

### **Admin APIs (cần token + ADMIN role)** 🔐

#### **User Management (6 endpoints)**
```
GET    /api/admin/users                - List users (paginated)
GET    /api/admin/users/{id}           - User detail
POST   /api/admin/users                - Create user
PUT    /api/admin/users/{id}           - Update user
PATCH  /api/admin/users/{id}/status    - Change status
DELETE /api/admin/users/{id}           - Delete user (soft)
```

#### **Hospital Management (8 endpoints)**
```
GET    /api/admin/hospitals                               - List hospitals
GET    /api/admin/hospitals/{id}                          - Hospital detail
POST   /api/admin/hospitals                               - Create hospital
PUT    /api/admin/hospitals/{id}                          - Update hospital
PATCH  /api/admin/hospitals/{id}/status                   - Update status
DELETE /api/admin/hospitals/{id}                          - Delete hospital
POST   /api/admin/hospitals/{id}/specialties              - Add specialty
DELETE /api/admin/hospitals/{hId}/specialties/{sId}       - Delete specialty
```

#### **Nutrition Management (10 endpoints)**
```
# Food Items
GET    /api/admin/nutrition/food-items
POST   /api/admin/nutrition/food-items
PUT    /api/admin/nutrition/food-items/{id}
DELETE /api/admin/nutrition/food-items/{id}

# Food Types
GET    /api/admin/nutrition/food-types
POST   /api/admin/nutrition/food-types
DELETE /api/admin/nutrition/food-types/{id}

# Nutrients
GET    /api/admin/nutrition/nutrients
POST   /api/admin/nutrition/nutrients
DELETE /api/admin/nutrition/nutrients/{id}
```

#### **Revenue & Transactions (4 endpoints)**
```
GET /api/admin/revenue/transactions         - List transactions
GET /api/admin/revenue/transactions/{id}    - Transaction detail
GET /api/admin/revenue/statistics           - Revenue stats ⭐
GET /api/admin/revenue/export               - Export (CSV/Excel/PDF) ⭐
```

#### **System Settings (4 endpoints)**
```
GET    /api/admin/settings
GET    /api/admin/settings/{key}
PUT    /api/admin/settings/{key}
DELETE /api/admin/settings/{key}
```

**TOTAL: 32+ Admin Endpoints**

---

## 🔐 **AUTHENTICATION FLOW**

### **1. Login Request**
```typescript
POST /api/auth/login
{
  "email": "admin@lanhcare.com",
  "password": "password123"
}
```

### **2. Response**
```typescript
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "account": {
      "id": 1,
      "email": "admin@lanhcare.com",
      "fullname": "Admin User",
      "role": "ADMIN"
    }
  }
}
```

### **3. Store Token**
```typescript
localStorage.setItem('accessToken', response.data.accessToken);
```

### **4. Use in Requests**
```typescript
headers: {
  'Authorization': `Bearer ${accessToken}`,
  'Content-Type': 'application/json'
}
```

---

## 📊 **DATA MODELS (TypeScript)**

### **User Model**
```typescript
interface AdminUserResponse {
  id: number;
  email: string;
  fullname: string;
  role: 'USER' | 'ADMIN' | 'DOCTOR' | 'NUTRITIONIST';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'DELETED';
  transactionCount: number;
  totalSpent: number;
}
```

### **Hospital Model**
```typescript
interface Hospital {
  id: number;
  name: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  status: 'ACTIVE' | 'INACTIVE' | 'UNDER_CONSTRUCTION' | 'TEMPORARILY_CLOSED';
  specialtyCount: number;
}
```

### **Revenue Stats Model**
```typescript
interface RevenueStats {
  totalRevenue: number;
  totalTransactions: number;
  completedTransactions: number;
  pendingTransactions: number;
  failedTransactions: number;
  averageTransactionValue: number;
  revenueByMonth: MonthlyRevenue[];
  revenueByServicePlan: ServicePlanRevenue[];
}
```

**📌 Full type definitions in NEXTJS-INTEGRATION-GUIDE.md**

---

## 🎨 **RECOMMENDED UI LIBRARIES**

### **Essential**
```bash
npm install axios          # HTTP client
npm install typescript     # Type safety
```

### **Recommended**
```bash
npm install recharts              # Charts for revenue
npm install @tanstack/react-query # Data fetching
npm install zustand               # State management
npm install tailwindcss           # Styling
npm install @shadcn/ui            # UI components
```

---

## 🛠️ **TESTING CHECKLIST**

### **Backend Already Running?**
- [ ] Docker containers up: `docker ps`
- [ ] Backend accessible: http://localhost:8080
- [ ] Swagger working: http://localhost:8080/swagger-ui/index.html
- [ ] Can login with admin@lanhcare.com

### **Frontend Setup**
- [ ] Next.js project created
- [ ] Axios installed
- [ ] Types copied from guide
- [ ] Services copied from guide
- [ ] API client configured
- [ ] `.env.local` created

### **Test APIs**
- [ ] Can login and get token
- [ ] Can fetch users list
- [ ] Can create new user
- [ ] Can fetch hospitals
- [ ] Can get revenue statistics
- [ ] Can export CSV/Excel

---

## 🔥 **SPECIAL FEATURES**

### **1. Pagination Support** ✅
All list endpoints support:
- `page` - Page number (0-indexed)
- `size` - Items per page
- `sortBy` - Sort field
- `sortDir` - ASC or DESC

### **2. Advanced Filtering** ✅
User API supports:
- `search` - Search by email/name
- `role` - Filter by role
- `status` - Filter by status

### **3. Export Functionality** ✅
Revenue export supports:
- CSV format
- Excel format (.xlsx)
- PDF format

### **4. Soft Delete** ✅
All delete operations are soft deletes:
- Users: status = DELETED
- Hospitals: status = INACTIVE
- Food Items: status = ARCHIVED

### **5. Revenue Analytics** ✅
Statistics API provides:
- Total revenue
- Transaction counts by status
- Average transaction value
- Monthly breakdown
- Service plan breakdown

---

## 📞 **SUPPORT & RESOURCES**

### **Documentation Files**
1. **NEXTJS-INTEGRATION-GUIDE.md** - Full integration guide
2. **API-DOCUMENTATION.md** - API reference
3. **ADMIN-API-DESIGN.md** - Admin API specs
4. **ADMIN-MODULE-COMPLETE.md** - Feature list

### **Interactive Testing**
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- Test all endpoints interactively
- See request/response examples
- Copy example JSON

### **Example Code**
Check `NEXTJS-INTEGRATION-GUIDE.md` sections:
- Section 5: All service functions
- Section 6: Usage examples
- Section 7: Auth setup

---

## ✅ **DELIVERABLES SUMMARY**

### **What You Have:**
✅ Complete Backend API (32+ endpoints)  
✅ Full TypeScript types  
✅ All service functions (ready to copy)  
✅ Authentication flow  
✅ Example components  
✅ Protected route setup  
✅ Export functionality (CSV/Excel/PDF)  
✅ Revenue analytics  
✅ Comprehensive documentation  

### **What Frontend Needs to Build:**
1. ⬜ Admin dashboard UI
2. ⬜ User management interface
3. ⬜ Hospital management interface
4. ⬜ Nutrition management interface
5. ⬜ Revenue dashboard with charts
6. ⬜ System settings interface

### **Integration Time Estimate:**
- API Setup: 1-2 hours
- Basic Auth Flow: 2-3 hours
- User Management UI: 4-6 hours
- Revenue Dashboard: 3-4 hours
- Complete Admin Panel: 2-3 days

---

## 🎯 **SUCCESS CRITERIA**

Frontend integration thành công khi:
- [x] Backend running on localhost:8080
- [ ] Next.js can login and get token
- [ ] Can fetch and display users list
- [ ] Can create/update/delete users
- [ ] Can view revenue statistics
- [ ] Can export transactions
- [ ] Protected routes working
- [ ] Error handling working

---

## 🚀 **READY TO GO!**

**Main File to Share:** `NEXTJS-INTEGRATION-GUIDE.md`

This guide contains:
- ✅ Environment setup
- ✅ Complete TypeScript types
- ✅ All API service functions
- ✅ Authentication setup
- ✅ Example components
- ✅ Best practices
- ✅ Full API endpoint list

**Just share this file and Frontend Dev can start immediately!** 🎉

---

**Created:** December 6, 2024  
**Backend Version:** 1.0.0  
**Total Endpoints:** 32+ Admin APIs  
**Status:** ✅ Production Ready
