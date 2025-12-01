@echo off
echo ===============================================
echo    LanhCare - View Docker Logs
echo ===============================================
echo.
echo Select which logs to view:
echo   1. All services
echo   2. App only
echo   3. MySQL only
echo.
set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" (
    echo.
    echo Viewing all service logs...
    docker-compose logs -f
) else if "%choice%"=="2" (
    echo.
    echo Viewing app logs...
    docker-compose logs -f app
) else if "%choice%"=="3" (
    echo.
    echo Viewing MySQL logs...
    docker-compose logs -f mysql
) else (
    echo Invalid choice!
    pause
)
