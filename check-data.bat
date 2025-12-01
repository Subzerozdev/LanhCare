@echo off
echo ================================================
echo    DATABASE DATA SUMMARY
echo ================================================
echo.

echo Checking account table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as Accounts FROM account;" 2>nul | findstr /V "Warning COUNT"

echo.
echo Checking service_plan table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as ServicePlans FROM service_plan;" 2>nul | findstr /V "Warning COUNT"

echo.
echo Checking food_type table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as FoodTypes FROM food_type;" 2>nul | findstr /V "Warning COUNT"

echo.
echo Checking food_item table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as FoodItems FROM food_item;" 2>nul | findstr /V "Warning COUNT"

echo.
echo Checking hospital table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as Hospitals FROM hospital;" 2>nul | findstr /V "Warning COUNT"

echo.
echo Checking nutrient table...
docker exec lanhcare-mysql mysql -uroot -prootpassword health_app_db -e "SELECT COUNT(*) as Nutrients FROM nutrient;" 2>nul | findstr /V "Warning COUNT"

echo.
echo ================================================
pause
