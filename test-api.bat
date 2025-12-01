@echo off
REM Quick API Test Script

echo ================================
echo  LanhCare API - Quick Test
echo ================================
echo.

echo Testing API endpoints...
echo.

echo [1] Health Check
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo [2] Auth Service Health
curl -s http://localhost:8080/api/auth/health
echo.
echo.

echo ================================
echo  Test Authentication
echo ================================
echo.

echo [3] Register Test User
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"testuser@lanhcare.com\",\"fullname\":\"Test User\",\"password\":\"password123\"}"
echo.
echo.

echo [4] Login Test User
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"testuser@lanhcare.com\",\"password\":\"password123\"}"
echo.
echo.

echo ================================
echo.
echo Copy the accessToken from above and use it to test other endpoints!
echo.
echo Example:
echo   curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/foods
echo.
echo Or visit Swagger UI for interactive testing:
echo   http://localhost:8080/swagger-ui.html
echo.
echo ================================
pause
