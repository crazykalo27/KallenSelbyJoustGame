# HTML Deployment for Joust Game

## Current Approach

Due to challenges with the GWT compiler for HTML deployment, we've implemented a simplified approach that:

1. Creates a placeholder HTML page that showcases the game
2. Provides screenshots and information about the game
3. Offers a download link to the desktop version
4. Avoids the complexity and issues of GWT compilation

## How to Deploy

Running the `deploy-html.bat` script will:
1. Create the necessary directory structure in the `docs` folder
2. Copy image assets from the main project
3. Generate a JavaScript file for basic interactivity
4. Set up the HTML page with proper styling

After running the script, the contents of the `docs` directory can be committed and pushed to GitHub, which will automatically publish to GitHub Pages.

## Why This Approach?

The GWT compilation process for LibGDX projects can be challenging, especially with:
- Dependency management
- Platform-specific code that's not GWT compatible
- Asset handling
- File permission issues on Windows

By using a simplified placeholder approach, we:
- Maintain a professional web presence
- Avoid build failures
- Provide a clear path for users to access the desktop version
- Keep deployment simple and reliable

## Future Improvements

To implement a full HTML version in the future:
1. Create GWT-compatible alternatives for any incompatible code
2. Add conditional compilation with `GWT.isClient()` checks
3. Refactor the asset loading process to be GWT-friendly
4. Consider using a more modern HTML5/WebGL approach like PlayN or TeaVM
