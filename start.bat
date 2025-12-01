@echo off
REM Build and start LanhCare API with Docker

echo ================================
echo  LanhCare API - Build and Start
echo ================================
echo.

echo [1/3] Stopping existing containers...
docker-compose down

echo.
echo [2/3] Building Docker image...
docker-compose build --no-cache app

echo.
echo [3/3] Starting services...
docker-compose up -d

echo.
echo ================================
echo  Services Starting...
echo ================================
echo.
echo Waiting for application to start (this may take 2-3 minutes)...
echo.

REM Wait for app to be ready
timeout /t 10 /nobreak > nul

echo Checking application status...
docker-compose ps

echo.
echo ================================
echo  Application URLs
echo ================================
echo.
echo API Base URL:     http://localhost:8080
echo Swagger UI:       http://localhost:8080/swagger-ui.html
echo API Docs:         http://localhost:8080/v3/api-docs
echo Health Check:     http://localhost:8080/actuator/health
echo.
echo MySQL Workbench:
echo   Host: localhost
echo   Port: 3306
echo   User: root
echo   Pass: rootpassword
echo   DB:   health_app_db
echo.
echo ================================

echo.
echo To view logs, run: logs.bat
echo To stop services, run: stop.bat
echo.
pause
