/**
 * Base Enemy class
 */
class Enemy extends MoveableObject {
    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, name);
        this.speed = speed;
    }

    update() {
        super.update();
        if (this.shouldBeRemoved()) {
            throw new Error("DeadException: Enemy is marked for removal");
        }
    }

    getSpeed() {
        return this.speed;
    }

    setSpeed(speed) {
        this.speed = speed;
    }

    collidewith(other) {
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

    getCopy() {
        return new Enemy(this.getXCent(), this.getYCent(), this.speed, this.getName());
    }
}

/**
 * Left-Right moving enemy (Ghost type) - matches Java implementation exactly
 */
class LeftRightEnemy extends Enemy {
    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, speed, name);
        this.setHasGravity(false); // Ghosts float - no gravity
        this.ticks = 0;
        this.waitNum = Math.floor(Math.random() * 50) + 40; // generates 40-89
    }

    update() {
        this.updatePreviousPosition();
        
        // Set X velocity using squared speed (matches Java exactly)
        const speedSquared = Math.pow(this.getSpeed(), 2) * Math.sign(this.getSpeed());
        this.setXVelocity(speedSquared);
        
        this.ticks++;
        
        // Change direction on timer (matches Java exactly)
        if (this.ticks === this.waitNum) {
            let diry = -1;
            if (Math.random() > 0.5) {
                diry *= -1;
            }
            this.setYVelocity(this.getSpeed() * diry);
            
            // Reverse speed to change direction
            this.setSpeed(this.getSpeed() * -1);
            this.ticks = 0;
            this.waitNum = Math.floor(Math.random() * 50) + 50; // generates 50-99
        }
        
        super.update();
    }

    getCopy() {
        return new LeftRightEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName());
    }
}

/**
 * Random movement enemy (Koopa type) - matches Java implementation exactly
 */
class RandomMoveEnemy extends Enemy {
    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, speed, name);
        this.setHasGravity(true); // Koopas are affected by gravity
        this.setSpeed(this.getSpeed() * 1.2); // Reduced speed multiplier for better control
        this.direction = 1; // 1 for right, -1 for left
        this.isGrounded = false; // Track if enemy is on ground
    }

    /**
     * Check if the enemy is touching the ground (a platform below it)
     */
    isOnGround(platforms) {
        if (!platforms || platforms.length === 0) return false;
        
        // Create a small test rectangle slightly below the enemy
        const testY = this.getYCent() + this.getHeight() / 2 + 1; // 1 pixel below bottom edge
        const testRect = {
            x: this.getXCent() - this.getWidth() / 2,
            y: testY,
            width: this.getWidth(),
            height: 2 // Small height for ground detection
        };
        
        // Check if this test rectangle overlaps with any platform
        for (const platform of platforms) {
            if (!platform) continue;
            
            const platformRect = platform.getBoundingBox();
            
            // Check for overlap between test rectangle and platform
            if (testRect.x < platformRect.x + platformRect.width &&
                testRect.x + testRect.width > platformRect.x &&
                testRect.y < platformRect.y + platformRect.height &&
                testRect.y + testRect.height > platformRect.y) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Set the platforms array for ground detection
     */
    setPlatforms(platforms) {
        this.platforms = platforms;
    }

    update() {
        this.updatePreviousPosition();
        
        // Update grounded status
        this.isGrounded = this.isOnGround(this.platforms);
        
        // Random jump with 2% chance each frame, but ONLY if grounded
        const randomValue = Math.random();
        if (randomValue < 0.02 && this.isGrounded) {
            this.addYVelocity(-12); // Reduced jump strength for better gameplay
        }
        
        // Move horizontally in current direction
        this.setXVelocity(this.getSpeed() * this.direction);
        
        super.update();
    }

    collidewith(other) {
        const rectH = this.getBoundingBox();
        const rectO = other.getBoundingBox();
        
        // Calculate intersection exactly like Java's createIntersection
        const intersectionLeft = Math.max(rectH.x, rectO.x);
        const intersectionRight = Math.min(rectH.x + rectH.width, rectO.x + rectO.width);
        const intersectionTop = Math.max(rectH.y, rectO.y);
        const intersectionBottom = Math.min(rectH.y + rectH.height, rectO.y + rectO.height);
        
        const overlapWidth = intersectionRight - intersectionLeft;
        const overlapHeight = intersectionBottom - intersectionTop;
        
        // Change direction on horizontal collision (wall hit)
        if (overlapHeight >= overlapWidth) {
            this.direction *= -1; // Reverse direction
            const direction = Math.sign(other.getXCent() - this.getXCent());
            this.move(-direction * overlapWidth, 0);
            this.setXVelocity(0);
        } else {
            this.move(0, -Math.sign(this.getYVelocity()) * overlapHeight);
            this.setYVelocity(0);
        }
    }

    getCopy() {
        return new RandomMoveEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName());
    }
}

/**
 * Tracker enemy that follows the hero - matches Java implementation exactly
 */
class Tracker extends Enemy {
    constructor(xCent, yCent, speed, hero, name) {
        super(xCent, yCent, speed, name);
        this.hero = hero;
        this.setHasGravity(false); // Trackers float (no gravity)
        this.setWidth(50); // Matches Java
        this.setHeight(50); // Matches Java
    }

    update() {
        this.updatePreviousPosition();
        
        if (this.hero) {
            // Distance-based movement (matches Java exactly)
            // Velocity is proportional to distance, creating slowdown when closer
            const xDistance = this.getXCent() - this.hero.getXCent();
            const yDistance = this.getYCent() - this.hero.getYCent();
            
            this.setXVelocity(xDistance * -0.03); // Negative to move towards hero
            this.setYVelocity(yDistance * -0.03); // Negative to move towards hero
            
            // Minimum speed multiplier when too slow (matches Java)
            if (Math.abs(this.getXVelocity()) <= 2) {
                this.setXVelocity(this.getXVelocity() * 2);
                this.setYVelocity(this.getYVelocity() * 2);
            }
        }
        
        super.update();
    }

    getCopy() {
        return new Tracker(this.getXCent(), this.getYCent(), this.speed, this.hero, this.getName());
    }
} 