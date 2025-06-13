# Script to forcibly convert all image filenames to lowercase
# This will actually rename the files, not just copy them

# Define paths
$imagesDir = "docs\images"

# Ensure the directory exists
if (-not (Test-Path $imagesDir)) {
    Write-Host "Directory not found: $imagesDir" -ForegroundColor Red
    exit
}

# Get all files
$files = Get-ChildItem -Path $imagesDir -File

Write-Host "Found $($files.Count) files in $imagesDir" -ForegroundColor Yellow

# Convert each file to lowercase
foreach ($file in $files) {
    # Skip the manifest file
    if ($file.Name -eq "image-manifest.json") {
        continue
    }
    
    # Generate lowercase name
    $lowerName = $file.Name.ToLower()
    
    # Check if the name contains uppercase characters
    if ($file.Name -cmatch "[A-Z]" -or $file.Name -cmatch "\.PNG$") {
        # Create temporary name to avoid case-insensitive filesystem issues
        $tempPath = Join-Path -Path $file.Directory.FullName -ChildPath "temp_$($file.Name)"
        $lowerPath = Join-Path -Path $file.Directory.FullName -ChildPath $lowerName
        
        Write-Host "Renaming: $($file.Name) -> $lowerName" -ForegroundColor Yellow
        
        try {
            # Use two-step renaming to avoid case-sensitivity issues
            Copy-Item -Path $file.FullName -Destination $tempPath -Force
            Remove-Item -Path $file.FullName -Force
            Rename-Item -Path $tempPath -NewName $lowerName -Force
        } catch {
            Write-Host "Error renaming file: $_" -ForegroundColor Red
        }
    } else {
        Write-Host "Already lowercase: $($file.Name)" -ForegroundColor Green
    }
}

Write-Host "All files converted to lowercase." -ForegroundColor Green
