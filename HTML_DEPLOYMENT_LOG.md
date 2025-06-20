# HTML Deployment Log

## Status: 🔧 DEBUGGING - Directory Structure Issue Fixed

### Latest Issue & Resolution (Current)
**Problem**: Game loads GWT files but hangs on "Initializing..." indefinitely
**Root Cause Discovered**: GWT module configuration mismatch
- `build.gradle` compiles module as `com.joust.JoustGame` 
- `JoustGame.gwt.xml` specifies `rename-to="html"`
- This creates inconsistent internal module references

**Solution Applied**:
1. **Added Debug Logging**: Enhanced index.html with comprehensive debug output
2. **Fixed Directory Structure**: Created `/html/` directory to match `rename-to="html"`
3. **Updated Entry Point**: Changed script src from `./com.joust.JoustGame/com.joust.JoustGame.nocache.js` to `./html/com.joust.JoustGame.nocache.js`

### Testing Status
**Current Test URL**: https://crazykalo27.github.io/KallenSelbyJoustGame/
- ✅ GWT nocache.js loads without 404 errors
- ✅ GWT cache.js files load without 404 errors  
- ⏳ Testing if directory structure fix resolves initialization hang
- 📊 Debug log should show detailed initialization progress

### Debug Information Available
The current deployment includes:
- **Debug Console**: Bottom-left corner shows real-time loading progress
- **Error Capture**: Captures all console.log, console.error, and game errors
- **Timeout Detection**: 30-second timeout with detailed reporting

### Project Structure Diagnosis
```
docs/
├── index.html (✅ With debug logging)
├── html/ (✅ NEW - Matches rename-to="html")
│   ├── com.joust.JoustGame.nocache.js (✅ Entry point)
│   ├── *.cache.js (✅ All 5 browser-specific files)
│   └── gwt/ (✅ CSS and chrome assets)
├── com.joust.JoustGame/ (⚠️ OLD - May be redundant now)
├── assets/ (✅ Game assets for runtime)
└── WEB-INF/ (✅ Symbol maps)
```

---

## Previous Issues Resolved

### Issue Resolution (Previous)
**Problem**: GitHub Pages was returning 404 errors for GWT cache.js files
**Root Cause**: The `.gitignore` file contained `*.cache.js` which excluded all GWT-compiled cache files from being committed to the repository
**Solution**: 
1. Used `git add -f` to force-add all 5 required cache.js files:
   - `6D08E884C3A5CEF2B457C3D1A9E6FB67.cache.js` (Safari)
   - `DCF0BF2152A73144582EEE70281697B7.cache.js` (Gecko1_8)  
   - `3546F4292AD633DB8B25627177E7E638.cache.js` (IE10)
   - `1529D80E2699CAA2E171D26FE356841D.cache.js` (IE8)
   - `0F7928414E13583BFD782A97E9516728.cache.js` (IE9)
2. Updated `index.html` with proper error handling
3. Committed and pushed changes to GitHub

### Key Changes Made
1. **Fixed Missing Files**: Added all GWT cache.js files that were excluded by gitignore
2. **Updated Entry Point**: Clean index.html with proper error handling  
3. **Added Debug Support**: Comprehensive logging for initialization troubleshooting
4. **Fixed Directory Structure**: Aligned deployment structure with GWT configuration

### Future Maintenance
- ✅ **Fixed .gitignore**: Updated to allow cache.js files in docs/ but exclude them from build directories
- If you recompile the GWT code, cache.js files in docs/ will now be tracked automatically
- No more need to use `git add -f` for deployment files
- Monitor debug log output to identify any remaining initialization issues

---

## Build Configuration
- **Framework**: LibGDX with GWT backend
- **Module**: `com.joust.JoustGame` with `rename-to="html"`
- **Deployment**: GitHub Pages from `/docs` folder
- **Assets**: Located in `docs/assets/`
- **Debug**: Enabled in current deployment

### Compilation Process
1. GWT compiles Java source to JavaScript
2. Creates browser-specific cache.js files 
3. Generates nocache.js entry point with module name
4. Copies assets to deployment directory
5. Applies rename-to for directory structure

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
