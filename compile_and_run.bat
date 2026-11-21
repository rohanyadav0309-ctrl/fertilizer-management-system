@echo off
REM ========================================================
REM Fertilizer Management System - Compile and Run Batch Script
REM Second-Year Java Practical Project
REM ========================================================

echo =====================================================
echo    Fertilizer Management System - Build & Run
echo =====================================================
echo.

REM Check if javac is available
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] 'javac' is not found in your system PATH!
    echo Please install Java JDK or add JDK\bin to your System PATH environment variable.
    echo.
    pause
    exit /b
)

REM Look for MySQL connector jar in the current directory or lib folder
set CLASSPATH_VAL=.
for %%f in (*mysql*.jar lib\*mysql*.jar) do (
    set CLASSPATH_VAL=.;%%f
)

echo Using Classpath: %CLASSPATH_VAL%
echo.

echo Step 1: Compiling Java source files...
javac -cp "%CLASSPATH_VAL%" *.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Check the error messages above.
    pause
    exit /b
)

echo [SUCCESS] Compilation completed successfully!
echo.
echo Step 2: Launching Fertilizer Management System...
java -cp "%CLASSPATH_VAL%" Main

pause
