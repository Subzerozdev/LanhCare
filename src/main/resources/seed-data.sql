-- ============================================
-- LANHCARE DATABASE SEED DATA (Vietnamese)
-- Generated: 2026-01-18
-- ============================================
-- This script will:
-- 1. Truncate all tables (with CASCADE)
-- 2. Reset all sequences
-- 3. Insert fresh sample data in Vietnamese
-- ============================================

-- ============================================
-- TRUNCATE ALL TABLES (CASCADE handles FK)
-- ============================================
-- TRUNCATE is faster than DELETE and automatically handles FK constraints with CASCADE
-- It also resets sequences automatically with RESTART IDENTITY

TRUNCATE TABLE 
    comment_media,
    comment,
    post_media,
    post,
    meal_food,
    meal_log,
    exercise_log,
    daily_log,
    food_nutrient,
    food_item,
    food_type,
    nutrient,
    dietary_restriction,
    user_health_profile,
    medical_specialty,
    hospital,
    icd11_translation,
    icd11_code,
    icd11_chapter,
    transaction,
    service_plan,
    exercise_type,
    fcmtoken,
    account
RESTART IDENTITY CASCADE;

-- ============================================
-- RESET SEQUENCES
-- ============================================
ALTER SEQUENCE IF EXISTS account_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS user_health_profile_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS daily_log_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS meal_log_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS meal_food_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS food_item_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS food_type_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS nutrient_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS food_nutrient_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS exercise_log_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS exercise_type_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS hospital_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS medical_specialty_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS service_plan_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS transaction_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS post_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS post_media_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS comment_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS comment_media_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS dietary_restriction_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS fcmtoken_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS icd11_translation_id_seq RESTART WITH 1;

-- ============================================
-- INSERT DATA
-- ============================================

-- ============================================
-- 1. ACCOUNT (password: password123 - BCrypt encoded)
-- ============================================
INSERT INTO account (id, email, fullname, password, role, status) VALUES
(1, 'admin@lanhcare.com', 'Quản Trị Viên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'ACTIVE'),
(2, 'staff@lanhcare.com', 'Nhân Viên Nguyễn Văn An', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STAFF', 'ACTIVE'),
(3, 'user1@gmail.com', 'Trần Thị Bích Ngọc', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'),
(4, 'user2@gmail.com', 'Lê Hoàng Minh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'),
(5, 'user3@gmail.com', 'Phạm Thị Hương', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'),
(6, 'user4@gmail.com', 'Võ Đình Khoa', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'INACTIVE'),
(7, 'user5@gmail.com', 'Nguyễn Thị Mai Anh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'),
(8, 'user6@gmail.com', 'Đặng Văn Hùng', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE');

SELECT setval('account_id_seq', 8, true);

-- ============================================
-- 2. USER HEALTH PROFILE
-- ============================================
INSERT INTO user_health_profile (id, account_id, date_of_birth, gender, height_cm, weight_kg, activity_level, bmi_value, bmi_status, tdee_value, health_goals, created_at, updated_at) VALUES
(1, 3, '1995-05-15', 'FEMALE', 160.00, 55.00, 'NORMAL_EXERCISE', 21.48, 'NORMAL', 1860.00, 'MAINTAIN', NOW(), NOW()),
(2, 4, '1990-08-22', 'MALE', 175.00, 78.00, 'LIGHT_EXERCISE', 25.47, 'OBESE_I', 2300.00, 'LOSE_WEIGHT', NOW(), NOW()),
(3, 5, '1988-12-03', 'FEMALE', 158.00, 52.00, 'NO_EXERCISE', 20.83, 'NORMAL', 1500.00, 'MAINTAIN', NOW(), NOW()),
(4, 7, '1998-03-10', 'FEMALE', 165.00, 58.00, 'HIGH_EXERCISE', 21.30, 'NORMAL', 2100.00, 'EXTREME_GAIN', NOW(), NOW()),
(5, 8, '1985-11-28', 'MALE', 172.00, 85.00, 'LIGHT_EXERCISE', 28.73, 'OBESE_I', 2450.00, 'LOSE_WEIGHT', NOW(), NOW());

SELECT setval('user_health_profile_id_seq', 5, true);

-- ============================================
-- 3. FOOD TYPE (Loại thực phẩm)
-- ============================================
INSERT INTO food_type (id, name, is_deleted) VALUES
(1, 'Ngũ cốc và tinh bột', false),
(2, 'Rau củ quả', false),
(3, 'Thịt và hải sản', false),
(4, 'Sữa và các sản phẩm từ sữa', false),
(5, 'Trái cây', false),
(6, 'Đồ uống', false),
(7, 'Đồ ăn nhanh', false),
(8, 'Món chay', false);

SELECT setval('food_type_id_seq', 8, true);

-- ============================================
-- 4. NUTRIENT (Chất dinh dưỡng)
-- ============================================
INSERT INTO nutrient (id, name, unit, is_deleted) VALUES
(1, 'Protein', 'g', false),
(2, 'Carbohydrate', 'g', false),
(3, 'Chất béo', 'g', false),
(4, 'Chất xơ', 'g', false),
(5, 'Vitamin A', 'mcg', false),
(6, 'Vitamin C', 'mg', false),
(7, 'Vitamin D', 'mcg', false),
(8, 'Canxi', 'mg', false),
(9, 'Sắt', 'mg', false),
(10, 'Kali', 'mg', false),
(11, 'Natri', 'mg', false),
(12, 'Đường', 'g', false);

SELECT setval('nutrient_id_seq', 12, true);

-- ============================================
-- 5. FOOD ITEM (Thực phẩm)
-- ============================================
INSERT INTO food_item (id, food_type_id, name, description, calo, serving_unit, standard_serving_size, status, data_source, image_url) VALUES
-- Ngũ cốc và tinh bột
(1, 1, 'Cơm trắng', 'Cơm gạo trắng thông thường, nguồn carbohydrate chính trong bữa ăn Việt Nam', 130.00, 'chén', 150.00, 'ACTIVE', 'Bộ Y tế Việt Nam', 'https://example.com/com-trang.jpg'),
(2, 1, 'Phở bò', 'Phở bò truyền thống với nước dùng xương bò, bánh phở và thịt bò', 450.00, 'tô', 500.00, 'ACTIVE', 'LanhCare', 'https://example.com/pho-bo.jpg'),
(3, 1, 'Bánh mì thịt', 'Bánh mì Việt Nam với thịt nguội, pate, rau sống', 350.00, 'ổ', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/banh-mi.jpg'),
(4, 1, 'Bún chả', 'Bún với chả thịt nướng và nước mắm chua ngọt', 550.00, 'suất', 400.00, 'ACTIVE', 'LanhCare', 'https://example.com/bun-cha.jpg'),

-- Rau củ quả
(5, 2, 'Rau muống xào tỏi', 'Rau muống xào với tỏi, dầu ăn', 85.00, 'đĩa', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/rau-muong.jpg'),
(6, 2, 'Canh chua cá', 'Canh chua nấu với cá, cà chua, dứa, đậu bắp', 180.00, 'tô', 300.00, 'ACTIVE', 'LanhCare', 'https://example.com/canh-chua.jpg'),
(7, 2, 'Salad rau trộn', 'Salad rau xanh trộn sốt dầu giấm', 120.00, 'đĩa', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/salad.jpg'),

-- Thịt và hải sản
(8, 3, 'Gà kho gừng', 'Gà kho với gừng, nước mắm, đường', 280.00, 'đĩa', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/ga-kho.jpg'),
(9, 3, 'Thịt kho tàu', 'Thịt ba chỉ kho với trứng, nước dừa', 450.00, 'đĩa', 250.00, 'ACTIVE', 'LanhCare', 'https://example.com/thit-kho.jpg'),
(10, 3, 'Cá hồi nướng', 'Cá hồi nướng với rau củ, giàu omega-3', 320.00, 'phần', 180.00, 'ACTIVE', 'LanhCare', 'https://example.com/ca-hoi.jpg'),
(11, 3, 'Tôm rang muối', 'Tôm rang muối giòn, món khai vị phổ biến', 220.00, 'đĩa', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/tom-rang.jpg'),

-- Sữa và sản phẩm từ sữa
(12, 4, 'Sữa tươi không đường', 'Sữa tươi nguyên chất, không thêm đường', 65.00, 'ly', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/sua-tuoi.jpg'),
(13, 4, 'Sữa chua Vinamilk', 'Sữa chua có đường, tốt cho hệ tiêu hóa', 100.00, 'hộp', 100.00, 'ACTIVE', 'Vinamilk', 'https://example.com/sua-chua.jpg'),
(14, 4, 'Phô mai con bò cười', 'Phô mai chế biến, giàu canxi', 45.00, 'miếng', 17.50, 'ACTIVE', 'LanhCare', 'https://example.com/pho-mai.jpg'),

-- Trái cây
(15, 5, 'Chuối', 'Chuối chín, giàu kali và vitamin B6', 90.00, 'quả', 120.00, 'ACTIVE', 'USDA', 'https://example.com/chuoi.jpg'),
(16, 5, 'Táo', 'Táo tươi, giàu chất xơ và vitamin C', 72.00, 'quả', 150.00, 'ACTIVE', 'USDA', 'https://example.com/tao.jpg'),
(17, 5, 'Cam', 'Cam tươi, nguồn vitamin C dồi dào', 62.00, 'quả', 130.00, 'ACTIVE', 'USDA', 'https://example.com/cam.jpg'),
(18, 5, 'Xoài', 'Xoài chín, ngọt và thơm', 150.00, 'quả', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/xoai.jpg'),

-- Đồ uống
(19, 6, 'Trà xanh', 'Trà xanh không đường, chống oxy hóa', 2.00, 'ly', 250.00, 'ACTIVE', 'LanhCare', 'https://example.com/tra-xanh.jpg'),
(20, 6, 'Cà phê sữa đá', 'Cà phê phin pha sữa đặc, đá', 180.00, 'ly', 300.00, 'ACTIVE', 'LanhCare', 'https://example.com/cafe-sua.jpg'),
(21, 6, 'Nước ép cam', 'Nước cam tươi ép, không đường', 112.00, 'ly', 250.00, 'ACTIVE', 'LanhCare', 'https://example.com/nuoc-cam.jpg'),

-- Đồ ăn nhanh
(22, 7, 'Hamburger bò', 'Hamburger với patty bò, rau và sốt', 540.00, 'cái', 180.00, 'ACTIVE', 'LanhCare', 'https://example.com/hamburger.jpg'),
(23, 7, 'Pizza Margherita', 'Pizza phô mai với sốt cà chua, húng quế', 266.00, 'miếng', 107.00, 'ACTIVE', 'LanhCare', 'https://example.com/pizza.jpg'),
(24, 7, 'Gà rán', 'Gà rán giòn kiểu Mỹ', 320.00, 'miếng', 150.00, 'ACTIVE', 'LanhCare', 'https://example.com/ga-ran.jpg'),

-- Món chay
(25, 8, 'Đậu hũ chiên sả ớt', 'Đậu hũ chiên giòn, xào sả ớt', 180.00, 'đĩa', 200.00, 'ACTIVE', 'LanhCare', 'https://example.com/dau-hu.jpg'),
(26, 8, 'Chả giò chay', 'Chả giò nhân rau củ, giòn tan', 150.00, 'cuốn', 50.00, 'ACTIVE', 'LanhCare', 'https://example.com/cha-gio-chay.jpg');

SELECT setval('food_item_id_seq', 26, true);

-- ============================================
-- 6. FOOD NUTRIENT (Thành phần dinh dưỡng)
-- ============================================
INSERT INTO food_nutrient (id, food_item_id, nutrient_id, value) VALUES
-- Cơm trắng
(1, 1, 1, 2.70),   -- Protein
(2, 1, 2, 28.00),  -- Carbohydrate
(3, 1, 3, 0.30),   -- Chất béo
(4, 1, 4, 0.40),   -- Chất xơ
-- Phở bò
(5, 2, 1, 25.00),  -- Protein
(6, 2, 2, 45.00),  -- Carbohydrate
(7, 2, 3, 10.00),  -- Chất béo
(8, 2, 11, 1200.00), -- Natri
-- Chuối
(9, 15, 1, 1.10),  -- Protein
(10, 15, 2, 23.00), -- Carbohydrate
(11, 15, 10, 358.00), -- Kali
(12, 15, 12, 12.00); -- Đường

SELECT setval('food_nutrient_id_seq', 12, true);

-- ============================================
-- 7. EXERCISE TYPE (Loại bài tập)
-- ============================================
INSERT INTO exercise_type (id, activity, examples, met_value, is_deleted) VALUES
(1, 'Đi bộ nhẹ nhàng', 'Đi bộ quanh nhà, trong công viên với tốc độ chậm', 2.5, false),
(2, 'Đi bộ nhanh', 'Đi bộ tốc độ 5-6 km/giờ', 4.3, false),
(3, 'Chạy bộ', 'Chạy bộ tốc độ trung bình 8 km/giờ', 8.3, false),
(4, 'Chạy bộ nhanh', 'Chạy bộ tốc độ cao 10-12 km/giờ', 11.5, false),
(5, 'Đạp xe', 'Đạp xe đạp tốc độ vừa phải', 7.5, false),
(6, 'Bơi lội', 'Bơi tự do tốc độ vừa', 7.0, false),
(7, 'Yoga', 'Tập yoga cơ bản, thiền định', 2.5, false),
(8, 'Tập gym - Cardio', 'Chạy máy treadmill, xe đạp tập', 6.0, false),
(9, 'Tập gym - Tạ', 'Tập tạ, máy tập sức mạnh', 5.0, false),
(10, 'Nhảy dây', 'Nhảy dây cường độ trung bình', 11.0, false),
(11, 'Cầu lông', 'Chơi cầu lông giải trí', 5.5, false),
(12, 'Bóng đá', 'Đá bóng với cường độ trung bình', 7.0, false),
(13, 'Tennis', 'Chơi tennis đơn', 7.3, false),
(14, 'Aerobic', 'Tập aerobic cường độ cao', 7.5, false),
(15, 'Đấm bốc', 'Tập boxing, kickboxing', 9.0, false);

SELECT setval('exercise_type_id_seq', 15, true);

-- ============================================
-- 8. DAILY LOG (Nhật ký hàng ngày)
-- ============================================
INSERT INTO daily_log (id, account_id, meal_date, step_amount, total_calories_in, total_calories_out) VALUES
(1, 3, '2026-01-15', 8500, 1650.00, 1920.00),
(2, 3, '2026-01-16', 10200, 1580.00, 2050.00),
(3, 3, '2026-01-17', 7800, 1720.00, 1850.00),
(4, 4, '2026-01-15', 5200, 2100.00, 2450.00),
(5, 4, '2026-01-16', 6100, 1950.00, 2380.00),
(6, 5, '2026-01-17', 3200, 1420.00, 1550.00),
(7, 7, '2026-01-17', 12500, 2200.00, 2450.00);

SELECT setval('daily_log_id_seq', 7, true);

-- ============================================
-- 9. MEAL LOG (Nhật ký bữa ăn)
-- ============================================
INSERT INTO meal_log (id, account_id, meal_date, meal_type, logged_time, total_calories, food_item_id, notes, created_at) VALUES
-- User 3 - Day 1 (2026-01-15)
(1, 3, '2026-01-15', 'BREAKFAST', '07:30:00', 350.00, 3, 'Ăn sáng với bánh mì thịt và cà phê', NOW()),
(2, 3, '2026-01-15', 'LUNCH', '12:00:00', 650.00, 1, 'Cơm văn phòng: cơm, gà kho, rau muống', NOW()),
(3, 3, '2026-01-15', 'DINNER', '19:00:00', 500.00, 2, 'Phở bò tái', NOW()),
(4, 3, '2026-01-15', 'SNACK', '15:30:00', 150.00, 15, 'Trái cây và sữa chua', NOW()),
-- User 3 - Day 2 (2026-01-16)
(5, 3, '2026-01-16', 'BREAKFAST', '07:00:00', 280.00, 19, 'Xôi gà và trà xanh', NOW()),
(6, 3, '2026-01-16', 'LUNCH', '12:30:00', 600.00, 4, 'Bún chả Hà Nội', NOW()),
(7, 3, '2026-01-16', 'DINNER', '18:30:00', 550.00, 10, 'Cơm với cá hồi nướng và salad', NOW()),
-- User 4 - Day 1 (2026-01-15)
(8, 4, '2026-01-15', 'BREAKFAST', '08:00:00', 450.00, 3, 'Ăn sáng đầy đủ: trứng, bánh mì, sữa', NOW()),
(9, 4, '2026-01-15', 'LUNCH', '12:00:00', 850.00, 9, 'Cơm với thịt kho tàu, canh chua', NOW()),
(10, 4, '2026-01-15', 'DINNER', '19:30:00', 650.00, 2, 'Bún bò Huế', NOW()),
(11, 4, '2026-01-15', 'SNACK', '16:00:00', 150.00, 16, 'Bánh ngọt và trà', NOW());

SELECT setval('meal_log_id_seq', 11, true);

-- ============================================
-- 10. MEAL FOOD (Chi tiết thực phẩm trong bữa ăn)
-- ============================================
-- NOTE: meal_log already has food_item_id, so meal_food table may not be needed
-- Uncomment if your database has this table with these columns:
-- INSERT INTO meal_food (id, meal_log_id, food_item_id, quantity, calories) VALUES
-- (1, 1, 3, 1, 350.00),
-- (2, 2, 1, 1, 130.00);

-- SELECT setval('meal_food_id_seq', 2, true);

-- ============================================
-- 11. EXERCISE LOG (Nhật ký tập luyện)
-- ============================================
-- NOTE: Database has BOTH daily_log_id AND daily_log_entry_id as NOT NULL
INSERT INTO exercise_log (id, daily_log_id, daily_log_entry_id, exercise_type_id, duration_minute, calories_out) VALUES
-- User 3 - Day 1
(1, 1, 1, 2, 30.00, 120.00),   -- Đi bộ nhanh 30 phút
(2, 1, 1, 7, 45.00, 90.00),    -- Yoga 45 phút
-- User 3 - Day 2
(3, 2, 2, 3, 25.00, 200.00),   -- Chạy bộ 25 phút
(4, 2, 2, 9, 40.00, 180.00),   -- Tập gym - Tạ 40 phút
-- User 3 - Day 3
(5, 3, 3, 8, 30.00, 150.00),   -- Gym Cardio 30 phút
-- User 4 - Day 1
(6, 4, 4, 3, 20.00, 180.00),   -- Chạy bộ 20 phút
-- User 4 - Day 2
(7, 5, 5, 6, 45.00, 280.00),   -- Bơi lội 45 phút
-- User 7 - Day 1
(8, 7, 7, 4, 35.00, 380.00),   -- Chạy bộ nhanh 35 phút
(9, 7, 7, 9, 60.00, 270.00);   -- Tập gym - Tạ 60 phút

SELECT setval('exercise_log_id_seq', 9, true);

-- ============================================
-- 12. SERVICE PLAN (Gói dịch vụ)
-- ============================================
INSERT INTO service_plan (id, name, description, price, period_value, period_unit, status) VALUES
(1, 'Gói Cơ Bản', 'Theo dõi calo và bữa ăn cơ bản. Phù hợp cho người mới bắt đầu quản lý sức khỏe.', 0.00, 0, 'MONTH', 'ACTIVE'),
(2, 'Gói Premium Tháng', 'Truy cập đầy đủ tính năng: AI tư vấn dinh dưỡng, lập kế hoạch tập luyện, theo dõi chi tiết.', 99000.00, 1, 'MONTH', 'ACTIVE'),
(3, 'Gói Premium Quý', 'Tiết kiệm 20% - Đầy đủ tính năng Premium trong 3 tháng.', 239000.00, 3, 'MONTH', 'ACTIVE'),
(4, 'Gói Premium Năm', 'Tiết kiệm 40% - Đầy đủ tính năng Premium trong 12 tháng. Ưu đãi tốt nhất!', 699000.00, 12, 'MONTH', 'ACTIVE'),
(5, 'Gói Gia Đình', 'Tối đa 5 thành viên, chia sẻ tính năng Premium cho cả gia đình.', 149000.00, 1, 'MONTH', 'ACTIVE'),
(6, 'Gói Doanh Nghiệp', 'Dành cho công ty, tối đa 50 nhân viên với báo cáo sức khỏe tổng hợp.', 2990000.00, 1, 'MONTH', 'INACTIVE');

SELECT setval('service_plan_id_seq', 6, true);

-- ============================================
-- 13. TRANSACTION (Giao dịch)
-- ============================================
INSERT INTO transaction (id, account_id, service_plan_id, transaction_date, amount, payment_method, status) VALUES
(1, 3, 2, '2026-01-01 10:30:00', 99000.00, 'MOMO', 'COMPLETED'),
(2, 4, 3, '2026-01-05 14:20:00', 239000.00, 'VNPAY', 'COMPLETED'),
(3, 5, 2, '2026-01-10 09:15:00', 99000.00, 'BANK_TRANSFER', 'COMPLETED'),
(4, 7, 4, '2026-01-12 16:45:00', 699000.00, 'MOMO', 'COMPLETED'),
(5, 8, 2, '2026-01-15 11:00:00', 99000.00, 'VNPAY', 'PENDING'),
(6, 3, 3, '2026-01-17 08:30:00', 239000.00, 'CREDIT_CARD', 'COMPLETED');

SELECT setval('transaction_id_seq', 6, true);

-- ============================================
-- 14. ICD11 CHAPTER (Chương bệnh ICD-11)
-- ============================================
INSERT INTO icd11_chapter (chapter_uri, vn_title, original_title_en, chapter_code, release_id, status) VALUES
('http://id.who.int/icd/release/11/2025-01/mms/1', 'Một số bệnh nhiễm trùng hoặc ký sinh trùng', 'Certain infectious or parasitic diseases', '01', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/2', 'U tân sinh', 'Neoplasms', '02', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/4', 'Bệnh của hệ miễn dịch', 'Diseases of the immune system', '04', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/5', 'Rối loạn nội tiết, dinh dưỡng hoặc chuyển hóa', 'Endocrine, nutritional or metabolic diseases', '05', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/6', 'Rối loạn tâm thần, hành vi hoặc phát triển thần kinh', 'Mental, behavioural or neurodevelopmental disorders', '06', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/11', 'Bệnh của hệ tuần hoàn', 'Diseases of the circulatory system', '11', '2025-01', 'ACTIVE'),
('http://id.who.int/icd/release/11/2025-01/mms/13', 'Bệnh của hệ tiêu hóa', 'Diseases of the digestive system', '13', '2025-01', 'ACTIVE');

-- ============================================
-- 15. ICD11 CODE (Mã bệnh ICD-11)
-- ============================================
INSERT INTO icd11_code (icd_uri, chapter_uri, icd_code, original_title_en, definition_en, class_kind, status, created_date, last_synced) VALUES
('http://id.who.int/icd/release/11/mms/5A11', 'http://id.who.int/icd/release/11/2025-01/mms/5', '5A11', 'Type 2 diabetes mellitus', 'A metabolic disorder characterized by high blood sugar levels', 'category', 'ACTIVE', NOW(), NOW()),
('http://id.who.int/icd/release/11/mms/5B80', 'http://id.who.int/icd/release/11/2025-01/mms/5', '5B80', 'Overweight or obesity', 'Excessive accumulation of body fat', 'category', 'ACTIVE', NOW(), NOW()),
('http://id.who.int/icd/release/11/mms/BA00', 'http://id.who.int/icd/release/11/2025-01/mms/11', 'BA00', 'Essential hypertension', 'High blood pressure without identifiable cause', 'category', 'ACTIVE', NOW(), NOW()),
('http://id.who.int/icd/release/11/mms/BA80', 'http://id.who.int/icd/release/11/2025-01/mms/11', 'BA80', 'Ischaemic heart disease', 'Heart disease caused by reduced blood supply', 'category', 'ACTIVE', NOW(), NOW()),
('http://id.who.int/icd/release/11/mms/DA90', 'http://id.who.int/icd/release/11/2025-01/mms/13', 'DA90', 'Gastro-oesophageal reflux disease', 'Chronic digestive disease affecting the stomach and esophagus', 'category', 'ACTIVE', NOW(), NOW());

-- ============================================
-- 16. ICD11 TRANSLATION (Bản dịch ICD-11)
-- ============================================
INSERT INTO icd11_translation (id, icd_uri, vn_title, vn_definition, status) VALUES
(1, 'http://id.who.int/icd/release/11/mms/5A11', 'Đái tháo đường tuýp 2', 'Rối loạn chuyển hóa đặc trưng bởi lượng đường trong máu cao do kháng insulin hoặc thiếu insulin tương đối', 'PUBLISHED'),
(2, 'http://id.who.int/icd/release/11/mms/5B80', 'Thừa cân hoặc béo phì', 'Sự tích tụ mỡ cơ thể quá mức, có thể ảnh hưởng đến sức khỏe', 'PUBLISHED'),
(3, 'http://id.who.int/icd/release/11/mms/BA00', 'Tăng huyết áp nguyên phát', 'Huyết áp cao không xác định được nguyên nhân cụ thể', 'PUBLISHED'),
(4, 'http://id.who.int/icd/release/11/mms/BA80', 'Bệnh tim thiếu máu cục bộ', 'Bệnh tim do giảm lưu lượng máu đến cơ tim', 'PUBLISHED'),
(5, 'http://id.who.int/icd/release/11/mms/DA90', 'Bệnh trào ngược dạ dày thực quản', 'Bệnh tiêu hóa mãn tính ảnh hưởng đến dạ dày và thực quản', 'PUBLISHED');

SELECT setval('icd11_translation_id_seq', 5, true);

-- ============================================
-- 17. HOSPITAL (Bệnh viện)
-- ============================================
INSERT INTO hospital (id, name, address, latitude, longitude, status) VALUES
(1, 'Bệnh viện Chợ Rẫy', '201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP.HCM', 10.75556300, 106.66028200, 'ACTIVE'),
(2, 'Bệnh viện Bạch Mai', '78 Giải Phóng, Phương Mai, Đống Đa, Hà Nội', 21.00136100, 105.84367500, 'ACTIVE'),
(3, 'Bệnh viện Đại học Y Dược TP.HCM', '215 Hồng Bàng, Phường 11, Quận 5, TP.HCM', 10.75792000, 106.66419000, 'ACTIVE'),
(4, 'Bệnh viện Việt Đức', '40 Tràng Thi, Hàng Bông, Hoàn Kiếm, Hà Nội', 21.02741700, 105.84805600, 'ACTIVE'),
(5, 'Bệnh viện Nhi Đồng 1', '341 Sư Vạn Hạnh, Phường 10, Quận 10, TP.HCM', 10.77472200, 106.66916700, 'ACTIVE'),
(6, 'Bệnh viện Từ Dũ', '284 Cống Quỳnh, Phường Phạm Ngũ Lão, Quận 1, TP.HCM', 10.76819400, 106.69083300, 'ACTIVE'),
(7, 'Bệnh viện E', '89 Trần Cung, Nghĩa Tân, Cầu Giấy, Hà Nội', 21.04888900, 105.79666700, 'ACTIVE');

SELECT setval('hospital_id_seq', 7, true);

-- ============================================
-- 18. MEDICAL SPECIALTY (Chuyên khoa)
-- ============================================
INSERT INTO medical_specialty (id, hospital_id, icd_uri, name_vn, name_en, status) VALUES
(1, 1, 'http://id.who.int/icd/release/11/mms/5A11', 'Nội tiết - Đái tháo đường', 'Endocrinology - Diabetes', 'ACTIVE'),
(2, 1, 'http://id.who.int/icd/release/11/mms/BA00', 'Tim mạch', 'Cardiology', 'ACTIVE'),
(3, 2, 'http://id.who.int/icd/release/11/mms/BA80', 'Tim mạch can thiệp', 'Interventional Cardiology', 'ACTIVE'),
(4, 2, 'http://id.who.int/icd/release/11/mms/DA90', 'Tiêu hóa', 'Gastroenterology', 'ACTIVE'),
(5, 3, 'http://id.who.int/icd/release/11/mms/5B80', 'Dinh dưỡng - Béo phì', 'Nutrition - Obesity', 'ACTIVE'),
(6, 4, NULL, 'Ngoại tổng quát', 'General Surgery', 'ACTIVE'),
(7, 5, NULL, 'Nhi khoa', 'Pediatrics', 'ACTIVE');

SELECT setval('medical_specialty_id_seq', 7, true);

-- ============================================
-- 19. POST (Bài viết cộng đồng)
-- ============================================
INSERT INTO post (id, account_id, content, heart, created_at, is_deleted, status, rejection_reason) VALUES
(1, 3, 'Xin chào mọi người! Mình mới bắt đầu hành trình giảm cân từ tháng trước. Sau 4 tuần kiên trì tập luyện và ăn uống lành mạnh, mình đã giảm được 3kg rồi! Cảm ơn LanhCare đã giúp mình theo dõi calo hàng ngày. 💪🥗', 45, '2026-01-10 08:30:00', false, 'APPROVED', NULL),
(2, 4, 'Chia sẻ thực đơn giảm cân trong 1 ngày của mình:\n- Sáng: Yến mạch + chuối + sữa không đường\n- Trưa: Cơm gạo lứt + ức gà áp chảo + salad\n- Tối: Canh rau + trứng luộc\n- Snack: Táo + yogurt\n\nTổng: khoảng 1500 calo. Ai thử chưa nhỉ?', 32, '2026-01-12 12:00:00', false, 'APPROVED', NULL),
(3, 5, 'Hôm nay mình chạy bộ được 5km lần đầu tiên trong đời! 🏃‍♀️ Trước đây mình chỉ chạy được 1km là đuối rồi. Kiên trì tập luyện 2 tháng cuối cùng cũng có kết quả. Cảm ơn chức năng theo dõi tập luyện của app!', 28, '2026-01-14 19:00:00', false, 'APPROVED', NULL),
(4, 7, 'Tips tập Yoga buổi sáng cho người mới bắt đầu:\n1. Bắt đầu với các động tác đơn giản\n2. Tập trung vào hơi thở\n3. Không ép bản thân quá mức\n4. Kiên trì mỗi ngày 15-20 phút\n\nMình đã tập được 3 tháng và cảm thấy cơ thể dẻo dai hơn nhiều 🧘‍♀️', 56, '2026-01-15 07:00:00', false, 'APPROVED', NULL),
(5, 8, 'Có ai biết app nào tính calo chính xác không ạ?', 5, '2026-01-16 10:00:00', false, 'PENDING', NULL),
(6, 3, 'Món ăn này thật sự rất ngon và healthy! Cá hồi nướng măng tây, chỉ khoảng 400 calo thôi mà no lâu cả buổi chiều. Recipe mình để ở comment nhé! 🐟🥦', 38, '2026-01-17 13:00:00', false, 'APPROVED', NULL);

SELECT setval('post_id_seq', 6, true);

-- ============================================
-- 20. POST MEDIA (Media của bài viết)
-- ============================================
INSERT INTO post_media (id, post_id, url, media_type, order_index) VALUES
(1, 1, 'https://res.cloudinary.com/lanhcare/image/upload/before_after_1.jpg', 'IMAGE', 1),
(2, 2, 'https://res.cloudinary.com/lanhcare/image/upload/healthy_meal_2.jpg', 'IMAGE', 1),
(3, 3, 'https://res.cloudinary.com/lanhcare/image/upload/running_5k.jpg', 'IMAGE', 1),
(4, 4, 'https://res.cloudinary.com/lanhcare/image/upload/yoga_morning.jpg', 'IMAGE', 1),
(5, 6, 'https://res.cloudinary.com/lanhcare/image/upload/salmon_recipe.jpg', 'IMAGE', 1),
(6, 6, 'https://res.cloudinary.com/lanhcare/image/upload/salmon_recipe_2.jpg', 'IMAGE', 2);

SELECT setval('post_media_id_seq', 6, true);

-- ============================================
-- 21. COMMENT (Bình luận)
-- ============================================
INSERT INTO comment (id, post_id, account_id, parent_id, content, is_deleted, status, rejection_reason, created_at) VALUES
-- Comments on Post 1
(1, 1, 4, NULL, 'Chúc mừng bạn nha! Kiên trì là chìa khóa! 💪', false, 'APPROVED', NULL, '2026-01-10 09:00:00'),
(2, 1, 5, NULL, 'Wow 3kg trong 4 tuần là tuyệt vời rồi! Bạn có thể chia sẻ thêm chế độ ăn được không?', false, 'APPROVED', NULL, '2026-01-10 09:30:00'),
(3, 1, 3, 2, 'Mình chủ yếu là cắt giảm đường và tinh bột, tăng rau xanh và protein bạn ơi!', false, 'APPROVED', NULL, '2026-01-10 10:00:00'),
-- Comments on Post 2
(4, 2, 7, NULL, 'Thực đơn này hay quá! Mình sẽ thử áp dụng. Cảm ơn bạn đã chia sẻ!', false, 'APPROVED', NULL, '2026-01-12 14:00:00'),
(5, 2, 3, NULL, 'Mình cũng đang ăn tương tự. Yến mạch buổi sáng giúp no lâu lắm nhé!', false, 'APPROVED', NULL, '2026-01-12 15:00:00'),
-- Comments on Post 3
(6, 3, 4, NULL, 'Giỏi quá! Chạy 5km không phải ai cũng làm được đâu. Tiếp tục phát huy nhé!', false, 'APPROVED', NULL, '2026-01-14 19:30:00'),
(7, 3, 7, NULL, 'Inspirational! Mình cũng đang cố gắng chạy được 3km đây 😅', false, 'APPROVED', NULL, '2026-01-14 20:00:00'),
-- Comments on Post 4
(8, 4, 3, NULL, 'Yoga buổi sáng thật sự tuyệt vời! Mình cũng tập được 1 tháng rồi.', false, 'APPROVED', NULL, '2026-01-15 08:00:00'),
(9, 4, 8, NULL, 'Bạn tập ở đâu vậy? Có video hướng dẫn không?', false, 'APPROVED', NULL, '2026-01-15 09:00:00'),
(10, 4, 7, 9, 'Mình tập theo YouTube thôi bạn. Kênh "Yoga with Adriene" rất hay nhé!', false, 'APPROVED', NULL, '2026-01-15 10:00:00'),
-- Comments on Post 6
(11, 6, 5, NULL, 'Recipe recipe recipe! 😍🐟', false, 'APPROVED', NULL, '2026-01-17 13:30:00'),
(12, 6, 3, 11, 'Recipe đây nhé:\n1. Ướp cá hồi với muối, tiêu, chanh 15 phút\n2. Nướng ở 200°C trong 15 phút\n3. Măng tây xào dầu olive 3 phút\nDone! 🎉', false, 'APPROVED', NULL, '2026-01-17 14:00:00');

SELECT setval('comment_id_seq', 12, true);

-- ============================================
-- 22. DIETARY RESTRICTION (Hạn chế ăn uống)
-- ============================================
INSERT INTO dietary_restriction (id, user_health_profile_id, nutrient_id, icd_uri, name, description, limit_type, limit_value, limit_unit, frequency, status, source_of_advice) VALUES
-- User 4 (Lê Hoàng Minh) - có nguy cơ tiểu đường
(1, 2, 12, 'http://id.who.int/icd/release/11/mms/5A11', 'Hạn chế đường', 'Giảm lượng đường tiêu thụ để kiểm soát đường huyết', 'MAX', 25.00, 'g', 'DAILY', 'ACTIVE', 'Bác sĩ Nguyễn Văn An'),
(2, 2, 11, 'http://id.who.int/icd/release/11/mms/BA00', 'Hạn chế muối', 'Giảm natri để kiểm soát huyết áp', 'MAX', 2000.00, 'mg', 'DAILY', 'ACTIVE', 'Bác sĩ Nguyễn Văn An'),
-- User 8 (Đặng Văn Hùng) - thừa cân
(3, 5, 3, 'http://id.who.int/icd/release/11/mms/5B80', 'Hạn chế chất béo', 'Giảm chất béo để hỗ trợ giảm cân', 'MAX', 50.00, 'g', 'DAILY', 'ACTIVE', 'Chuyên gia dinh dưỡng'),
(4, 5, 2, NULL, 'Hạn chế carbohydrate', 'Áp dụng chế độ low-carb', 'MAX', 150.00, 'g', 'DAILY', 'ACTIVE', 'App LanhCare');

SELECT setval('dietary_restriction_id_seq', 4, true);

-- ============================================
-- 23. FCM TOKEN (Push notification tokens)
-- ============================================
INSERT INTO fcmtoken (id, account_id, token, created_at, updated_at) VALUES
(1, 3, 'dKjH8sL9_fake_token_user3_android_device', NOW(), NOW()),
(2, 4, 'fGhI2jK3_fake_token_user4_ios_device', NOW(), NOW()),
(3, 5, 'lMnO4pQ5_fake_token_user5_android_device', NOW(), NOW()),
(4, 7, 'rStU6vW7_fake_token_user7_ios_device', NOW(), NOW());

SELECT setval('fcmtoken_id_seq', 4, true);

-- ============================================
-- VERIFICATION
-- ============================================
DO $$
BEGIN
    RAISE NOTICE '✅ Seed data imported successfully!';
    RAISE NOTICE '📊 Summary:';
    RAISE NOTICE '   - Accounts: %', (SELECT COUNT(*) FROM account);
    RAISE NOTICE '   - Health Profiles: %', (SELECT COUNT(*) FROM user_health_profile);
    RAISE NOTICE '   - Food Types: %', (SELECT COUNT(*) FROM food_type);
    RAISE NOTICE '   - Food Items: %', (SELECT COUNT(*) FROM food_item);
    RAISE NOTICE '   - Nutrients: %', (SELECT COUNT(*) FROM nutrient);
    RAISE NOTICE '   - Exercise Types: %', (SELECT COUNT(*) FROM exercise_type);
    RAISE NOTICE '   - Daily Logs: %', (SELECT COUNT(*) FROM daily_log);
    RAISE NOTICE '   - Meal Logs: %', (SELECT COUNT(*) FROM meal_log);
    RAISE NOTICE '   - Exercise Logs: %', (SELECT COUNT(*) FROM exercise_log);
    RAISE NOTICE '   - Service Plans: %', (SELECT COUNT(*) FROM service_plan);
    RAISE NOTICE '   - Transactions: %', (SELECT COUNT(*) FROM transaction);
    RAISE NOTICE '   - Hospitals: %', (SELECT COUNT(*) FROM hospital);
    RAISE NOTICE '   - ICD11 Chapters: %', (SELECT COUNT(*) FROM icd11_chapter);
    RAISE NOTICE '   - ICD11 Codes: %', (SELECT COUNT(*) FROM icd11_code);
    RAISE NOTICE '   - Posts: %', (SELECT COUNT(*) FROM post);
    RAISE NOTICE '   - Comments: %', (SELECT COUNT(*) FROM comment);
END $$;
