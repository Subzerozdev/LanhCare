@echo off
echo ===============================================
echo    LanhCare - Stopping Docker Services
echo ===============================================
echo.

docker-compose down

echo.
echo Services stopped successfully!
echo.
pause
