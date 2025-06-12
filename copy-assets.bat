@echo off
echo Copying game assets...

:: Create assets directory if it doesn't exist
if not exist "assets" mkdir assets
if not exist "assets\images" mkdir assets\images
if not exist "assets\levels" mkdir assets\levels

:: Copy images
xcopy /Y /E "src\main\resources\images\*" "assets\images\"

:: Copy levels
xcopy /Y /E "src\main\resources\levels\*" "assets\levels\"

echo Assets copied successfully!
pause 