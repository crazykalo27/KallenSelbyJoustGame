/**
 * Platform class for static platforms with different properties
 */
class Platform extends GameObject {
    constructor(xCord, yCord, type) {
        super(xCord, yCord);
        this.isLava = false;
        this.isIce = false;
        this.isSlime = false;
        this.isCool = false;
        this.name = "";
        this.imageCache = new Map();
        
        this.setName(type);
        
        if (this.isLava) {
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
                this.isLava = true;
                break;
            case 2:
                this.name = "Ice";
                this.isIce = true;
                break;
            case 3:
                this.name = "Slime";
                this.isSlime = true;
                break;
            case 4:
                this.name = "Health";
                this.isCool = true;
                break;
        }
    }

    drawOn(ctx) {
        const fileName = "Plat" + this.name + ".PNG";
        
        if (this.imageCache.has(fileName)) {
            const img = this.imageCache.get(fileName);
            if (img.complete) {
                ctx.drawImage(
                    img,
                    this.xCent - this.width / 2,
                    this.yCent - this.height / 2,
                    this.width,
                    this.height
                );
            }
        } else {
            // Load image if not cached
            const img = new Image();
            img.onload = () => {
                this.imageCache.set(fileName, img);
            };
            img.src = `images/${fileName}`;
            this.imageCache.set(fileName, img);
            
            // Draw a colored rectangle as fallback while loading
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
    }

    getColorForType() {
        if (this.isLava) return '#ff4444';
        if (this.isIce) return '#44ddff';
        if (this.isSlime) return '#44ff44';
        if (this.isCool) return '#ffdd44';
        return '#888888'; // Default gray
    }

    isLava() {
        return this.isLava;
    }

    setLava(isLava) {
        this.isLava = isLava;
    }

    isIce() {
        return this.isIce;
    }

    isSlime() {
        return this.isSlime;
    }

    isCool() {
        return this.isCool;
    }

    setCool(isCool) {
        this.isCool = isCool;
    }
} 