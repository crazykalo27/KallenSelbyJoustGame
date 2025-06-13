$sourceDir = "assets\images"
$destDir = "docs\images"

# Ensure destination directory exists
if (-not (Test-Path $destDir)) {
    New-Item -ItemType Directory -Path $destDir -Force
}

# Copy all PNG files (maintaining uppercase extension)
Write-Host "Copying PNG files to $destDir..."
Copy-Item "$sourceDir\*.PNG" -Destination $destDir -Force

# Create lowercase versions of the PNG files
Write-Host "Creating lowercase versions of PNG files..."
$pngFiles = Get-ChildItem -Path $sourceDir -Filter "*.PNG"
foreach ($file in $pngFiles) {
    $lowercaseName = $file.Name.ToLower()
    Copy-Item $file.FullName -Destination "$destDir\$lowercaseName" -Force
}

Write-Host "Done!"
