@echo off
cd /d "%~dp0"

set "SRC=com"
set "OUT=out"
set "LIB=lib\*"

echo ===== BUILD START =====
echo Source: %SRC%
echo Output: %OUT%
echo Libs: %LIB%
echo.

if not exist "%OUT%" mkdir "%OUT%"

echo Collecting source files...

rem Create a list of all .java files
if exist sources.txt del sources.txt

for /r "%SRC%" %%f in (*.java) do (
    echo %%f>>sources.txt
)

echo Compiling...
javac -cp "%LIB%" -d "%OUT%" @sources.txt

if errorlevel 1 (
    echo  Compilation FAILED
    exit /b 1
)

del sources.txt

echo.
echo  Build complete!
