// Image extension auto-correction script
window.addEventListener('DOMContentLoaded', function() {
    // Get all images on the page
    const images = document.querySelectorAll('img');
    
    // Apply auto-correction to each image
    images.forEach(img => {
        // Store the original source
        const originalSrc = img.getAttribute('src');
        
        // If the image fails to load, try different extensions
        img.addEventListener('error', function() {
            console.log('Image failed to load:', this.src);
            
            // Try changing extension case
            if (this.src.endsWith('.png')) {
                console.log('Trying uppercase PNG extension');
                this.src = this.src.replace('.png', '.PNG');
            } else if (this.src.endsWith('.PNG')) {
                console.log('Trying lowercase png extension');
                this.src = this.src.replace('.PNG', '.png');
            }
            
            // If still fails, try full URL if it's a relative path
            this.addEventListener('error', function() {
                if (!this.src.startsWith('http') && 
                    (this.src.includes('/assets/') || this.src.includes('\\assets\\'))) {
                    console.log('Trying full GitHub Pages URL');
                    const assetPath = this.src.split(/[\/\\]assets[\/\\]/)[1];
                    if (assetPath) {
                        this.src = 'https://crazykalo27.github.io/KallenSelbyJoustGame/assets/' + assetPath;
                    }
                }
            }, { once: true });
        }, { once: true });
    });
    
    console.log('Image auto-correction applied to', images.length, 'images');
});
