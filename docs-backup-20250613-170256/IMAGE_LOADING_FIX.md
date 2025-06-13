# Image Loading Fix for Joust Game HTML Deployment

## Problem

When deploying the Joust Game to GitHub Pages, we encountered issues with images not loading properly. The primary causes were:

1. **Case sensitivity in file extensions**: GitHub Pages is hosted on Linux servers, which treat `.png` and `.PNG` as different file extensions. In Windows development environments, these are treated as the same, leading to inconsistencies.

2. **Path resolution**: The LibGDX code in `GameAssetManager.java` loads images from a root-level `images/` directory (e.g., `images/DigDugRight.PNG`), but our HTML deployment was placing images in `assets/images/`.

## Complete Solution

After extensive testing, we've implemented a comprehensive fix:

1. **Dual Path Support**: Images are now copied to both locations:
   - `docs/assets/images/` (for backward compatibility)
   - `docs/images/` (for LibGDX compatibility)

2. **Updated HTML**: All image references in `index.html` now use the root-level path (`images/filename.png`) to match LibGDX's expectations.

3. **Updated Deployment Scripts**: Both the batch file and Gradle scripts now automatically copy images to both locations.

4. **JavaScript Fallback**: We've retained the JavaScript-based path correction as a secondary safety mechanism.

## Solution Files

We created several files to diagnose and fix the issue:

1. **image-test.html**: A comprehensive test page that tries different methods of loading images to identify which approach works best. This page tests:
   - Loading with lowercase extensions (.png)
   - Loading with uppercase extensions (.PNG)
   - Loading with full GitHub Pages URLs
   - Testing automatic extension correction

2. **auto-fixed.html**: A version of the main page that implements JavaScript-based fixes:
   - Automatically tries different case variations of extensions
   - Falls back to full GitHub Pages URLs if needed
   - Shows visual indicators of image loading status

3. **libgdx-test.html**: Tests loading images using the same paths that LibGDX uses.

4. **path-test.html**: Tests all possible path combinations and provides a solution button.

5. **html/image-fix.js**: A reusable script that can be included in any HTML page to automatically correct image extensions.

6. **copy-images-to-root.ps1**: A PowerShell script that copies images to the root-level directory.

## How to Use

1. **Standard Usage**: The main `index.html` has been updated to use root-level image paths and includes the `image-fix.js` script as a fallback.

2. **For Testing**: Use any of the test pages to verify image loading:
   - `image-test.html` - Tests various path and extension combinations
   - `libgdx-test.html` - Tests the LibGDX-style image paths
   - `path-test.html` - Tests all path approaches with detailed feedback

3. **For Development**: When making changes to the project:
   - Use the updated deployment scripts which copy images to both locations
   - Or manually run `copy-images-to-root.ps1` to copy images

## Technical Explanation

The core issues were:

1. **Case Sensitivity**: 
   - **Windows**: File systems are case-insensitive, so `image.PNG` and `image.png` are considered the same file.
   - **Linux/Unix** (including GitHub Pages servers): File systems are case-sensitive, so `image.PNG` and `image.png` are considered different files.

2. **LibGDX Path Expectations**:
   - LibGDX's `GameAssetManager.java` loads images from the root level (`images/filename.png`)
   - Our HTML deployment was placing images in `assets/images/filename.png`

The solution ensures images are available at both paths, with all HTML references updated to use the LibGDX-compatible root path.

## LibGDX Asset Loading Code

In `GameAssetManager.java`, images are loaded with root-level paths:

```java
queueTexture("hero_right", "images/DigDugRight.PNG");
queueTexture("platform_truss", "images/PlatTruss.PNG");
queueTexture("egg", "images/Egg.png");
```

This explains why the images needed to be at the root level for proper loading.
