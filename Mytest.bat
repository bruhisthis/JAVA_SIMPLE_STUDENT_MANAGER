@echo off
cd /d "%~dp0"
if "%~1"==""(
    echo Usage: test.bat path\to\YourTestClass.java
    pause
    exit /b 1
)

set "SRCFILE=%~1"
set "OUT=out_test"
set "LIB=lib\*"

if exist "%OUT%" rmdir /q /s "%OUT%"
mkdir "%OUT%"

echo Compiling %SRCFILE%...
javac -cp "%LIB%"-d "%OUT%" "%SRCFILE%"

if %ERRORLEVEL% neq O (
    echo Compilion failed.
    pause
    exist /b 1
)
set "CL=%SRCFILE%"
set "CL=%CL:.java=%"
for %%A in ("%CL%") do set "CL=%%~nA"
echo runnig test...
java -cp "%OUT%;%LIB%" %CL%
pause