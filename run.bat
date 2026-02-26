REM Classpath: out\ (compiled classes) + lib\* (PostgreSQL)
set "CP=out;lib\*"

java -cp "%CP%" com.gradeapp.app.Main

if errorlevel 1 (
    echo.
    echo ❌ Application failed to start!
    echo    Check if database is running and credentials are correct.
    pause
    exit /b 1
)
