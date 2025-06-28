/**
 * Egg class - spawned when enemies die (matches Java implementation)
 */
class Egg extends MoveableObject {
    constructor(xCent, yCent, containedEnemy, imageName) {
        super(xCent, yCent, imageName);
        this.containedEnemy = containedEnemy;
        this.respawnTimer = 0;
        this.respawnDelay = 420; // 7 seconds at 60fps
        this.imageCache = new Map(); // For image caching
        
        // Eggs are affected by gravity and don't move on their own (matches Java)
        this.setHasGravity(true);
        this.setXVelocity(0);
        this.setYVelocity(0);
        
        // Set egg size (matches Java)
        this.setWidth(20);
        this.setHeight(20);
    }

    update() {
        super.update(); // This will handle gravity and physics
        this.respawnTimer++;
        
        // Mark for removal when timer expires (will be handled by game engine)
        if (this.respawnTimer >= this.respawnDelay) {
            this.markForRemoval();
        }
    }

    drawOn(ctx) {
        const fileName = this.getName() + ".PNG";
        
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
        // Use standard MoveableObject collision logic (matches Java)
        const previousXDist = other.getXCent() - this.getPreviousXPos();
        const previousYDist = other.getYCent() - this.getPreviousYPos();
        
        const rectH = this.getBoundingBox();
        const rectO = other.getBoundingBox();
        
        // Calculate intersection exactly like Java's createIntersection
        const intersectionLeft = Math.max(rectH.x, rectO.x);
        const intersectionRight = Math.min(rectH.x + rectH.width, rectO.x + rectO.width);
        const intersectionTop = Math.max(rectH.y, rectO.y);
        const intersectionBottom = Math.min(rectH.y + rectH.height, rectO.y + rectO.height);
        
        const overlapWidth = intersectionRight - intersectionLeft;
        const overlapHeight = intersectionBottom - intersectionTop;

        if (Math.abs(previousYDist) === Math.abs(previousXDist)) {
            return;
        } else if (Math.abs(previousYDist) < Math.abs(previousXDist)) {
            this.setXVelocity(0);
            const direction = Math.sign(other.getXCent() - this.getXCent());
            this.move(-direction * overlapWidth, 0);
            this.updatePreviousPosition();
        } else {
            this.move(0, -Math.sign(previousYDist) * overlapHeight);
            this.setYVelocity(0);
            this.updatePreviousPosition();
        }
    }
} 