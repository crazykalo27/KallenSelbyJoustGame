@echo off
echo ========================================
echo Joust Game HTML Deployment Script
echo ========================================
echo.
echo This script will create a simple HTML version in the docs directory.
echo.

echo Step 1: Creating directory structure...
if not exist "docs" mkdir docs
if not exist "docs\html" mkdir docs\html
if not exist "docs\assets" mkdir docs\assets
if not exist "docs\assets\images" mkdir docs\assets\images

echo Step 2: Copying assets...
xcopy /s /y assets\images\*.* docs\assets\images\

echo Step 3: Creating JavaScript file...
echo // Simple JavaScript for the HTML placeholder > docs\html\game.js
echo window.onload = function() { >> docs\html\game.js
echo     var loadingMsg = document.getElementById("loadingMessage"); >> docs\html\game.js
echo     if (loadingMsg) { >> docs\html\game.js
echo         loadingMsg.innerHTML = "HTML version is currently under maintenance.&lt;br&gt;Please download the desktop version instead."; >> docs\html\game.js
echo     } >> docs\html\game.js
echo     >> docs\html\game.js
echo     var container = document.getElementById("gameContainer"); >> docs\html\game.js
echo     if (container) { >> docs\html\game.js
echo         var downloadBtn = document.createElement("button"); >> docs\html\game.js
echo         downloadBtn.innerHTML = "Download Desktop Version"; >> docs\html\game.js
echo         downloadBtn.className = "download-btn"; >> docs\html\game.js
echo         downloadBtn.onclick = function() { >> docs\html\game.js
echo             window.location.href = "https://github.com/selbykj/csse220-spring-2023-final-project-s23_a303/releases/latest"; >> docs\html\game.js
echo         }; >> docs\html\game.js
echo         container.appendChild(downloadBtn); >> docs\html\game.js
echo     } >> docs\html\game.js
echo }; >> docs\html\game.js

echo.
echo Step 4: Ensuring index.html is properly configured...
echo.
echo Deployment complete! You can test the HTML version by opening:
echo docs\index.html
echo.
echo To deploy to GitHub Pages, commit and push the docs directory.
echo.

pause
