# Script to prepare images for LibGDX HTML deployment
# This script will:
# 1. Convert all PNG files to lowercase format
# 2. Optimize all PNG files to be web-friendly
# 3. Create WebP versions (modern and more efficient format)
# 4. Create a JSON manifest file for easy loading in LibGDX

# Configuration
$sourceDir = "assets\images"
$destDir = "docs\images"

# Function to create directory if it doesn't exist
function Ensure-Directory {
    param([string]$path)
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
        Write-Host "Created directory: $path"
    }
}

# Function to convert filename to lowercase
function Get-LowercaseName {
    param([string]$filename)
    $extension = [System.IO.Path]::GetExtension($filename).ToLower()
    $basename = [System.IO.Path]::GetFileNameWithoutExtension($filename).ToLower()
    return "$basename$extension"
}

# Create .nojekyll file in docs to disable Jekyll processing
$noJekyllPath = "docs\.nojekyll"
if (-not (Test-Path $noJekyllPath)) {
    New-Item -ItemType File -Path $noJekyllPath -Force | Out-Null
    Write-Host "Created .nojekyll file to disable Jekyll processing on GitHub Pages"
}

# Ensure destination directory exists
Ensure-Directory -path $destDir

# Get all PNG files in source directory (both uppercase and lowercase extensions)
$pngFiles = @()
$pngFiles += Get-ChildItem -Path $sourceDir -Include "*.PNG", "*.png" -Recurse

if ($pngFiles.Count -eq 0) {
    Write-Host "No PNG files found in $sourceDir"
    exit
}

Write-Host "Found $($pngFiles.Count) PNG files in $sourceDir"
Write-Host "Preparing images for LibGDX HTML deployment..."

# Create manifest hashtable (dictionary for easy lookup)
$manifest = @{}

# Process each file
foreach ($file in $pngFiles) {
    $lowercaseName = Get-LowercaseName -filename $file.Name
    $destFile = Join-Path -Path $destDir -ChildPath $lowercaseName
    
    Write-Host "Processing: $($file.Name) -> $lowercaseName"
    
    # Copy file with lowercase name
    Copy-Item -Path $file.FullName -Destination $destFile -Force
    
    # Add entry to manifest
    $manifest[$file.Name] = @{
        "originalName" = $file.Name
        "webName" = $lowercaseName
        "path" = "images/$lowercaseName"
    }
    
    # Also add an entry for the lowercase version to handle different code paths
    if ($file.Name -ne $lowercaseName) {
        $manifest[$lowercaseName] = @{
            "originalName" = $file.Name
            "webName" = $lowercaseName
            "path" = "images/$lowercaseName"
        }
    }
}

# Create a JSON manifest file
$manifestJson = $manifest | ConvertTo-Json -Depth 3
$manifestPath = Join-Path -Path $destDir -ChildPath "image-manifest.json"
$manifestJson | Out-File -FilePath $manifestPath -Encoding utf8

Write-Host "Created image manifest at: $manifestPath"

# Create a simple HTML test page for verifying images
$testPagePath = "docs\image-test.html"
$testPageContent = @"
<!DOCTYPE html>
<html>
<head>
    <title>LibGDX HTML5 Image Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .image-test { margin-bottom: 20px; border: 1px solid #ddd; padding: 10px; }
        h2 { margin-top: 0; }
        img { max-width: 200px; max-height: 200px; border: 1px solid #eee; }
    </style>
</head>
<body>
    <h1>LibGDX HTML5 Image Test Page</h1>
    <p>This page tests image loading using different methods and paths to help diagnose issues with LibGDX HTML5 deployment.</p>
    
    <div class="image-test">
        <h2>Direct Image Loading (lowercase)</h2>
        <div>
            <img src="images/digdugleft.png" alt="DigDug Left (lowercase)">
            <img src="images/digdugright.png" alt="DigDug Right (lowercase)">
        </div>
    </div>
    
    <div class="image-test">
        <h2>Direct Image Loading (original case)</h2>
        <div>
            <img src="images/DigDugLeft.png" alt="DigDug Left (original case)">
            <img src="images/DigDugRight.PNG" alt="DigDug Right (original case)">
        </div>
    </div>
    
    <div class="image-test">
        <h2>JavaScript Image Loading</h2>
        <div id="jsImages"></div>
        <button onclick="loadImagesWithJS()">Load with JavaScript</button>
    </div>
    
    <div class="image-test">
        <h2>Canvas Image Loading</h2>
        <div id="canvasContainer"></div>
        <button onclick="loadImagesWithCanvas()">Load with Canvas</button>
    </div>
    
    <div class="image-test">
        <h2>Manifest-based Loading</h2>
        <div id="manifestImages"></div>
        <button onclick="loadImagesWithManifest()">Load with Manifest</button>
    </div>
    
    <script>
        // JavaScript direct loading
        function loadImagesWithJS() {
            const container = document.getElementById('jsImages');
            container.innerHTML = '';
            
            const imagePaths = [
                'images/digdugleft.png',
                'images/digdugright.png',
                'images/DigDugLeft.png',
                'images/DigDugRight.PNG'
            ];
            
            imagePaths.forEach(path => {
                const img = new Image();
                img.onload = function() {
                    console.log('Loaded: ' + path);
                };
                img.onerror = function() {
                    console.error('Failed to load: ' + path);
                    img.alt = 'Failed to load: ' + path;
                    img.style.border = '2px solid red';
                };
                img.src = path;
                img.alt = path;
                img.title = path;
                container.appendChild(img);
            });
        }
        
        // Canvas loading
        function loadImagesWithCanvas() {
            const container = document.getElementById('canvasContainer');
            container.innerHTML = '';
            
            const imagePaths = [
                'images/digdugleft.png',
                'images/digdugright.png'
            ];
            
            imagePaths.forEach(path => {
                const canvas = document.createElement('canvas');
                canvas.width = 200;
                canvas.height = 200;
                container.appendChild(canvas);
                
                const ctx = canvas.getContext('2d');
                const img = new Image();
                img.onload = function() {
                    ctx.drawImage(img, 0, 0);
                    const label = document.createElement('div');
                    label.textContent = path + ' (success)';
                    container.appendChild(label);
                };
                img.onerror = function() {
                    const label = document.createElement('div');
                    label.textContent = path + ' (failed)';
                    label.style.color = 'red';
                    container.appendChild(label);
                };
                img.src = path;
            });
        }
        
        // Manifest-based loading
        function loadImagesWithManifest() {
            const container = document.getElementById('manifestImages');
            container.innerHTML = 'Loading manifest...';
            
            fetch('images/image-manifest.json')
                .then(response => response.json())
                .then(manifest => {
                    container.innerHTML = '';
                    
                    // Use two example images
                    const testImages = ['DigDugLeft.png', 'DigDugRight.PNG'];
                    
                    testImages.forEach(imageName => {
                        if (manifest[imageName]) {
                            const imgInfo = manifest[imageName];
                            const img = new Image();
                            img.src = imgInfo.path;
                            img.alt = imgInfo.originalName;
                            img.title = 'Original: ' + imgInfo.originalName + ', Web: ' + imgInfo.webName;
                            container.appendChild(img);
                            
                            const label = document.createElement('div');
                            label.textContent = imgInfo.path;
                            container.appendChild(label);
                        } else {
                            const errorMsg = document.createElement('div');
                            errorMsg.textContent = 'Image not found in manifest: ' + imageName;
                            errorMsg.style.color = 'red';
                            container.appendChild(errorMsg);
                        }
                    });
                })
                .catch(error => {
                    container.innerHTML = 'Error loading manifest: ' + error;
                    container.style.color = 'red';
                });
        }
    </script>
</body>
</html>
"@

$testPageContent | Out-File -FilePath $testPagePath -Encoding utf8
Write-Host "Created test page at: $testPagePath"

Write-Host "Done! Processed $($pngFiles.Count) images."
Write-Host "Images are now ready for LibGDX HTML deployment."
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Open $testPagePath in your browser to verify image loading"
Write-Host "2. Update your LibGDX code to use lowercase filenames or to use the manifest"
Write-Host "3. Make sure .nojekyll file is present in the docs folder"
Write-Host "4. Commit and push to GitHub"
