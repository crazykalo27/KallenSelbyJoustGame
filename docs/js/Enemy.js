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
        
        // Calculate overlap
        const overlapLeft = Math.max(rectH.x, rectO.x);
        const overlapRight = Math.min(rectH.x + rectH.width, rectO.x + rectO.width);
        const overlapTop = Math.max(rectH.y, rectO.y);
        const overlapBottom = Math.min(rectH.y + rectH.height, rectO.y + rectO.height);
        
        const overlapWidth = overlapRight - overlapLeft;
        const overlapHeight = overlapBottom - overlapTop;

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
 * Left-Right moving enemy (Ghost type)
 */
class LeftRightEnemy extends Enemy {
    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, speed, name);
        this.direction = Math.random() > 0.5 ? 1 : -1; // Random initial direction
    }

    update() {
        this.updatePreviousPosition();
        
        // Move left or right
        this.addXVelocity(this.speed * this.direction);
        
        super.update();
    }

    getCopy() {
        return new LeftRightEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName());
    }
}

/**
 * Random movement enemy (Koopa type)
 */
class RandomMoveEnemy extends Enemy {
    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, speed, name);
        this.moveTimer = 0;
        this.moveDirection = Math.random() * Math.PI * 2;
    }

    update() {
        this.updatePreviousPosition();
        
        // Change direction randomly
        this.moveTimer++;
        if (this.moveTimer > 60) { // Change direction every ~1 second at 60fps
            this.moveDirection = Math.random() * Math.PI * 2;
            this.moveTimer = 0;
        }
        
        // Move in current direction
        this.addXVelocity(Math.cos(this.moveDirection) * this.speed);
        this.addYVelocity(Math.sin(this.moveDirection) * this.speed * 0.5);
        
        super.update();
    }

    getCopy() {
        return new RandomMoveEnemy(this.getXCent(), this.getYCent(), this.speed, this.getName());
    }
}

/**
 * Tracker enemy that follows the hero
 */
class Tracker extends Enemy {
    constructor(xCent, yCent, speed, hero, name) {
        super(xCent, yCent, speed, name);
        this.hero = hero;
    }

    update() {
        this.updatePreviousPosition();
        
        if (this.hero) {
            // Move towards hero
            const dx = this.hero.getXCent() - this.getXCent();
            const dy = this.hero.getYCent() - this.getYCent();
            const distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance > 0) {
                this.addXVelocity((dx / distance) * this.speed);
                this.addYVelocity((dy / distance) * this.speed * 0.7);
            }
        }
        
        super.update();
    }

    getCopy() {
        return new Tracker(this.getXCent(), this.getYCent(), this.speed, this.hero, this.getName());
    }
} 