# PowerShell script to create a minimal test environment in the docs folder
# This keeps only a single test image and a minimal HTML file

# Define paths
$docsDir = "docs"
$imageDir = "docs\images"
$testImage = "docs\images\DigDugRight.PNG"
$testHtml = "docs\minimal-test.html"

# Create a backup directory
$backupDir = "docs-backup-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
Write-Host "Creating backup in $backupDir..."
Copy-Item -Path $docsDir -Destination $backupDir -Recurse

# Clean up the docs directory while preserving the image directory and test image
Write-Host "Cleaning docs directory..."
Get-ChildItem -Path $docsDir -Exclude "images", "minimal-test.html" | Remove-Item -Recurse -Force

# Clean up the images directory, keeping only the test image
Write-Host "Cleaning images directory..."
Get-ChildItem -Path $imageDir | Where-Object { $_.FullName -ne (Resolve-Path $testImage).Path } | Remove-Item -Force

Write-Host "Done! The docs directory now contains:"
Get-ChildItem -Path $docsDir -Recurse | Format-Table -Property FullName

Write-Host "A backup of the original docs directory is available in: $backupDir"
Write-Host "You can view the minimal test page at: docs/minimal-test.html"
