@echo off

cd /d "%~dp0"

echo.
echo [INFO] Project Directory: %CD%

REM ===== CHECK STRUCTURE =====
if not exist "com\gradeapp" (
    echo.
    echo ❌ ERROR: Cannot find com\gradeapp\ folder!
    echo    Your source files should be in: com\gradeapp\
    echo.
    pause
    exit /b 1
)

if not exist "lib" (
    echo.
    echo ❌ ERROR: lib\ folder not found!
    echo    Please create lib\ and put postgresql-XX.jar inside
    echo.
    pause
    exit /b 1
)

REM Check for JAR files
set "JAR_COUNT=0"
for %%f in ("lib\*.jar") do set /a JAR_COUNT+=1

if %JAR_COUNT%==0 (
    echo.
    echo ❌ ERROR: No .jar files in lib\
    echo    Download PostgreSQL JDBC driver from:
    echo    https://jdbc.postgresql.org/download/
    echo.
    pause
    exit /b 1
)

echo [INFO] Found %JAR_COUNT% JAR file(s) in lib\

REM ===== PREPARE OUTPUT =====
if not exist out mkdir out

REM ===== COLLECT SOURCE FILES =====
echo.
echo [1/3] Collecting source files...
echo --------------------------------------------

if exist sources.txt del sources.txt

REM Find all .java files in com\gradeapp
for /r "com\gradeapp" %%f in (*.java) do @echo %%f >> sources.txt

REM Check if sources.txt was created
if not exist sources.txt (
    echo.
    echo ❌ ERROR: No Java files found!
    pause
    exit /b 1
)

REM Count files
for /f %%A in ('type sources.txt ^| find /c /v ""') do set count=%%A

if "%count%"=="0" (
    echo.
    echo ❌ ERROR: sources.txt is empty!
    del sources.txt
    pause
    exit /b 1
)

echo ✅ Found %count% Java source files

REM ===== COMPILE =====
echo.
echo [2/3] Compiling...
echo --------------------------------------------

REM Classpath: lib\* for PostgreSQL driver
set "CP=lib\*"

REM Compile to out\
javac -encoding UTF-8 -cp "%CP%" -d out @sources.txt

if errorlevel 1 (
    echo.
    echo ❌ Compilation FAILED!
    echo    Check error messages above.
    echo.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo ✅ Compilation successful! (Output: out\)

REM ===== RUN =====
echo.
echo [3/3] Running application...
echo --------------------------------------------
echo.
echo [INFO] Database Connection:
echo        Host: localhost:5432
echo        Database: gradedb
echo        User: postgres
echo.
echo [INFO] Make sure PostgreSQL is running!
echo.
echo Press any key to start the application...
pause >nul

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

echo.
echo ============================================
echo   Application stopped normally
echo ============================================
pause