@echo off
echo ===============================================
echo    LanhCare - Reset Database and Rebuild
echo ===============================================
echo.
echo WARNING: This will DELETE all data in the database!
echo.
set /p confirm="Are you sure? (yes/no): "

if /i "%confirm%"=="yes" (
    echo.
    echo Stopping services...
    docker-compose down -v
    
    echo.
    echo Rebuilding and starting services...
    docker-compose up -d --build
    
    echo.
    echo Reset complete!
) else (
    echo.
    echo Reset cancelled.
)

echo.
pause
