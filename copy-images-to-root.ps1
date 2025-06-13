# PowerShell script to copy images from assets/images to docs/images directory
# This ensures images are in the root-level path that LibGDX expects

# Define source and destination paths
$sourceDir = "assets/images"
$destDir = "docs/images"

# Ensure destination directory exists
if (!(Test-Path -Path $destDir)) {
    New-Item -ItemType Directory -Force -Path $destDir
    Write-Host "Created directory: $destDir"
}

# Copy all PNG files (both lowercase and uppercase extensions)
Write-Host "Copying image files from $sourceDir to $destDir..."
Copy-Item -Path "$sourceDir/*.png" -Destination $destDir -Force
Copy-Item -Path "$sourceDir/*.PNG" -Destination $destDir -Force

# List the copied files
$copiedFiles = Get-ChildItem -Path $destDir
Write-Host "Successfully copied $($copiedFiles.Count) files to $destDir"
foreach ($file in $copiedFiles) {
    Write-Host "  - $($file.Name)"
}

Write-Host ""
Write-Host "Done! Images are now available at both paths for maximum compatibility."
Write-Host "- Original path: assets/images/*.png"
Write-Host "- LibGDX path: images/*.png"
