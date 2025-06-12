@echo off
echo =====================================
echo 🚀 Gradle Bootstrap Setup
echo =====================================

echo.
echo This script will help you set up Gradle for your project.
echo You have several options:
echo.

echo 🟢 RECOMMENDED: Install Gradle globally
echo   Option 1: Using Chocolatey (if you have it)
echo     choco install gradle
echo.
echo   Option 2: Using Scoop (if you have it)  
echo     scoop install gradle
echo.
echo   Option 3: Manual Download
echo     1. Download from: https://gradle.org/releases/
echo     2. Extract to C:\gradle
echo     3. Add C:\gradle\bin to your PATH
echo.

echo 🟡 ALTERNATIVE: Use existing Maven
echo   You can continue using Maven for now:
echo     mvn clean package
echo     copy target\KallenSelbyJoustGame-0.0.1-SNAPSHOT.jar docs\JoustGame.jar
echo.

echo 🔵 QUICK TEST: Use online Gradle wrapper generator
echo   1. Go to: https://gradle-initializr.cleverapps.io/
echo   2. Download wrapper files
echo   3. Extract to this project
echo.

echo =====================================
echo 📋 What would you like to do?
echo =====================================
echo.
echo 1. Install Gradle with Chocolatey
echo 2. Install Gradle with Scoop  
echo 3. Use Maven (existing setup)
echo 4. Manual setup instructions
echo 5. Exit
echo.

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" (
    echo.
    echo Installing Gradle with Chocolatey...
    choco install gradle -y
    if errorlevel 1 (
        echo ❌ Chocolatey not found or installation failed.
        echo Please install Chocolatey first: https://chocolatey.org/install
        pause
        exit /b 1
    )
    echo ✅ Gradle installed! Now run: gradle wrapper
    pause
    exit /b 0
)

if "%choice%"=="2" (
    echo.
    echo Installing Gradle with Scoop...
    scoop install gradle
    if errorlevel 1 (
        echo ❌ Scoop not found or installation failed.
        echo Please install Scoop first: https://scoop.sh/
        pause
        exit /b 1
    )
    echo ✅ Gradle installed! Now run: gradle wrapper
    pause
    exit /b 0
)

if "%choice%"=="3" (
    echo.
    echo Using existing Maven setup...
    echo Building with Maven...
    mvn clean package
    if errorlevel 1 (
        echo ❌ Maven build failed!
        pause
        exit /b 1
    )
    echo.
    echo Copying JAR to docs folder...
    copy target\KallenSelbyJoustGame-0.0.1-SNAPSHOT.jar docs\JoustGame.jar
    echo.
    echo ✅ Build complete! Your game is ready for web deployment.
    echo.
    echo Next steps:
    echo   git add docs\JoustGame.jar
    echo   git commit -m "Update JAR for deployment"
    echo   git push origin main
    echo.
    pause
    exit /b 0
)

if "%choice%"=="4" (
    echo.
    echo Manual Setup Instructions:
    echo =========================
    echo.
    echo 1. Download Gradle 8.5 from:
    echo    https://gradle.org/releases/
    echo.
    echo 2. Extract to C:\gradle
    echo.
    echo 3. Add to PATH environment variable:
    echo    C:\gradle\bin
    echo.
    echo 4. Restart your terminal
    echo.
    echo 5. Run: gradle wrapper
    echo.
    echo 6. Then use: .\gradlew.bat build
    echo.
    pause
    exit /b 0
)

if "%choice%"=="5" (
    echo Exiting...
    exit /b 0
)

echo Invalid choice. Please run the script again.
pause 