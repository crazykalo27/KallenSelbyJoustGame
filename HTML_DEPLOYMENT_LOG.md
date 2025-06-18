# HTML Deployment Log

## 2023-06-XX - Manual assets.txt Implementation

We've updated our HTML deployment process to use a manually created assets.txt file instead of generating one dynamically. This change was made to ensure proper asset loading in the GWT environment.

The assets.txt file format follows the GWT PreloaderBundleGenerator specification, where each line contains exactly six tokens separated by colons:

```
i:images/BlueKoopaLeft.PNG:images/BlueKoopaLeft.PNG:0:PNG:0
```

The six tokens represent:
1. Type code: 'i' for image or 'd' for data
2. Primary path (passed to M6e(f))
3. Secondary path (passed to M6e(e))
4. Integer parameter (e.g., mipmap levels)
5. String parameter (file type like PNG or TXT)
6. Preload flag (1 to preload, 0 to defer loading)

This format ensures the GWT preloader correctly processes each asset without throwing the "Invalid assets description file" exception. The fk function in the GWT code checks for exactly six tokens per line.

Changes made:
- Removed the generateAssetsList task from html/build.gradle
- Updated the copyAssets task to exclude overwriting the manually created assets.txt
- Modified task dependencies to use copyAssets directly instead of generateAssetsList

References:
- GWT PreloaderBundleGenerator source: https://github.com/libgdx/libgdx/blob/master/backends/gdx-backends-gwt/src/com/badlogic/gdx/backends/gwt/preloader/PreloaderBundleGenerator.java
- LibGDX HTML5-backend guide: https://libgdx.com/wiki/deployment/deploying-your-application#html5
