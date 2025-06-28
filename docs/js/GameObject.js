/**
 * Base class for all game objects
 */
class GameObject {
    constructor(xCord, yCord) {
        this.xCent = xCord;
        this.yCent = yCord;
        this.width = 50;
        this.height = 50;
        this.shouldRemove = false;
    }

    getWidth() {
        return this.width;
    }

    setWidth(width) {
        this.width = width;
    }

    getHeight() {
        return this.height;
    }

    setHeight(height) {
        this.height = height;
    }

    update() {
        // Override in subclasses
    }

    drawOn(ctx) {
        // Override in subclasses
    }

    getXCent() {
        return this.xCent;
    }

    setXCent(x) {
        this.xCent = x;
    }

    getYCent() {
        return this.yCent;
    }

    setYCent(y) {
        this.yCent = y;
    }

    getBoundingBox() {
        return {
            x: this.xCent - this.width / 2,
            y: this.yCent - this.height / 2,
            width: this.width,
            height: this.height
        };
    }

    overlaps(other) {
        const rect1 = this.getBoundingBox();
        const rect2 = other.getBoundingBox();
        
        const isOverlapping = rect1.x < rect2.x + rect2.width &&
               rect1.x + rect1.width > rect2.x &&
               rect1.y < rect2.y + rect2.height &&
               rect1.y + rect1.height > rect2.y;
        
        // Debug: Log critical overlaps only
        if (this.constructor.name === 'Hero' && other.constructor.name === 'Platform' && isOverlapping && this.firstCollision !== true) {
            console.log(`First collision detected: Hero overlaps Platform`);
            this.firstCollision = true;
        }
        
        return isOverlapping;
    }

    markForRemoval() {
        this.shouldRemove = true;
    }

    shouldBeRemoved() {
        return this.shouldRemove;
    }

    getDistance(x, y) {
        return Math.sqrt(Math.pow(x - this.xCent, 2) + Math.pow(y - this.yCent, 2));
    }
} 