# LibGDX Image Loading Fix for Joust Game HTML Deployment

## Problem Summary

The HTML deployment of the Joust Game was encountering image loading issues when deployed to GitHub Pages. After extensive testing and code examination, we identified two main causes:

1. **Path Mismatch**: The LibGDX code in `GameAssetManager.java` loads images from a root-level `images/` directory (e.g., `images/DigDugRight.PNG`), but our HTML deployment was placing images in `assets/images/`.

2. **Case Sensitivity**: GitHub Pages is hosted on Linux servers which are case-sensitive, meaning `image.png` and `image.PNG` are treated as different files. This differs from Windows development environments where case is ignored.

## Solution Implemented

We implemented a comprehensive fix with the following components:

1. **Dual Path Support**: Images are now copied to both locations:
   - `docs/assets/images/` (for backward compatibility)
   - `docs/images/` (for LibGDX compatibility)

2. **Updated Deployment Scripts**:
   - Modified `deploy-html.bat` to create both directories and copy images to both paths
   - Updated Gradle `deployHtml` task to do the same
   - Created a standalone `copy-images-to-root.ps1` PowerShell script that can be run manually

3. **HTML Path Updates**:
   - Updated all image references in `index.html` to use the root-level path (`images/filename.png`)

4. **Retained Image Fix Script**:
   - Kept the JavaScript-based path correction script (`image-fix.js`) as a fallback

## Technical Details

### LibGDX Asset Loading Logic

In the `GameAssetManager.java` file, all images are queued for loading with paths like:

```java
queueTexture("hero_right", "images/DigDugRight.PNG");
queueTexture("platform_truss", "images/PlatTruss.PNG");
queueTexture("egg", "images/Egg.png");
```

When the GWT compiler processes these paths for HTML deployment, it expects image files to be located at the root level (e.g., `images/DigDugRight.PNG`), not inside the `assets` directory.

### Key Testing Results

Through the various test pages we created, we confirmed:

1. Images loaded correctly when using the root path: `images/DigDugRight.PNG`
2. Images failed to load when using the assets path: `assets/images/DigDugRight.PNG`
3. Case sensitivity matters - `.PNG` and `.png` must match exactly on GitHub Pages

## Deployment Instructions

To ensure proper image loading in future deployments:

1. **Automated Method**: Use the updated deployment scripts:
   - Windows: Run `deploy-html.bat`
   - Gradle: Run `./gradlew deployHtml`

2. **Manual Method**: If needed, run the standalone PowerShell script:
   ```
   .\copy-images-to-root.ps1
   ```

3. **Verification**: After deployment, test the main page and image test pages to verify:
   - `index.html` - All game assets should be visible
   - `image-test.html` - All test images should load successfully
   - `libgdx-test.html` - LibGDX-style paths should work

## Future Considerations

1. **Full GWT Deployment**: If full LibGDX HTML deployment with GWT is implemented:
   - Make sure to maintain the root-level `images/` directory
   - Set the correct asset path in the GWT module XML files
   - Configure the build to copy assets to the correct location

2. **Case Sensitivity**: Continue using the exact case for file extensions as defined in the codebase, or consider standardizing all extensions to either lowercase or uppercase.

3. **Build Integration**: The dual path approach ensures maximum compatibility with both the HTML placeholder and any future fully functional HTML/GWT deployment.
