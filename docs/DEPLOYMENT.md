# Joust Game - LibGDX Web Deployment Guide

## Overview
This document outlines the deployment process for the Joust Game web version using libGDX and GWT.

## Prerequisites
- Java 8 JDK
- Gradle 8.5
- Git

## Build Process

### 1. Build the Web Version
```bash
./gradlew html:dist
```
This will:
- Compile the Java code to JavaScript using GWT
- Package all assets
- Create a web-ready distribution in `html/build/dist/`

### 2. Deploy to GitHub Pages
1. Copy the distribution files to the docs folder:
```bash
cp -r html/build/dist/* docs/
```

2. Commit and push to GitHub:
```bash
git add docs/
git commit -m "Update web deployment"
git push origin main
```

3. Enable GitHub Pages:
- Go to repository settings
- Under "GitHub Pages", select "main" branch and "/docs" folder
- Save the settings

## Web Deployment Structure
```
docs/
├── index.html          # Game launcher page
├── styles.css          # Web styling
├── .nojekyll           # GitHub Pages compatibility
├── html/              # Compiled JavaScript game
│   ├── html.nocache.js
│   └── gwt/           # GWT runtime files
└── assets/            # Game assets
    ├── images/        # All game sprites
    └── levels/        # All game levels
```

## Asset Management
- All game assets are stored in the `assets/` directory
- Assets are automatically copied during the build process
- The `assets.txt` file lists all required assets for GWT preloading

## Browser Compatibility
- Chrome (recommended)
- Firefox
- Safari
- Edge

## Troubleshooting

### Common Issues
1. **Game not loading**
   - Check browser console for errors
   - Verify all assets are present in docs/assets/
   - Ensure GitHub Pages is enabled

2. **Missing assets**
   - Verify assets.txt is present and up to date
   - Check asset paths in GameAssetManager.java

3. **Performance issues**
   - Clear browser cache
   - Try a different browser
   - Check for background processes

## Maintenance
- Regularly update dependencies
- Test on multiple browsers
- Monitor GitHub Pages deployment status 