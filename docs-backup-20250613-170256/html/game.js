
// Simple JavaScript for the HTML placeholder
window.onload = function() {
    var loadingMsg = document.getElementById("loadingMessage");
    if (loadingMsg) {
        loadingMsg.innerHTML = "HTML version is currently under maintenance.<br>Please download the desktop version instead.";
    }
    
    var container = document.getElementById("gameContainer");
    if (container) {
        var downloadBtn = document.createElement("button");
        downloadBtn.innerHTML = "Download Desktop Version";
        downloadBtn.className = "download-btn";
        downloadBtn.onclick = function() {
            window.location.href = "https://github.com/selbykj/csse220-spring-2023-final-project-s23_a303/releases/latest";
        };
        container.appendChild(downloadBtn);
    }
};
