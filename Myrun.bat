@echo off
cd /d "%~dp0"
set "OUT=out"
set "LIB=lib\*"
set "CP=%OUT%;%LIB%"

if "%~1"=="" (
    echo Usage: run.bat full.package.ClassName
    echo Example: run.bat com.gradeapp.Main
    pause
    exit /b 1
)

set "MAIN=%~1"
echo runnig %MAIN% ...
java -cp "%CP%" %MAIN%

if %ERRORLEVEL% neq 0 (
    echo .
    echo Application exited with error.
    pause
    exit /b 1
)
pause
