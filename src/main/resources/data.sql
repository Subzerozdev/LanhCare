USE
defaultdb;

-- LanhCare Sample Data (Vietnamese)
-- This file is automatically executed by Spring Boot on startup
-- Only runs if tables are empty (spring.sql.init.mode=always)

-- Reset sequences (PostgreSQL)
ALTER SEQUENCE IF EXISTS account_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS service_plan_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS food_type_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS nutrient_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS hospital_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS food_item_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS user_health_profile_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS transaction_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS dietary_restriction_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS medical_specialty_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS icd11_translation_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS fcmtoken_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS meal_log_id_seq RESTART WITH 1;

-- 1. Accounts (5 users)
INSERT INTO account (id, email, full_name, password, role, status)
VALUES (1, 'admin@lanhcare.com', 'Quản trị viên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN', 'ACTIVE'),
       (2, 'user1@lanhcare.com', 'Nguyễn Văn An', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'USER', 'ACTIVE'),
       (3, 'user2@lanhcare.com', 'Trần Thị Bình', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'USER', 'ACTIVE'),
       (4, 'doctor@lanhcare.com', 'BS. Lê Văn Cường', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'STAFF', 'ACTIVE'),
       (5, 'nutritionist@lanhcare.com', 'CN. Phạm Thị Dung',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'STAFF', 'ACTIVE') ON CONFLICT (id) DO NOTHING;

-- 2. Service Plans (4 plans)
INSERT INTO service_plan (id, name, description, price, period_value, period_unit, status)
VALUES (1, 'Miễn phí', 'Gói miễn phí với các tính năng cơ bản', 0.00, 1, 'MONTH', 'ACTIVE'),
       (2, 'Cao cấp tháng', 'Gói cao cấp hàng tháng với đầy đủ tính năng', 99000.00, 1, 'MONTH', 'ACTIVE'),
       (3, 'Cao cấp năm', 'Gói cao cấp hàng năm - Tiết kiệm 20%', 950000.00, 1, 'YEAR', 'ACTIVE'),
       (4, 'Doanh nghiệp', 'Gói doanh nghiệp cho tổ chức', 5000000.00, 1, 'YEAR', 'ACTIVE') ON CONFLICT (id) DO NOTHING;

-- 3. Food Types (8 types)
INSERT INTO food_type (id, name, is_deleted)
VALUES (1, 'Rau củ quả', false),
       (2, 'Thịt và sản phẩm từ thịt', false),
       (3, 'Cá và hải sản', false),
       (4, 'Trứng và sữa', false),
       (5, 'Ngũ cốc và tinh bột', false),
       (6, 'Trái cây', false),
       (7, 'Đồ uống', false),
       (8, 'Đồ ăn nhanh', false) ON CONFLICT (id) DO NOTHING;

-- 4. Nutrients (12 nutrients)
INSERT INTO nutrient (id, name, unit)
VALUES (1, 'Protein', 'g'),
       (2, 'Carbohydrate', 'g'),
       (3, 'Chất béo', 'g'),
       (4, 'Chất xơ', 'g'),
       (5, 'Đường', 'g'),
       (6, 'Vitamin A', 'mcg'),
       (7, 'Vitamin C', 'mg'),
       (8, 'Vitamin D', 'mcg'),
       (9, 'Canxi', 'mg'),
       (10, 'Sắt', 'mg'),
       (11, 'Natri', 'mg'),
       (12, 'Kali', 'mg') ON CONFLICT (id) DO NOTHING;

-- 5. Hospitals (5 hospitals in HCM)
INSERT INTO hospital (id, name, address, latitude, longitude, status)
VALUES (1, 'Bệnh viện Chợ Rẫy', '201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP.HCM', 10.7546729, 106.6573527, 'ACTIVE'),
       (2, 'Bệnh viện Đại học Y Dược TP.HCM', '215 Hồng Bàng, Phường 11, Quận 5, TP.HCM', 10.7563847, 106.6543272,
        'ACTIVE'),
       (3, 'Bệnh viện Nhân dân 115', '527 Sư Vạn Hạnh, Phường 12, Quận 10, TP.HCM', 10.7723742, 106.6644367, 'ACTIVE'),
       (4, 'Bệnh viện Thống Nhất', '1 Lý Thường Kiệt, Phường 7, Quận Tân Bình, TP.HCM', 10.7929157, 106.6535897,
        'ACTIVE'),
       (5, 'Bệnh viện Nhi đồng 1', '341 Sư Vạn Hạnh, Phường 12, Quận 10, TP.HCM', 10.7695342, 106.6663428,
        'ACTIVE') ON CONFLICT (id) DO NOTHING;

-- 6. ICD11 Chapters (3 chapters)
INSERT INTO icd11_chapter (chapter_uri, vn_title, original_title_en, chapter_code, release_id, status)
VALUES ('http://id.who.int/icd/entity/1435254666', 'Bệnh nội tiết, dinh dưỡng hoặc chuyển hóa',
        'Endocrine, nutritional or metabolic diseases', '05', '2024-01', 'ACTIVE'),
       ('http://id.who.int/icd/entity/1294209752', 'Bệnh của hệ tuần hoàn', 'Diseases of the circulatory system', '11',
        '2024-01', 'ACTIVE'),
       ('http://id.who.int/icd/entity/334423054', 'Bệnh của hệ tiêu hóa', 'Diseases of the digestive system', '13',
        '2024-01', 'ACTIVE') ON CONFLICT (chapter_uri) DO NOTHING;

-- 7. ICD11 Codes (3 codes)
INSERT INTO icd11_code (icd_uri, chapter_uri, icd_code, original_title_en, definition_en, parent_uri, last_synced, status)
VALUES ('http://id.who.int/icd/entity/2030283443', 'http://id.who.int/icd/entity/1435254666', '5A10',
        'Type 2 diabetes mellitus',
        'A metabolic disorder characterized by high blood glucose in the context of insulin resistance and relative insulin deficiency.',
        NULL, CURRENT_TIMESTAMP, 'ACTIVE'),
       ('http://id.who.int/icd/entity/1881269402', 'http://id.who.int/icd/entity/1435254666', '5B81', 'Obesity',
        'Excessive accumulation of body fat that presents a risk to health.', NULL, CURRENT_TIMESTAMP,
        'ACTIVE'),
       ('http://id.who.int/icd/entity/398019458', 'http://id.who.int/icd/entity/1294209752', 'BA00',
        'Essential hypertension', 'Persistently elevated blood pressure in the absence of an identifiable cause.', NULL,
        CURRENT_TIMESTAMP, 'ACTIVE') ON CONFLICT (icd_uri) DO NOTHING;

-- 8. ICD11 Translations (3 translations)
INSERT INTO icd11_translation (id, icd_uri, vn_title, vn_definition, status)
VALUES (1, 'http://id.who.int/icd/entity/2030283443', 'Đái tháo đường type 2',
        'Rối loạn chuyển hóa đặc trưng bởi glucose máu cao trong bối cảnh kháng insulin và thiếu hụt insulin tương đối.',
        'PUBLISHED'),
       (2, 'http://id.who.int/icd/entity/1881269402', 'Béo phì', 'Tích tụ mỡ cơ thể quá mức gây nguy cơ cho sức khỏe.',
        'PUBLISHED'),
       (3, 'http://id.who.int/icd/entity/398019458', 'Tăng huyết áp nguyên phát',
        'Huyết áp tăng cao liên tục mà không xác định được nguyên nhân.', 'PUBLISHED') ON CONFLICT (id) DO NOTHING;

-- 9. Medical Specialties (5 specialties)

-- 10. Food Items (8 items)
INSERT INTO food_item (id, food_type_id, name, description, calo, serving_unit, standard_serving_size, status,
                       data_source)
VALUES (1, 5, 'Cơm trắng', 'Cơm trắng nấu chín', 130.00, 'g', 100.00, 'ACTIVE', 'USDA'),
       (2, 5, 'Phở bò', 'Phở bò truyền thống', 350.00, 'bát', 1.00, 'ACTIVE', 'Vietnam'),
       (8, 2, 'Ức gà luộc', 'Ức gà luộc không da', 165.00, 'g', 100.00, 'ACTIVE', 'USDA'),
       (10, 3, 'Cá hồi nướng', 'Cá hồi nướng', 206.00, 'g', 100.00, 'ACTIVE', 'USDA'),
       (13, 4, 'Trứng gà luộc', 'Trứng gà luộc chín', 155.00, 'quả', 1.00, 'ACTIVE', 'USDA'),
       (14, 4, 'Sữa tươi không đường', 'Sữa tươi tiệt trùng', 42.00, 'ml', 100.00, 'ACTIVE', 'Vietnam'),
       (16, 6, 'Chuối tiêu', 'Chuối tiêu chín', 89.00, 'g', 100.00, 'ACTIVE', 'USDA'),
       (18, 6, 'Cam', 'Cam tươi', 47.00, 'g', 100.00, 'ACTIVE', 'USDA') ON CONFLICT (id) DO NOTHING;

-- 11. User Health Profiles (2 profiles)
INSERT INTO user_health_profile (id, account_id, date_of_birth, gender, height_cm, weight_kg, activity_level, bmi_value,
                                 bmi_status, health_goals, created_at, updated_at, tdde_value)
VALUES (1, 2, '1990-05-15', 'MALE', 175.00, 70.00, 'NO_EXERCISE', 22.86, 'NORMAL', 'LOSE_WEIGHT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1.0),
       (2, 3, '1995-08-20', 'FEMALE', 160.00, 55.00, 'NO_EXERCISE', 21.48, 'NORMAL', 'MAINTAIN', CURRENT_TIMESTAMP
        , CURRENT_TIMESTAMP,1.0) ON CONFLICT (id) DO NOTHING;

-- 12. Transactions (2 transactions)
INSERT INTO transaction (id, account_id, service_plan_id, transaction_date, amount, payment_method, status)
VALUES (1, 2, 2, CURRENT_TIMESTAMP, 99000.00, 'MOMO', 'COMPLETED'),
       (2, 3, 3, CURRENT_TIMESTAMP, 950000.00, 'VNPAY', 'COMPLETED') ON CONFLICT (id) DO NOTHING;

-- 13. Dietary Restrictions (2 restrictions)
INSERT INTO dietary_restriction (id, user_health_profile_id, nutrient_id, icd_uri, name, description, limit_type,
                                 limit_value, limit_unit, frequency, status, source_of_advice)
VALUES (1, 1, 5, 'http://id.who.int/icd/entity/2030283443', 'Hạn chế đường', 'Hạn chế đường do tiền đái tháo đường',
        'MAX', 25.00, 'g', 'DAILY', 'ACTIVE', 'Bác sĩ nội tiết'),
       (2, 2, 11, NULL, 'Hạn chế muối', 'Giảm natri để kiểm soát huyết áp', 'MAX', 2000.00, 'mg', 'DAILY', 'ACTIVE',
        'Chuyên gia dinh dưỡng') ON CONFLICT (id) DO NOTHING;

-- 14. Food Nutrients (12 records)
INSERT INTO food_nutrient (food_item_id, nutrient_id, value)
VALUES (1, 1, 2.7),
       (1, 2, 28.0),
       (1, 3, 0.3),
       (8, 1, 31.0),
       (8, 2, 0.0),
       (8, 3, 3.6),
       (10, 1, 22.0),
       (10, 2, 0.0),
       (10, 3, 13.0),
       (13, 1, 13.0),
       (13, 2, 1.1),
       (13, 3, 11.0) ;

-- 15. Meal Logs (3 logs)
INSERT INTO meal_log (id, account_id, meal_type, meal_date, logged_time, total_calories, notes,
                      created_at)
VALUES (1, 2, 'BREAKFAST', CURRENT_DATE, '07:00:00', 260.00, 'Hai bát cơm sáng', CURRENT_TIMESTAMP),
       (2, 2, 'BREAKFAST', CURRENT_DATE, '07:05:00', 155.00, 'Một quả trứng luộc', CURRENT_TIMESTAMP),
       (3, 2, 'LUNCH', CURRENT_DATE, '12:00:00', 350.00, 'Phở bò trưa',
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 16. FCM Tokens (2 tokens)
INSERT INTO fcmtoken (id, account_id, token, created_at, updated_at)
VALUES (1, 2, 'sample_fcm_token_android_user2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, 'sample_fcm_token_ios_user3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;
