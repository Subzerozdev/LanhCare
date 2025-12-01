@echo off
chcp 65001 >nul
echo ================================================
echo    IMPORT DỮ LIỆU TIẾNG VIỆT VÀO DATABASE
echo ================================================
echo.

echo Bước 1: Xóa dữ liệu cũ...
docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SET FOREIGN_KEY_CHECKS=0; TRUNCATE TABLE fcmtoken; TRUNCATE TABLE meal_log; TRUNCATE TABLE food_nutrient; TRUNCATE TABLE dietary_restriction; TRUNCATE TABLE medical_specialty; TRUNCATE TABLE icd11_translation; TRUNCATE TABLE icd11_code; TRUNCATE TABLE icd11_chapter; TRUNCATE TABLE food_item; TRUNCATE TABLE food_type; TRUNCATE TABLE nutrient; TRUNCATE TABLE user_health_profile; TRUNCATE TABLE transaction; TRUNCATE TABLE hospital; TRUNCATE TABLE service_plan; TRUNCATE TABLE account; SET FOREIGN_KEY_CHECKS=1;" 2>nul

echo.
echo Bước 2: Import dữ liệu mới với UTF-8...
type src\main\resources\init-data-fixed.sql | docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db --default-character-set=utf8mb4

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================
    echo ✓ IMPORT THÀNH CÔNG!
    echo ================================================
    echo.
    echo Kiểm tra dữ liệu:
    docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT id, email, fullname, role FROM account LIMIT 5;" 2>nul
    echo.
    docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT id, name FROM food_type LIMIT 5;" 2>nul
    echo.
    docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT id, name FROM hospital LIMIT 5;" 2>nul
) else (
    echo.
    echo ================================================
    echo ✗ LỖI KHI IMPORT!
    echo ================================================
    echo Vui lòng kiểm tra lại file SQL hoặc kết nối MySQL.
)

echo.
echo ================================================
pause
