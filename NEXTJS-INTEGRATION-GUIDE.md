# 🚀 NEXT.JS INTEGRATION GUIDE - LANHCARE ADMIN PANEL

## 📋 **PACKAGE INFORMATION**

Tài liệu này chứa **TẤT CẢ** thông tin cần thiết để integrate Backend LanhCare với Next.js Frontend.

**Backend API Base URL:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## 📦 **1. ENVIRONMENT VARIABLES**

Tạo file `.env.local` trong Next.js project:

```env
# Backend API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_API_TIMEOUT=30000

# Google OAuth (nếu dùng Google Login)
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your_google_client_id_here
```

---

## 🔐 **2. AUTHENTICATION FLOW**

### **2.1. Login API**

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "admin@lanhcare.com",
  "password": "password123"
}
```

**Response:**
```json
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

### **2.2. Store Token**

```typescript
// Store in localStorage or cookies
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('user', JSON.stringify(response.data.account));
```

### **2.3. Authenticated Requests**

**Headers:**
```typescript
{
  'Authorization': `Bearer ${accessToken}`,
  'Content-Type': 'application/json'
}
```

---

## 📡 **3. API CLIENT SETUP**

### **3.1. Create API Client (TypeScript)**

Tạo file: `lib/api-client.ts`

```typescript
import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor - Add token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor - Handle errors
    this.client.interceptors.response.use(
      (response) => response.data,
      (error) => {
        if (error.response?.status === 401) {
          // Unauthorized - redirect to login
          localStorage.removeItem('accessToken');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return this.client.get(url, config);
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.client.post(url, data, config);
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.client.put(url, data, config);
  }

  async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.client.patch(url, data, config);
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return this.client.delete(url, config);
  }
}

export const apiClient = new ApiClient();
```

---

## 🎯 **4. TYPESCRIPT TYPES/INTERFACES**

### **4.1. Common Types**

Tạo file: `types/common.ts`

```typescript
// API Response Wrapper
export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

// Pagination
export interface PageInfo {
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface PageResponse<T> {
  content: T[];
  pageable: PageInfo;
}

// Enums
export enum AccountRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
  DOCTOR = 'DOCTOR',
  NUTRITIONIST = 'NUTRITIONIST'
}

export enum AccountStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  DELETED = 'DELETED'
}

export enum HospitalStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  UNDER_CONSTRUCTION = 'UNDER_CONSTRUCTION',
  TEMPORARILY_CLOSED = 'TEMPORARILY_CLOSED'
}

export enum FoodItemStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  PENDING_REVIEW = 'PENDING_REVIEW',
  ARCHIVED = 'ARCHIVED'
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
  CANCELLED = 'CANCELLED'
}
```

### **4.2. User Types**

Tạo file: `types/user.ts`

```typescript
import { AccountRole, AccountStatus } from './common';

export interface User {
  id: number;
  email: string;
  fullname: string;
  role: AccountRole;
  status: AccountStatus;
}

export interface AdminUserResponse extends User {
  transactionCount: number;
  totalSpent: number;
}

export interface AdminUserDetailResponse extends User {
  healthProfile: HealthProfileSummary | null;
  recentTransactions: TransactionSummary[];
  totalTransactionCount: number;
  totalSpent: number;
  mealLogCount: number;
}

export interface HealthProfileSummary {
  heightCm: number | null;
  currentWeightKg: number | null;
  bmi: number | null;
  activityLevel: string | null;
}

export interface TransactionSummary {
  id: number;
  amount: number;
  status: string;
  transactionDate: string;
}

export interface CreateUserRequest {
  email: string;
  fullname: string;
  password: string;
  role: AccountRole;
  status?: AccountStatus;
}

export interface UpdateUserRequest {
  fullname?: string;
  role?: AccountRole;
  status?: AccountStatus;
}
```

### **4.3. Hospital Types**

Tạo file: `types/hospital.ts`

```typescript
import { HospitalStatus } from './common';

export interface Hospital {
  id: number;
  name: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  status: HospitalStatus;
  specialtyCount: number;
}

export interface CreateHospitalRequest {
  name: string;
  address: string;
  latitude?: number;
  longitude?: number;
  status?: HospitalStatus;
}

export interface Specialty {
  id: number;
  nameVn: string;
  nameEn: string;
  status: string;
}

export interface CreateSpecialtyRequest {
  nameVn: string;
  nameEn: string;
  icdUri?: string;
  status?: string;
}
```

### **4.4. Nutrition Types**

Tạo file: `types/nutrition.ts`

```typescript
import { FoodItemStatus } from './common';

export interface FoodItem {
  id: number;
  name: string;
  description: string;
  calo: number;
  servingUnit: string;
  standardServingSize: number;
  status: FoodItemStatus;
  foodTypeName: string;
  imageUrl: string | null;
  nutrientCount: number;
}

export interface CreateFoodItemRequest {
  name: string;
  description: string;
  calo: number;
  servingUnit: string;
  standardServingSize: number;
  foodTypeId: number;
  dataSource?: string;
  imageUrl?: string;
  status?: FoodItemStatus;
}

export interface FoodType {
  id: number;
  name: string;
  isDeleted: boolean;
}

export interface Nutrient {
  id: number;
  name: string;
  unit: string;
}
```

### **4.5. Revenue Types**

Tạo file: `types/revenue.ts`

```typescript
import { TransactionStatus } from './common';

export interface Transaction {
  id: number;
  userId: number;
  userEmail: string;
  userName: string;
  servicePlanName: string;
  amount: number;
  paymentMethod: string | null;
  status: string;
  transactionDate: string;
}

export interface RevenueStats {
  totalRevenue: number;
  totalTransactions: number;
  completedTransactions: number;
  pendingTransactions: number;
  failedTransactions: number;
  averageTransactionValue: number;
  revenueByMonth: MonthlyRevenue[] | null;
  revenueByServicePlan: ServicePlanRevenue[] | null;
}

export interface MonthlyRevenue {
  month: string;
  revenue: number;
  transactionCount: number;
}

export interface ServicePlanRevenue {
  servicePlanId: number;
  servicePlanName: string;
  revenue: number;
  transactionCount: number;
}
```

### **4.6. Settings Types**

Tạo file: `types/settings.ts`

```typescript
export interface SystemSetting {
  id: number;
  key: string;
  value: string;
  description: string | null;
  updatedAt: string | null;
}

export interface UpdateSettingRequest {
  value: string;
  description?: string;
}
```

---

## 🔌 **5. API SERVICE FUNCTIONS**

### **5.1. Auth Service**

Tạo file: `services/auth.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse } from '@/types/common';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  account: {
    id: number;
    email: string;
    fullname: string;
    role: string;
  };
}

export const authService = {
  login: async (credentials: LoginRequest) => {
    return apiClient.post<ApiResponse<LoginResponse>>('/api/auth/login', credentials);
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },

  isAdmin: () => {
    const user = authService.getCurrentUser();
    return user?.role === 'ADMIN';
  }
};
```

### **5.2. User Service**

Tạo file: `services/user.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse, PageResponse, AccountRole, AccountStatus } from '@/types/common';
import { AdminUserResponse, AdminUserDetailResponse, CreateUserRequest, UpdateUserRequest } from '@/types/user';

export interface GetUsersParams {
  page?: number;
  size?: number;
  search?: string;
  role?: AccountRole;
  status?: AccountStatus;
  sortBy?: string;
  sortDir?: 'ASC' | 'DESC';
}

export const userService = {
  getUsers: async (params: GetUsersParams = {}) => {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });
    
    return apiClient.get<ApiResponse<PageResponse<AdminUserResponse>>>(
      `/api/admin/users?${queryParams.toString()}`
    );
  },

  getUserDetail: async (id: number) => {
    return apiClient.get<ApiResponse<AdminUserDetailResponse>>(`/api/admin/users/${id}`);
  },

  createUser: async (data: CreateUserRequest) => {
    return apiClient.post<ApiResponse<AdminUserResponse>>('/api/admin/users', data);
  },

  updateUser: async (id: number, data: UpdateUserRequest) => {
    return apiClient.put<ApiResponse<AdminUserResponse>>(`/api/admin/users/${id}`, data);
  },

  changeStatus: async (id: number, status: AccountStatus) => {
    return apiClient.patch<ApiResponse<AdminUserResponse>>(
      `/api/admin/users/${id}/status`,
      { status }
    );
  },

  deleteUser: async (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/users/${id}`);
  }
};
```

### **5.3. Hospital Service**

Tạo file: `services/hospital.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse, PageResponse, HospitalStatus } from '@/types/common';
import { Hospital, CreateHospitalRequest, CreateSpecialtyRequest } from '@/types/hospital';

export interface GetHospitalsParams {
  page?: number;
  size?: number;
  search?: string;
  status?: HospitalStatus;
}

export const hospitalService = {
  getHospitals: async (params: GetHospitalsParams = {}) => {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });
    
    return apiClient.get<ApiResponse<PageResponse<Hospital>>>(
      `/api/admin/hospitals?${queryParams.toString()}`
    );
  },

  getHospitalById: async (id: number) => {
    return apiClient.get<ApiResponse<Hospital>>(`/api/admin/hospitals/${id}`);
  },

  createHospital: async (data: CreateHospitalRequest) => {
    return apiClient.post<ApiResponse<Hospital>>('/api/admin/hospitals', data);
  },

  updateHospital: async (id: number, data: CreateHospitalRequest) => {
    return apiClient.put<ApiResponse<Hospital>>(`/api/admin/hospitals/${id}`, data);
  },

  updateStatus: async (id: number, status: HospitalStatus) => {
    return apiClient.patch<ApiResponse<Hospital>>(
      `/api/admin/hospitals/${id}/status`,
      null,
      { params: { status } }
    );
  },

  deleteHospital: async (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/hospitals/${id}`);
  },

  addSpecialty: async (hospitalId: number, data: CreateSpecialtyRequest) => {
    return apiClient.post<ApiResponse<void>>(
      `/api/admin/hospitals/${hospitalId}/specialties`,
      data
    );
  },

  deleteSpecialty: async (hospitalId: number, specialtyId: number) => {
    return apiClient.delete<ApiResponse<void>>(
      `/api/admin/hospitals/${hospitalId}/specialties/${specialtyId}`
    );
  }
};
```

### **5.4. Nutrition Service**

Tạo file: `services/nutrition.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse, PageResponse, FoodItemStatus } from '@/types/common';
import { FoodItem, CreateFoodItemRequest, FoodType, Nutrient } from '@/types/nutrition';

export interface GetFoodItemsParams {
  page?: number;
  size?: number;
  search?: string;
  status?: FoodItemStatus;
  foodTypeId?: number;
}

export const nutritionService = {
  // Food Items
  getFoodItems: async (params: GetFoodItemsParams = {}) => {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });
    
    return apiClient.get<ApiResponse<PageResponse<FoodItem>>>(
      `/api/admin/nutrition/food-items?${queryParams.toString()}`
    );
  },

  createFoodItem: async (data: CreateFoodItemRequest) => {
    return apiClient.post<ApiResponse<FoodItem>>('/api/admin/nutrition/food-items', data);
  },

  updateFoodItem: async (id: number, data: CreateFoodItemRequest) => {
    return apiClient.put<ApiResponse<FoodItem>>(`/api/admin/nutrition/food-items/${id}`, data);
  },

  deleteFoodItem: async (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/nutrition/food-items/${id}`);
  },

  // Food Types
  getFoodTypes: async () => {
    return apiClient.get<ApiResponse<FoodType[]>>('/api/admin/nutrition/food-types');
  },

  createFoodType: async (name: string) => {
    return apiClient.post<ApiResponse<FoodType>>('/api/admin/nutrition/food-types', { name });
  },

  deleteFoodType: async (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/nutrition/food-types/${id}`);
  },

  // Nutrients
  getNutrients: async () => {
    return apiClient.get<ApiResponse<Nutrient[]>>('/api/admin/nutrition/nutrients');
  },

  createNutrient: async (data: { name: string; unit: string }) => {
    return apiClient.post<ApiResponse<Nutrient>>('/api/admin/nutrition/nutrients', data);
  },

  deleteNutrient: async (id: number) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/nutrition/nutrients/${id}`);
  }
};
```

### **5.5. Revenue Service**

Tạo file: `services/revenue.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse, PageResponse, TransactionStatus } from '@/types/common';
import { Transaction, RevenueStats } from '@/types/revenue';

export interface GetTransactionsParams {
  page?: number;
  size?: number;
  status?: TransactionStatus;
  userId?: number;
  servicePlanId?: number;
  startDate?: string; // ISO format
  endDate?: string;   // ISO format
}

export interface GetStatisticsParams {
  startDate?: string;
  endDate?: string;
}

export const revenueService = {
  getTransactions: async (params: GetTransactionsParams = {}) => {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });
    
    return apiClient.get<ApiResponse<PageResponse<Transaction>>>(
      `/api/admin/revenue/transactions?${queryParams.toString()}`
    );
  },

  getTransactionById: async (id: number) => {
    return apiClient.get<ApiResponse<Transaction>>(`/api/admin/revenue/transactions/${id}`);
  },

  getStatistics: async (params: GetStatisticsParams = {}) => {
    const queryParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });
    
    return apiClient.get<ApiResponse<RevenueStats>>(
      `/api/admin/revenue/statistics?${queryParams.toString()}`
    );
  },

  exportTransactions: async (format: 'CSV' | 'EXCEL' | 'PDF', params: GetStatisticsParams = {}) => {
    const queryParams = new URLSearchParams({ format });
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        queryParams.append(key, value.toString());
      }
    });

    // Download file directly
    const url = `${process.env.NEXT_PUBLIC_API_URL}/api/admin/revenue/export?${queryParams.toString()}`;
    const token = localStorage.getItem('accessToken');
    
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = `transactions_${Date.now()}.${format.toLowerCase()}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
  }
};
```

### **5.6. Settings Service**

Tạo file: `services/settings.service.ts`

```typescript
import { apiClient } from '@/lib/api-client';
import { ApiResponse } from '@/types/common';
import { SystemSetting, UpdateSettingRequest } from '@/types/settings';

export const settingsService = {
  getAllSettings: async () => {
    return apiClient.get<ApiResponse<SystemSetting[]>>('/api/admin/settings');
  },

  getSettingByKey: async (key: string) => {
    return apiClient.get<ApiResponse<SystemSetting>>(`/api/admin/settings/${key}`);
  },

  createOrUpdateSetting: async (key: string, data: UpdateSettingRequest) => {
    return apiClient.put<ApiResponse<SystemSetting>>(`/api/admin/settings/${key}`, data);
  },

  deleteSetting: async (key: string) => {
    return apiClient.delete<ApiResponse<void>>(`/api/admin/settings/${key}`);
  }
};
```

---

## 📝 **6. USAGE EXAMPLES**

### **6.1. Login Component Example**

```tsx
'use client';

import { useState } from 'react';
import { authService } from '@/services/auth.service';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login({ email, password });
      
      // Store token and user info
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('user', JSON.stringify(response.data.account));
      
      // Redirect to dashboard
      router.push('/admin/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
}
```

### **6.2. User List Component Example**

```tsx
'use client';

import { useState, useEffect } from 'react';
import { userService, GetUsersParams } from '@/services/user.service';
import { AdminUserResponse } from '@/types/user';
import { AccountRole, AccountStatus } from '@/types/common';

export default function UsersPage() {
  const [users, setUsers] = useState<AdminUserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState<GetUsersParams>({
    page: 0,
    size: 20,
    sortBy: 'id',
    sortDir: 'DESC'
  });

  useEffect(() => {
    loadUsers();
  }, [filters]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const response = await userService.getUsers(filters);
      setUsers(response.data.content);
      setTotalPages(response.data.pageable.totalPages);
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (search: string) => {
    setFilters({ ...filters, search, page: 0 });
  };

  const handleFilterRole = (role: AccountRole | undefined) => {
    setFilters({ ...filters, role, page: 0 });
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this user?')) {
      try {
        await userService.deleteUser(id);
        loadUsers(); // Reload list
      } catch (error) {
        console.error('Failed to delete user:', error);
      }
    }
  };

  return (
    <div>
      <h1>User Management</h1>
      
      {/* Filters */}
      <div>
        <input
          type="text"
          placeholder="Search by email or name..."
          onChange={(e) => handleSearch(e.target.value)}
        />
        <select onChange={(e) => handleFilterRole(e.target.value as AccountRole || undefined)}>
          <option value="">All Roles</option>
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
          <option value="DOCTOR">Doctor</option>
          <option value="NUTRITIONIST">Nutritionist</option>
        </select>
      </div>

      {/* Table */}
      {loading ? (
        <p>Loading...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Email</th>
              <th>Full Name</th>
              <th>Role</th>
              <th>Status</th>
              <th>Transactions</th>
              <th>Total Spent</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.email}</td>
                <td>{user.fullname}</td>
                <td>{user.role}</td>
                <td>{user.status}</td>
                <td>{user.transactionCount}</td>
                <td>${user.totalSpent}</td>
                <td>
                  <button onClick={() => handleDelete(user.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Pagination */}
      <div>
        <button
          disabled={filters.page === 0}
          onClick={() => setFilters({ ...filters, page: filters.page! - 1 })}
        >
          Previous
        </button>
        <span>Page {(filters.page || 0) + 1} of {totalPages}</span>
        <button
          disabled={filters.page! >= totalPages - 1}
          onClick={() => setFilters({ ...filters, page: filters.page! + 1 })}
        >
          Next
        </button>
      </div>
    </div>
  );
}
```

### **6.3. Revenue Dashboard Example**

```tsx
'use client';

import { useState, useEffect } from 'react';
import { revenueService } from '@/services/revenue.service';
import { RevenueStats } from '@/types/revenue';

export default function RevenueDashboard() {
  const [stats, setStats] = useState<RevenueStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const response = await revenueService.getStatistics();
      setStats(response.data);
    } catch (error) {
      console.error('Failed to load stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (format: 'CSV' | 'EXCEL' | 'PDF') => {
    try {
      await revenueService.exportTransactions(format);
    } catch (error) {
      console.error('Failed to export:', error);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (!stats) return <p>No data available</p>;

  return (
    <div>
      <h1>Revenue Dashboard</h1>
      
      {/* Summary Cards */}
      <div className="grid grid-cols-4 gap-4">
        <div className="card">
          <h3>Total Revenue</h3>
          <p className="text-2xl">${stats.totalRevenue.toFixed(2)}</p>
        </div>
        <div className="card">
          <h3>Total Transactions</h3>
          <p className="text-2xl">{stats.totalTransactions}</p>
        </div>
        <div className="card">
          <h3>Completed</h3>
          <p className="text-2xl">{stats.completedTransactions}</p>
        </div>
        <div className="card">
          <h3>Average Value</h3>
          <p className="text-2xl">${stats.averageTransactionValue.toFixed(2)}</p>
        </div>
      </div>

      {/* Monthly Revenue Chart */}
      {stats.revenueByMonth && (
        <div className="mt-8">
          <h2>Revenue by Month</h2>
          {/* Add your chart component here */}
          <ul>
            {stats.revenueByMonth.map((item) => (
              <li key={item.month}>
                {item.month}: ${item.revenue.toFixed(2)} ({item.transactionCount} transactions)
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Export Buttons */}
      <div className="mt-8">
        <button onClick={() => handleExport('CSV')}>Export CSV</button>
        <button onClick={() => handleExport('EXCEL')}>Export Excel</button>
        <button onClick={() => handleExport('PDF')}>Export PDF</button>
      </div>
    </div>
  );
}
```

---

## 🛡️ **7. PROTECTED ROUTES**

### **7.1. Auth Middleware**

Tạo file: `middleware.ts` (Next.js 13+ App Router)

```typescript
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('accessToken')?.value;
  
  // Protected admin routes
  if (request.nextUrl.pathname.startsWith('/admin')) {
    if (!token) {
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }
  
  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*']
};
```

### **7.2. Auth Context Provider**

Tạo file: `context/AuthContext.tsx`

```tsx
'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService } from '@/services/auth.service';

interface User {
  id: number;
  email: string;
  fullname: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  isAuthenticated: boolean;
  isAdmin: boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    setLoading(false);
  }, []);

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: !!user,
        isAdmin: user?.role === 'ADMIN',
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
```

---

## 📋 **8. CORS CONFIGURATION**

Backend đã được cấu hình CORS cho:
- `http://localhost:3000` ✅
- `http://localhost:3001` ✅

Nếu cần thêm origins khác, update trong `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "http://localhost:3001",
    "https://yourdomain.com" // Add your production URL
));
```

---

## 🎯 **9. COMPLETE API ENDPOINTS REFERENCE**

### **Authentication**
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/google
GET    /api/auth/health
```

### **Admin - User Management**
```
GET    /api/admin/users
GET    /api/admin/users/{id}
POST   /api/admin/users
PUT    /api/admin/users/{id}
PATCH  /api/admin/users/{id}/status
DELETE /api/admin/users/{id}
```

### **Admin - Hospital Management**
```
GET    /api/admin/hospitals
GET    /api/admin/hospitals/{id}
POST   /api/admin/hospitals
PUT    /api/admin/hospitals/{id}
PATCH  /api/admin/hospitals/{id}/status
DELETE /api/admin/hospitals/{id}
POST   /api/admin/hospitals/{hospitalId}/specialties
DELETE /api/admin/hospitals/{hospitalId}/specialties/{specialtyId}
```

### **Admin - Nutrition Management**
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

### **Admin - Revenue & Transactions**
```
GET /api/admin/revenue/transactions
GET /api/admin/revenue/transactions/{id}
GET /api/admin/revenue/statistics
GET /api/admin/revenue/export
```

### **Admin - System Settings**
```
GET    /api/admin/settings
GET    /api/admin/settings/{key}
PUT    /api/admin/settings/{key}
DELETE /api/admin/settings/{key}
```

---

## 📦 **10. PACKAGE.JSON DEPENDENCIES**

```json
{
  "dependencies": {
    "next": "^14.0.0",
    "react": "^18.0.0",
    "react-dom": "^18.0.0",
    "axios": "^1.6.0",
    "typescript": "^5.0.0"
  },
  "devDependencies": {
    "@types/node": "^20.0.0",
    "@types/react": "^18.0.0",
    "@types/react-dom": "^18.0.0"
  }
}
```

**Optional UI Libraries:**
```json
{
  "dependencies": {
    "recharts": "^2.10.0",        // For charts
    "react-query": "^3.39.0",     // For data fetching
    "zustand": "^4.4.0",          // State management
    "tailwindcss": "^3.4.0",      // Styling
    "shadcn-ui": "latest"         // UI components
  }
}
```

---

## 🚀 **11. QUICK START CHECKLIST**

### **Backend Setup:**
- ✅ Backend running on `http://localhost:8080`
- ✅ MySQL database connected
- ✅ Swagger UI accessible
- ✅ Admin account ready (`admin@lanhcare.com / password123`)

### **Next.js Setup:**
1. ✅ Create Next.js project: `npx create-next-app@latest`
2. ✅ Install axios: `npm install axios`
3. ✅ Copy all TypeScript types from this guide
4. ✅ Copy all service files
5. ✅ Create `.env.local` with API URL
6. ✅ Implement authentication flow
7. ✅ Build admin dashboard pages
8. ✅ Test API Integration

---

## 📚 **12. ADDITIONAL RESOURCES**

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **API Documentation:** `API-DOCUMENTATION.md`
- **Admin API Design:** `ADMIN-API-DESIGN.md`
- **Backend Completion:** `ADMIN-MODULE-COMPLETE.md`

---

## 💡 **13. BEST PRACTICES**

1. **Error Handling:**
   - Always wrap API calls in try-catch
   - Show user-friendly error messages
   - Log errors for debugging

2. **Loading States:**
   - Show loading indicators during API calls
   - Disable buttons during submission
   - Provide feedback on success/failure

3. **Security:**
   - Never store sensitive data in localStorage (only tokens)
   - Clear tokens on logout
   - Implement token refresh if needed
   - Validate user permissions on frontend

4. **Performance:**
   - Use pagination for large datasets
   - Implement debouncing for search
   - Cache frequently used data
   - Use React Query for data management

5. **UX:**
   - Provide clear success/error messages
   - Implement confirmation dialogs for destructive actions
   - Use optimistic updates where appropriate
   - Add keyboard shortcuts for power users

---

## 🎉 **YOU'RE ALL SET!**

Với guide này, anh có:
- ✅ Complete TypeScript types
- ✅ All API service functions
- ✅ Authentication setup
- ✅ Example components
- ✅ Protected routes
- ✅ Best practices

**Happy coding! 🚀**
