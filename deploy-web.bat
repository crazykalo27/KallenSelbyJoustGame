@echo off
echo ================================
echo    JOUST GAME WEB DEPLOYMENT
echo ================================

echo.
echo Building desktop JAR...
call gradlew desktop:build

echo.
echo Creating web deployment folder...
if not exist "docs" mkdir docs
if not exist "docs\game" mkdir docs\game

echo.
echo Copying game files...
copy "desktop\build\libs\JoustGameLibGDX-desktop.jar" "docs\game\JoustGame.jar"
copy "assets\*" "docs\game\" /E /Y

echo.
echo Creating web launcher HTML...
echo ^<!DOCTYPE html^> > docs\index.html
echo ^<html^> >> docs\index.html
echo ^<head^> >> docs\index.html
echo     ^<title^>Joust Game - LibGDX Version^</title^> >> docs\index.html
echo     ^<style^> >> docs\index.html
echo         body { background-color: #222; color: white; font-family: Arial; text-align: center; } >> docs\index.html
echo         .container { max-width: 800px; margin: 0 auto; padding: 20px; } >> docs\index.html
echo         .download-btn { background: #4CAF50; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 10px; } >> docs\index.html
echo     ^</style^> >> docs\index.html
echo ^</head^> >> docs\index.html
echo ^<body^> >> docs\index.html
echo     ^<div class="container"^> >> docs\index.html
echo         ^<h1^>🎮 Joust Game - LibGDX Version^</h1^> >> docs\index.html
echo         ^<p^>Classic arcade-style game converted from Swing to LibGDX^</p^> >> docs\index.html
echo         ^<p^>✅ All level loading issues fixed!^</p^> >> docs\index.html
echo         ^<p^>🎯 Fight enemies, collect eggs, progress through 13 levels^</p^> >> docs\index.html
echo         ^<a href="game/JoustGame.jar" class="download-btn"^>📥 Download Game (JAR)^</a^> >> docs\index.html
echo         ^<p^>^<small^>Requires Java 8+ to run^</small^>^</p^> >> docs\index.html
echo         ^<h3^>Controls:^</h3^> >> docs\index.html
echo         ^<p^>Arrow Keys: Move • Up Arrow: Flap/Fly^</p^> >> docs\index.html
echo         ^<h3^>Game Status:^</h3^> >> docs\index.html
echo         ^<p^>✅ Desktop Version: Working^<br^>⚠️ Web Version: In Development^</p^> >> docs\index.html
echo     ^</div^> >> docs\index.html
echo ^</body^> >> docs\index.html
echo ^</html^> >> docs\index.html

echo.
echo ================================
echo ✅ WEB DEPLOYMENT COMPLETE!
echo ================================
echo.
echo Next steps:
echo 1. git add docs/
echo 2. git commit -m "Add web deployment"
echo 3. git push origin main
echo 4. Enable GitHub Pages in repository settings
echo.
echo Game will be available at:
echo https://[username].github.io/[repo-name]/
echo.
pause 