@echo off
echo =====================================
echo 🚀 Deploying Joust Game to Web
echo =====================================

echo.
echo 📦 Building project with Gradle...
call gradlew.bat clean build

if errorlevel 1 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo 🌐 Preparing web deployment...
call gradlew.bat deployToGitHubPages

if errorlevel 1 (
    echo ❌ Web deployment preparation failed!
    pause
    exit /b 1
)

echo.
echo ✅ Deployment preparation complete!
echo.
echo 📋 Next steps:
echo    1. git add docs/
echo    2. git commit -m "Update web deployment"
echo    3. git push origin main
echo.
echo 🌐 Your game will be available at:
echo    https://[your-username].github.io/[repository-name]/
echo.
pause 