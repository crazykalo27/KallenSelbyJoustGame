
// JavaScript for the HTML placeholder
window.onload = function() {
    // Update the download button functionality
    var downloadBtn = document.getElementById("downloadButton");
    if (downloadBtn) {
        downloadBtn.onclick = function() {
            window.location.href = "https://github.com/selbykj/csse220-spring-2023-final-project-s23_a303/releases/latest";
        };
    }
    
    // Add animation to asset cards
    var assetCards = document.querySelectorAll('.asset-card');
    assetCards.forEach(function(card, index) {
        setTimeout(function() {
            card.style.opacity = 0;
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            
            setTimeout(function() {
                card.style.opacity = 1;
                card.style.transform = 'translateY(0)';
            }, 100);
        }, index * 150);
    });
    
    // Add simple hover animation for platform tiles
    var platformTiles = document.querySelectorAll('.platform-tile');
    platformTiles.forEach(function(tile) {
        tile.addEventListener('mouseover', function() {
            this.style.boxShadow = '0 0 15px rgba(255, 204, 0, 0.7)';
        });
        
        tile.addEventListener('mouseout', function() {
            this.style.boxShadow = 'none';
        });
    });
};
