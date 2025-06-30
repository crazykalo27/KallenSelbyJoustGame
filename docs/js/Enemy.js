/**
 * Base Enemy class
 */
class Enemy extends MoveableObject {
    constructor(xCent, yCent, speed, name, isRespawned = false) {
        super(xCent, yCent, name);
        this.speed = speed;
        this.isRespawned = isRespawned; // Track if this enemy was respawned from an egg
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

    isRespawnedEnemy() {
        return this.isRespawned;
    }

    setRespawned(respawned) {
        this.isRespawned = respawned;
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
        return new Enemy(this.getXCent(), this.getYCent(), this.speed, this.getName(), this.isRespawned);
    }
}

/**
 * Left-Right moving enemy (Ghost type) - Updated with 8-directional movement
 */
class LeftRightEnemy extends Enemy {
    constructor(xCent, yCent, speed, name, isRespawned = false) {
        super(xCent, yCent, speed, name, isRespawned);
        this.setHasGravity(false); // Ghosts float - no gravity
        this.setSpeed(this.getSpeed() * 1.3); // Increase speed to be faster than player (7.5 * 1.3 ≈ 9.75)
        this.ticks = 0;
        this.waitNum = Math.floor(Math.random() * 120) + 60; // Start with 1-3 seconds delay to prevent immediate corner phasing
        this.currentDirection = this.getRandomDirection8();
        this.hasStarted = false; // Track if movement has started
    }

    getRandomDirection8() {
        // 8 directions: N, NE, E, SE, S, SW, W, NW
        const directions = [
            { x: 0, y: -1 },    // North
            { x: 1, y: -1 },    // Northeast  
            { x: 1, y: 0 },     // East
            { x: 1, y: 1 },     // Southeast
            { x: 0, y: 1 },     // South
            { x: -1, y: 1 },    // Southwest
            { x: -1, y: 0 },    // West
            { x: -1, y: -1 }    // Northwest
        ];
        return directions[Math.floor(Math.random() * 8)];
    }

    update() {
        this.updatePreviousPosition();
        
        // Only start moving after initial delay to prevent corner phasing
        if (!this.hasStarted) {
            this.ticks++;
            if (this.ticks >= this.waitNum) {
                this.hasStarted = true;
                this.ticks = 0;
                this.waitNum = Math.floor(Math.random() * 120) + 30; // Normal timing after startup
            }
            return; // Don't move during startup delay
        }
        
        // Calculate speed multiplier for diagonal movement to maintain consistent speed
        const speedMultiplier = (this.currentDirection.x !== 0 && this.currentDirection.y !== 0) ? 1.4 : 1.0;
        
        // Move in current direction with speed compensation
        this.setXVelocity(this.getSpeed() * this.currentDirection.x * speedMultiplier);
        this.setYVelocity(this.getSpeed() * this.currentDirection.y * speedMultiplier);
        
        this.ticks++;
        
        // Change direction on timer (0.5-2.5 seconds for more dynamic movement)
        if (this.ticks >= this.waitNum) {
            this.currentDirection = this.getRandomDirection8();
            this.ticks = 0;
            this.waitNum = Math.floor(Math.random() * 120) + 30; // 0.5-2.5 seconds at 60fps
        }
        
        super.update();
    }

    getCopy() {
        return new LeftRightEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName(), this.isRespawned);
    }
}

/**
 * Random movement enemy (Koopa type) - Updated with less turning and timed jumping
 */
class RandomMoveEnemy extends Enemy {
    constructor(xCent, yCent, speed, name, isRespawned = false) {
        super(xCent, yCent, speed, name, isRespawned);
        this.setHasGravity(true); // Koopas are affected by gravity
        this.setSpeed(this.getSpeed() * 1.2); // Reduced speed multiplier for better control
        this.direction = 1; // 1 for right, -1 for left
        this.isGrounded = false; // Track if enemy is on ground
        this.jumpTimer = 0;
        this.nextJumpTime = Math.floor(Math.random() * 240); // 0-4 seconds at 60fps (0-240 ticks)
    }

    /**
     * Check if the enemy is on the ground by testing collision with platforms
     */
    isOnGround(platforms) {
        if (!platforms || platforms.length === 0) return false;
        
        // Create a test position slightly below current position
        const testRect = {
            x: this.getXCent() - this.getWidth() / 2,
            y: this.getYCent() - this.getHeight() / 2 + 2, // Test 2 pixels below
            width: this.getWidth(),
            height: this.getHeight()
        };
        
        for (const platform of platforms) {
            const platformRect = platform.getBoundingBox();
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
        
        // Timed jumping system (0-4 seconds)
        this.jumpTimer++;
        if (this.jumpTimer >= this.nextJumpTime && this.isGrounded) {
            this.addYVelocity(-12); // Jump strength
            this.jumpTimer = 0;
            this.nextJumpTime = Math.floor(Math.random() * 240); // Next jump in 0-4 seconds
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
        
        // Only change direction on horizontal collision (wall hit) - reduced frequency
        if (overlapHeight >= overlapWidth) {
            // Only change direction 75% of the time (reduced from 100%)
            if (Math.random() < 0.75) {
                this.direction *= -1; // Reverse direction
            }
            const direction = Math.sign(other.getXCent() - this.getXCent());
            this.move(-direction * overlapWidth, 0);
            this.setXVelocity(0);
        } else {
            this.move(0, -Math.sign(this.getYVelocity()) * overlapHeight);
            this.setYVelocity(0);
        }
    }

    getCopy() {
        return new RandomMoveEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName(), this.isRespawned);
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