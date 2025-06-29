/**
 * Base class for moveable objects with physics
 */
class MoveableObject extends GameObject {
    static DEFAULT_MAX_SPEED = 18;
    static GRAVITY_STRENGTH = 0.5; // Further reduced for even gentler gravity feel
    static FRICTION_STRENGTH = 0.25;

    constructor(xCent, yCent, name) {
        super(xCent, yCent);
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.hasGravity = true;
        this.hasFriction = true;
        this.name = name;
        this.previousXPos = xCent;
        this.previousYPos = yCent;
        this.imageCache = new Map(); // Cache for loaded images
        this.frameCount = 0; // Debug counter
    }

    drawOn(ctx) {
        // Draw sprite based on direction
        const imageName = this.name + (this.getDir() ? "Left" : "Right");
        
        // Handle the special case where DigDugLeft.png has lowercase extension
        let imageKey;
        if (imageName === "DigDugLeft") {
            imageKey = imageName + ".png";
        } else {
            imageKey = imageName + ".PNG";
        }
        
        if (this.imageCache.has(imageKey)) {
            const img = this.imageCache.get(imageKey);
            
            // Check if it's a failed image marker
            if (img && img.failed) {
                // Draw fallback rectangle
                ctx.fillStyle = '#cccccc';
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
            } else if (img && img.complete) {
                // Check if the image is actually valid
                if (img.naturalWidth && img.naturalWidth > 0) {
                    try {
                        ctx.drawImage(
                            img,
                            this.xCent - this.width / 2,
                            this.yCent - this.height / 2,
                            this.width,
                            this.height
                        );
                    } catch (error) {
                        console.error(`ERROR: Failed to draw character sprite ${imageKey}:`, error.message);
                        // Mark as failed and draw fallback
                        this.imageCache.set(imageKey, { complete: true, failed: true });
                        this.drawFallbackSprite(ctx);
                    }
                } else {
                    this.imageCache.set(imageKey, { complete: true, failed: true });
                    this.drawFallbackSprite(ctx);
                }
            }
        } else {
            // Load image if not cached
            const img = new Image();
            img.onload = () => {
                this.imageCache.set(imageKey, img);
            };
            
            img.onerror = () => {
                this.imageCache.set(imageKey, { complete: true, failed: true });
            };
            
            img.src = `images/${imageKey}`;
            this.imageCache.set(imageKey, img);
            
            // Draw fallback while loading
            this.drawFallbackSprite(ctx);
        }

        // Debug: Draw bounding box (uncomment to see)
        // ctx.strokeStyle = 'blue';
        // ctx.strokeRect(this.xCent - this.width/2, this.yCent - this.height/2, this.width, this.height);
    }

    drawFallbackSprite(ctx) {
        // Draw a simple colored rectangle as fallback
        ctx.fillStyle = '#cccccc';
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

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
    }

    isHasFriction() {
        return this.hasFriction;
    }

    setHasFriction(hasFriction) {
        this.hasFriction = hasFriction;
    }

    isHasGravity() {
        return this.hasGravity;
    }

    setHasGravity(hasGravity) {
        this.hasGravity = hasGravity;
    }

    update() {
        this.updatePreviousPosition();
        super.update();
        this.move(this.getXVelocity(), this.getYVelocity());

        if (this.hasGravity) {
            this.setYVelocity(this.getYVelocity() + MoveableObject.GRAVITY_STRENGTH);
        }

        if (this.hasFriction) {
            this.addXVelocity(-Math.sign(this.getXVelocity()) * MoveableObject.FRICTION_STRENGTH);
        }
        
        // Debug logging removed to prevent console spam
    }

    move(xDisplacement, yDisplacement) {
        this.setXCent(this.getXCent() + xDisplacement);
        this.setYCent(this.getYCent() + yDisplacement);
    }

    getXVelocity() {
        return this.xVelocity;
    }

    setXVelocity(xVelocity) {
        this.xVelocity = Math.sign(xVelocity) * Math.min(MoveableObject.DEFAULT_MAX_SPEED, Math.abs(xVelocity));
    }

    addXVelocity(amount) {
        this.setXVelocity(this.getXVelocity() + amount);
    }

    getYVelocity() {
        return this.yVelocity;
    }

    setYVelocity(yVelocity) {
        this.yVelocity = Math.sign(yVelocity) * Math.min(MoveableObject.DEFAULT_MAX_SPEED, Math.abs(yVelocity));
    }

    addYVelocity(amount) {
        this.setYVelocity(this.getYVelocity() + amount);
    }

    getDir() {
        return this.getXVelocity() < 0; // true = left, false = right
    }

    getPreviousXPos() {
        return this.previousXPos;
    }

    setPreviousXPos(previousXPos) {
        this.previousXPos = previousXPos;
    }

    getPreviousYPos() {
        return this.previousYPos;
    }

    setPreviousYPos(previousYPos) {
        this.previousYPos = previousYPos;
    }

    updatePreviousPosition() {
        this.previousXPos = this.xCent;
        this.previousYPos = this.yCent;
    }
} 