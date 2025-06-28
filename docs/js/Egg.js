/**
 * Egg class - spawned when enemies die
 */
class Egg extends GameObject {
    constructor(xCent, yCent, containedEnemy, imageName) {
        super(xCent, yCent);
        this.containedEnemy = containedEnemy;
        this.imageName = imageName;
        this.imageCache = new Map();
        this.respawnTimer = 0;
        this.respawnDelay = 420; // 7 seconds at 60fps
    }

    update() {
        super.update();
        this.respawnTimer++;
        
        // Mark for removal when timer expires (will be handled by game engine)
        if (this.respawnTimer >= this.respawnDelay) {
            this.markForRemoval();
        }
    }

    drawOn(ctx) {
        const fileName = this.imageName + ".png";
        
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
            
            // Draw a simple egg shape as fallback
            ctx.fillStyle = '#ffffcc';
            ctx.beginPath();
            ctx.ellipse(
                this.xCent,
                this.yCent,
                this.width / 2,
                this.height / 2 * 1.2,
                0,
                0,
                2 * Math.PI
            );
            ctx.fill();
            ctx.strokeStyle = '#666666';
            ctx.stroke();
        }
    }

    getContainedEnemy() {
        return this.containedEnemy;
    }

    getRemainingTime() {
        return Math.max(0, this.respawnDelay - this.respawnTimer);
    }

    isReadyToRespawn() {
        return this.respawnTimer >= this.respawnDelay;
    }

    collidewith(other) {
        // Basic collision for platforms - eggs should bounce/rest on them
        if (other instanceof Platform) {
            const previousXDist = other.getXCent() - this.xCent;
            const previousYDist = other.getYCent() - this.yCent;
            
            const rectH = this.getBoundingBox();
            const rectO = other.getBoundingBox();
            
            // Calculate overlap
            const overlapLeft = Math.max(rectH.x, rectO.x);
            const overlapRight = Math.min(rectH.x + rectH.width, rectO.x + rectO.width);
            const overlapTop = Math.max(rectH.y, rectO.y);
            const overlapBottom = Math.min(rectH.y + rectH.height, rectO.y + rectO.height);
            
            const overlapWidth = overlapRight - overlapLeft;
            const overlapHeight = overlapBottom - overlapTop;

            if (Math.abs(previousYDist) < Math.abs(previousXDist)) {
                const direction = Math.sign(other.getXCent() - this.getXCent());
                this.setXCent(this.getXCent() - direction * overlapWidth);
            } else {
                this.setYCent(this.getYCent() - Math.sign(previousYDist) * overlapHeight);
            }
        }
    }
} 