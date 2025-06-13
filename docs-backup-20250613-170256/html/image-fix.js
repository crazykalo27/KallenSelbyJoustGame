/**
 * Image Loading Fix for Joust Game HTML Deployment
 * This script automatically fixes image loading issues caused by:
 * 1. Case sensitivity in file extensions (.png vs .PNG)
 * 2. Path differences (assets/images/ vs images/)
 */
(function() {
    // Configuration
    const debug = false; // Set to true for console logging
    const rootImagesPath = 'images/';
    const assetsImagesPath = 'assets/images/';

    // Log helper function
    function log(message) {
        if (debug) {
            console.log('[ImageFix] ' + message);
        }
    }

    // Wait for DOM to be ready
    document.addEventListener('DOMContentLoaded', function() {
        log('Initializing image fix script');
        
        // Get all images on the page
        const images = document.querySelectorAll('img');
        log(`Found ${images.length} images to process`);
        
        // Process each image
        images.forEach(function(img) {
            // Store original src for reference
            const originalSrc = img.src;
            
            // Store a data attribute for tracking fixes
            img.setAttribute('data-original-src', originalSrc);
            
            // Add error handler to fix loading issues
            img.addEventListener('error', function() {
                const currentSrc = this.src;
                log(`Error loading image: ${currentSrc}`);
                
                // If this image has already been through all our fixes, don't try again
                if (this.hasAttribute('data-fix-attempted')) {
                    log('Already attempted all fixes for this image, giving up');
                    return;
                }
                
                // Try different fixes in sequence
                
                // 1. Try flipping the case of the extension
                if (currentSrc.toLowerCase().endsWith('.png')) {
                    const basePath = currentSrc.substring(0, currentSrc.length - 4);
                    
                    // If current is .png, try .PNG
                    if (currentSrc.endsWith('.png')) {
                        log('Trying uppercase extension: ' + basePath + '.PNG');
                        this.src = basePath + '.PNG';
                        return;
                    } 
                    // If current is .PNG, try .png
                    else if (currentSrc.endsWith('.PNG')) {
                        log('Trying lowercase extension: ' + basePath + '.png');
                        this.src = basePath + '.png';
                        return;
                    }
                }
                
                // 2. Try switching paths (assets/images/ vs images/)
                const filename = currentSrc.substring(currentSrc.lastIndexOf('/') + 1);
                
                // If using assets path, try root path
                if (currentSrc.includes(assetsImagesPath)) {
                    log(`Trying root path: ${rootImagesPath}${filename}`);
                    this.src = rootImagesPath + filename;
                    return;
                }
                
                // If using root path, try assets path
                if (currentSrc.includes(rootImagesPath)) {
                    log(`Trying assets path: ${assetsImagesPath}${filename}`);
                    this.src = assetsImagesPath + filename;
                    return;
                }
                
                // 3. If still fails, try full GitHub Pages URL
                this.addEventListener('error', function() {
                    if (!this.src.startsWith('http')) {
                        log('Trying full GitHub Pages URL');
                        this.src = 'https://crazykalo27.github.io/KallenSelbyJoustGame/images/' + filename;
                    }
                    // Mark this image as having attempted all fixes
                    this.setAttribute('data-fix-attempted', 'true');
                }, { once: true });
            });
        });
        
        log('Image auto-correction initialized');
    });
})();
