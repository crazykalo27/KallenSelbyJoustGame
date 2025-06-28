/**
 * Platform class for static platforms with different properties
 */
class Platform extends GameObject {
    constructor(xCord, yCord, type) {
        super(xCord, yCord);
        this._isLava = false;
        this._isIce = false;
        this._isSlime = false;
        this._isCool = false;
        this.name = "";
        this.imageCache = new Map();
        
        this.setName(type);
        
        if (this._isLava) {
            this.setHeight(this.getHeight() * 0.6);
        }
    }

    setName(type) {
        switch (type) {
            case 0:
                this.name = "Truss";
                break;
            case 1:
                this.name = "Lava";
                this._isLava = true;
                break;
            case 2:
                this.name = "Ice";
                this._isIce = true;
                break;
            case 3:
                this.name = "Slime";
                this._isSlime = true;
                break;
            case 4:
                this.name = "Health";
                this._isCool = true;
                break;
        }
    }

    drawOn(ctx) {
        const fileName = "Plat" + this.name + ".png"; // Fixed: lowercase extension for GitHub compatibility
        
        if (this.imageCache.has(fileName)) {
            const img = this.imageCache.get(fileName);
            if (img.complete && !img.src.includes('404')) {
                try {
                    ctx.drawImage(
                        img,
                        this.xCent - this.width / 2,
                        this.yCent - this.height / 2,
                        this.width,
                        this.height
                    );
                    return; // Successfully drew image
                } catch (error) {
                    // Image is broken, fall through to colored rectangle
                    console.warn(`Failed to draw platform image ${fileName}, using fallback color`);
                }
            }
        } else {
            // Load image if not cached
            const img = new Image();
            img.onload = () => {
                this.imageCache.set(fileName, img);
            };
            img.onerror = () => {
                // Mark as failed so we don't keep trying
                console.warn(`Failed to load platform image: ${fileName}`);
                this.imageCache.set(fileName, { complete: true, failed: true });
            };
            img.src = `images/${fileName}`;
            this.imageCache.set(fileName, img);
        }
        
        // Draw colored rectangle as fallback
        ctx.fillStyle = this.getColorForType();
        ctx.fillRect(
            this.xCent - this.width / 2,
            this.yCent - this.height / 2,
            this.width,
            this.height
        );
        ctx.strokeStyle = 'black';
        ctx.strokeRect(
            this.xCent - this.width / 2,
            this.yCent - this.height / 2,
            this.width,
            this.height
        );
    }

    getColorForType() {
        if (this._isLava) return '#ff4444';
        if (this._isIce) return '#44ddff';
        if (this._isSlime) return '#44ff44';
        if (this._isCool) return '#ffdd44';
        return '#888888'; // Default gray
    }

    isLava() {
        return this._isLava === true;
    }

    setLava(isLava) {
        this._isLava = isLava;
    }

    isIce() {
        return this._isIce === true;
    }

    isSlime() {
        return this._isSlime === true;
    }

    isCool() {
        return this._isCool === true;
    }

    setCool(isCool) {
        this._isCool = isCool;
    }
    
    // Alternative method name to match Java version
    SetCool(isCool) {
        this._isCool = isCool;
    }
} 