/**
 * Base class for moveable objects with physics
 */
class MoveableObject extends GameObject {
    static DEFAULT_MAX_SPEED = 18;
    static GRAVITY_STRENGTH = 0.8;
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
    }

    drawOn(ctx) {
        // Draw sprite based on direction
        const imageName = this.name + (this.getDir() ? "Left" : "Right");
        const imageKey = imageName + ".PNG";
        
        if (this.imageCache.has(imageKey)) {
            const img = this.imageCache.get(imageKey);
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
                this.imageCache.set(imageKey, img);
            };
            img.src = `images/${imageKey}`;
            this.imageCache.set(imageKey, img);
        }

        // Debug: Draw bounding box (uncomment to see)
        // ctx.strokeStyle = 'blue';
        // ctx.strokeRect(this.xCent - this.width/2, this.yCent - this.height/2, this.width, this.height);
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