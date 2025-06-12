@echo off
echo =====================================
echo 🧪 Testing Gradle Build System
echo =====================================

echo.
echo 🔍 Checking Gradle wrapper...
if not exist "gradlew.bat" (
    echo ❌ Gradle wrapper not found! Please run: gradle wrapper
    pause
    exit /b 1
)

echo ✅ Gradle wrapper found

echo.
echo 📋 Running build tests...
echo.

echo 1️⃣ Testing clean...
call gradlew.bat clean
if errorlevel 1 goto :error

echo.
echo 2️⃣ Testing compile...
call gradlew.bat compileJava
if errorlevel 1 goto :error

echo.
echo 3️⃣ Testing resource copying...
call gradlew.bat copyResources
if errorlevel 1 goto :error

echo.
echo 4️⃣ Testing JAR creation...
call gradlew.bat jar
if errorlevel 1 goto :error

echo.
echo 5️⃣ Testing full build...
call gradlew.bat build
if errorlevel 1 goto :error

echo.
echo 6️⃣ Testing web deployment preparation...
call gradlew.bat prepareWebDeploy
if errorlevel 1 goto :error

echo.
echo =====================================
echo ✅ All tests passed successfully! 
echo =====================================
echo.
echo 📦 JAR file: build\libs\KallenSelbyJoustGame-0.0.1-SNAPSHOT.jar
echo 🌐 Web files: docs\
echo.
echo 🎮 To run the game locally:
echo    gradlew runGame
echo.
echo 🚀 To deploy to web:
echo    gradlew deployToGitHubPages
echo.
pause
exit /b 0

:error
echo.
echo ❌ Build test failed!
echo.
pause
exit /b 1 