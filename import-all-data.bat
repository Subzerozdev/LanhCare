@echo off
chcp 65001 >nul
echo ================================================
echo    RESET VÀ IMPORT LẠI TẤT CẢ DỮ LIỆU
echo ================================================
echo.

echo [1/16] Import Accounts...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE account; INSERT INTO account (id, email, fullname, password, role, status) VALUES (1, 'admin@lanhcare.com', 'Quản trị viên', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'ACTIVE'), (2, 'user1@lanhcare.com', 'Nguyễn Văn An', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'), (3, 'user2@lanhcare.com', 'Trần Thị Bình', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', 'ACTIVE'), (4, 'doctor@lanhcare.com', 'BS. Lê Văn Cường', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', 'ACTIVE'), (5, 'nutritionist@lanhcare.com', 'CN. Phạm Thị Dung', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'NUTRITIONIST', 'ACTIVE');" 2>nul

echo [2/16] Import Service Plans...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE service_plan; INSERT INTO service_plan (id, name, description, price, period_value, period_unit, status) VALUES (1, 'Miễn phí', 'Gói miễn phí với các tính năng cơ bản', 0.00, 1, 'MONTH', 'ACTIVE'), (2, 'Cao cấp tháng', 'Gói cao cấp hàng tháng với đầy đủ tính năng', 99000.00, 1, 'MONTH', 'ACTIVE'), (3, 'Cao cấp năm', 'Gói cao cấp hàng năm - Tiết kiệm 20%%', 950000.00, 1, 'YEAR', 'ACTIVE'), (4, 'Doanh nghiệp', 'Gói doanh nghiệp cho tổ chức', 5000000.00, 1, 'YEAR', 'ACTIVE');" 2>nul

echo [3/16] Import Food Types...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE food_type; INSERT INTO food_type (id, name, is_deleted) VALUES (1, 'Rau củ quả', FALSE), (2, 'Thịt và sản phẩm từ thịt', FALSE), (3, 'Cá và hải sản', FALSE), (4, 'Trứng và sữa', FALSE), (5, 'Ngũ cốc và tinh bột', FALSE), (6, 'Trái cây', FALSE), (7, 'Đồ uống', FALSE), (8, 'Đồ ăn nhanh', FALSE);" 2>nul

echo [4/16] Import Nutrients...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE nutrient; INSERT INTO nutrient (id, name, unit) VALUES (1, 'Protein', 'g'), (2, 'Carbohydrate', 'g'), (3, 'Chất béo', 'g'), (4, 'Chất xơ', 'g'), (5, 'Đường', 'g'), (6, 'Vitamin A', 'mcg'), (7, 'Vitamin C', 'mg'), (8, 'Vitamin D', 'mcg'), (9, 'Canxi', 'mg'), (10, 'Sắt', 'mg'), (11, 'Natri', 'mg'), (12, 'Kali', 'mg');" 2>nul

echo [5/16] Import Hospitals...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE hospital; INSERT INTO hospital (id, name, address, latitude, longitude, status) VALUES (1, 'Bệnh viện Chợ Rẫy', '201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP.HCM', 10.7546729, 106.6573527, 'ACTIVE'), (2, 'Bệnh viện Đại học Y Dược TP.HCM', '215 Hồng Bàng, Phường 11, Quận 5, TP.HCM', 10.7563847, 106.6543272, 'ACTIVE'), (3, 'Bệnh viện Nhân dân 115', '527 Sư Vạn Hạnh, Phường 12, Quận 10, TP.HCM', 10.7723742, 106.6644367, 'ACTIVE'), (4, 'Bệnh viện Thống Nhất', '1 Lý Thường Kiệt, Phường 7, Quận Tân Bình, TP.HCM', 10.7929157, 106.6535897, 'ACTIVE'), (5, 'Bệnh viện Nhi đồng 1', '341 Sư Vạn Hạnh, Phường 12, Quận 10, TP.HCM', 10.7695342, 106.6663428, 'ACTIVE');" 2>nul

echo [6/16] Import ICD11 Chapters...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE icd11_chapter; INSERT INTO icd11_chapter (chapter_uri, vn_title, original_title_en, chapter_code, release_id, status) VALUES ('http://id.who.int/icd/entity/1435254666', 'Bệnh nội tiết, dinh dưỡng hoặc chuyển hóa', 'Endocrine, nutritional or metabolic diseases', '05', '2024-01', 'ACTIVE'), ('http://id.who.int/icd/entity/1294209752', 'Bệnh của hệ tuần hoàn', 'Diseases of the circulatory system', '11', '2024-01', 'ACTIVE'), ('http://id.who.int/icd/entity/334423054', 'Bệnh của hệ tiêu hóa', 'Diseases of the digestive system', '13', '2024-01', 'ACTIVE');" 2>nul

echo [7/16] Import ICD11 Codes...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE icd11_code; INSERT INTO icd11_code (icd_uri, chapter_uri, icd_code, original_title_en, definition_en, parent_uri, is_residual_category, is_leaf, last_synced, status) VALUES ('http://id.who.int/icd/entity/2030283443', 'http://id.who.int/icd/entity/1435254666', '5A10', 'Type 2 diabetes mellitus', 'A metabolic disorder characterized by high blood glucose in the context of insulin resistance and relative insulin deficiency.', NULL, FALSE, TRUE, NOW(), 'ACTIVE'), ('http://id.who.int/icd/entity/1881269402', 'http://id.who.int/icd/entity/1435254666', '5B81', 'Obesity', 'Excessive accumulation of body fat that presents a risk to health.', NULL, FALSE, TRUE, NOW(), 'ACTIVE'), ('http://id.who.int/icd/entity/398019458', 'http://id.who.int/icd/entity/1294209752', 'BA00', 'Essential hypertension', 'Persistently elevated blood pressure in the absence of an identifiable cause.', NULL, FALSE, TRUE, NOW(), 'ACTIVE');" 2>nul

echo [8/16] Import ICD11 Translations...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE icd11_translation; INSERT INTO icd11_translation (icd_uri, vn_title, vn_definition, review_status, status) VALUES ('http://id.who.int/icd/entity/2030283443', 'Đái tháo đường type 2', 'Rối loạn chuyển hóa đặc trưng bởi glucose máu cao trong bối cảnh kháng insulin và thiếu hụt insulin tương đối.', 'APPROVED', 'PUBLISHED'), ('http://id.who.int/icd/entity/1881269402', 'Béo phì', 'Tích tụ mỡ cơ thể quá mức gây nguy cơ cho sức khỏe.', 'APPROVED', 'PUBLISHED'), ('http://id.who.int/icd/entity/398019458', 'Tăng huyết áp nguyên phát', 'Huyết áp tăng cao liên tục mà không xác định được nguyên nhân.', 'APPROVED', 'PUBLISHED');" 2>nul

echo [9/16] Import Medical Specialties...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE medical_specialty; INSERT INTO medical_specialty (id, hospital_id, icd_uri, name_vn, name_en, status) VALUES (1, 1, 'http://id.who.int/icd/entity/1435254666', 'Khoa Nội tiết - Đái tháo đường', 'Endocrinology - Diabetes', 'ACTIVE'), (2, 1, 'http://id.who.int/icd/entity/1294209752', 'Khoa Tim mạch', 'Cardiology', 'ACTIVE'), (3, 2, 'http://id.who.int/icd/entity/1435254666', 'Khoa Dinh dưỡng', 'Nutrition', 'ACTIVE'), (4, 3, 'http://id.who.int/icd/entity/334423054', 'Khoa Tiêu hóa', 'Gastroenterology', 'ACTIVE'), (5, 4, 'http://id.who.int/icd/entity/1294209752', 'Khoa Nội tổng hợp', 'General Internal Medicine', 'ACTIVE');" 2>nul

echo [10/16] Import Food Items...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE food_item; INSERT INTO food_item (id, food_type_id, name, description, calo, serving_unit, standard_serving_size, status, data_source) VALUES (1, 5, 'Cơm trắng', 'Cơm trắng nấu chín', 130.00, 'g', 100.00, 'ACTIVE', 'USDA'), (2, 5, 'Phở bò', 'Phở bò truyền thống', 350.00, 'bát', 1.00, 'ACTIVE', 'Vietnam'), (8, 2, 'Ức gà luộc', 'Ức gà luộc không da', 165.00, 'g', 100.00, 'ACTIVE', 'USDA'), (10, 3, 'Cá hồi nướng', 'Cá hồi nướng', 206.00, 'g', 100.00, 'ACTIVE', 'USDA'), (13, 4, 'Trứng gà luộc', 'Trứng gà luộc chín', 155.00, 'quả', 1.00, 'ACTIVE', 'USDA'), (14, 4, 'Sữa tươi không đường', 'Sữa tươi tiệt trùng', 42.00, 'ml', 100.00, 'ACTIVE', 'Vietnam'), (16, 6, 'Chuối tiêu', 'Chuối tiêu chín', 89.00, 'g', 100.00, 'ACTIVE', 'USDA'), (18, 6, 'Cam', 'Cam tươi', 47.00, 'g', 100.00, 'ACTIVE', 'USDA');" 2>nul

echo [11/16] Import User Health Profiles...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE user_health_profile; INSERT INTO user_health_profile (id, account_id, date_of_birth, gender, height_cm, weight_kg, activity_level, bmi_value, health_goals, created_at, updated_at) VALUES (1, 2, '1990-05-15', 'MALE', 175.00, 70.00, 'MODERATE', 22.86, 'Duy trì cân nặng và tăng cường sức khỏe', NOW(), NOW()), (2, 3, '1995-08-20', 'FEMALE', 160.00, 55.00, 'LIGHT', 21.48, 'Giảm 3kg trong 3 tháng', NOW(), NOW());" 2>nul

echo [12/16] Import Transactions...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE transaction; INSERT INTO transaction (id, account_id, service_plan_id, transaction_date, amount, payment_method, status) VALUES (1, 2, 2, NOW(), 99000.00, 'MOMO', 'COMPLETED'), (2, 3, 3, NOW(), 950000.00, 'VNPAY', 'COMPLETED');" 2>nul

echo [13/16] Import Dietary Restrictions...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE dietary_restriction; INSERT INTO dietary_restriction (id, user_health_profile_id, nutrient_id, icd_uri, name, description, limit_type, limit_value, limit_unit, frequency, status, source_of_advice) VALUES (1, 1, 5, 'http://id.who.int/icd/entity/2030283443', 'Hạn chế đường', 'Hạn chế đường do tiền đái tháo đường', 'MAX', 25.00, 'g', 'DAILY', 'ACTIVE', 'Bác sĩ nội tiết'), (2, 2, 11, NULL, 'Hạn chế muối', 'Giảm natri để kiểm soát huyết áp', 'MAX', 2000.00, 'mg', 'DAILY', 'ACTIVE', 'Chuyên gia dinh dưỡng');" 2>nul

echo [14/16] Import Food Nutrients...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE food_nutrient; INSERT INTO food_nutrient (food_item_id, nutrient_id, value) VALUES (1, 1, 2.7), (1, 2, 28.0), (1, 3, 0.3), (8, 1, 31.0), (8, 2, 0.0), (8, 3, 3.6), (10, 1, 22.0), (10, 2, 0.0), (10, 3, 13.0), (13, 1, 13.0), (13, 2, 1.1), (13, 3, 11.0);" 2>nul

echo [15/16] Import Meal Logs...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE meal_log; INSERT INTO meal_log (id, account_id, food_item_id, meal_type, meal_date, logged_time, total_calories, notes, created_at) VALUES (1, 2, 1, 'BREAKFAST', CURDATE(), '07:00:00', 260.00, 'Hai bát cơm sáng', NOW()), (2, 2, 13, 'BREAKFAST', CURDATE(), '07:05:00', 155.00, 'Một quả trứng luộc', NOW()), (3, 2, 2, 'LUNCH', CURDATE(), '12:00:00', 350.00, 'Phở bò trưa', NOW());" 2>nul

echo [16/16] Import FCM Tokens...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SET NAMES utf8mb4; TRUNCATE TABLE fcmtoken; INSERT INTO fcmtoken (id, account_id, token, device_type, created_at, updated_at) VALUES (1, 2, 'sample_fcm_token_android_user2', 'ANDROID', NOW(), NOW()), (2, 3, 'sample_fcm_token_ios_user3', 'IOS', NOW(), NOW());" 2>nul

echo.
echo ================================================
echo ✓ HOÀN TẤT IMPORT TẤT CẢ DỮ LIỆU!
echo ================================================
echo.
echo Kiểm tra dữ liệu tiếng Việt:
echo.
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SELECT id, fullname, email FROM account LIMIT 5;" 2>nul
echo.
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SELECT id, name FROM food_type LIMIT 5;" 2>nul
echo.
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4 -e "SELECT id, name FROM hospital LIMIT 5;" 2>nul
echo.
echo ================================================
pause
