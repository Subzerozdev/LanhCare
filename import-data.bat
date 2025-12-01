@echo off
echo Importing sample data into database...
echo.

REM Read SQL file and replace uppercase table names with lowercase
powershell -Command "(Get-Content src\main\resources\init-data.sql) -replace 'Account', 'account' -replace 'ServicePlan', 'service_plan' -replace 'Transaction', 'transaction' -replace 'FCMToken', 'fcmtoken' -replace 'UserHealthProfile', 'user_health_profile' -replace 'FoodType', 'food_type' -replace 'FoodItem', 'food_item' -replace 'MealLog', 'meal_log' -replace 'Nutrient', 'nutrient' -replace 'FoodNutrient', 'food_nutrient' -replace 'ICD11Chapter', 'icd11_chapter' -replace 'ICD11Code', 'icd11_code' -replace 'ICD11Translation', 'icd11_translation' -replace 'DietaryRestriction', 'dietary_restriction' -replace 'Hospital', 'hospital' -replace 'MedicalSpecialty', 'medical_specialty' | docker exec -i lanhcare-mysql mysql -uroot -prootpassword health_app_db"

echo.
echo Done! Data imported successfully.
echo.
pause
