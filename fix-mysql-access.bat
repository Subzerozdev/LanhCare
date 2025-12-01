@echo off
echo ================================================
echo    RESET MySQL User Permissions
echo ================================================
echo.

echo Resetting root user permissions...
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "ALTER USER 'root'@'%%' IDENTIFIED WITH mysql_native_password BY 'rootpassword';"
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%%' WITH GRANT OPTION;"

echo.
echo Creating lanhcare user...
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "DROP USER IF EXISTS 'lanhcare'@'%%';"
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "CREATE USER 'lanhcare'@'%%' IDENTIFIED BY 'lanhcare123';"
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "GRANT ALL PRIVILEGES ON health_app_db.* TO 'lanhcare'@'%%';"

echo.
echo Flushing privileges...
docker exec -it lanhcare-mysql mysql -uroot -prootpassword -e "FLUSH PRIVILEGES;"

echo.
echo ================================================
echo Done! You can now connect with:
echo.
echo Username: lanhcare
echo Password: lanhcare123
echo Host: 127.0.0.1
echo Port: 3306
echo Database: health_app_db
echo ================================================
echo.
pause
