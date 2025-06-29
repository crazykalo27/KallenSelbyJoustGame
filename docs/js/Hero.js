/**
 * Player character class
 */
class Hero extends MoveableObject {
    static SCALER = 0.75;

    constructor(xCent, yCent, speed, name) {
        super(xCent, yCent, name);
        this.speed = speed;
        this.rightKeyHeld = false;
        this.leftKeyHeld = false;
        this.upKeyHeld = false;
        
        // Scale the hero size
        this.setWidth(this.getWidth() * Hero.SCALER);
        this.setHeight(this.getHeight() * Hero.SCALER);
    }

    update() {
        this.updatePreviousPosition();
        
        if (this.rightKeyHeld) {
            this.addXVelocity(this.speed);
        }
        if (this.leftKeyHeld) {
            this.addXVelocity(-this.speed);
        }
        if (this.upKeyHeld) {
            this.addYVelocity(-2); // Further reduced for very gentle, precise control
        }
        
        // Debug logging removed to prevent console spam
        
        super.update();
    }

    setRightKeyHeld(state) {
        this.rightKeyHeld = state;
    }

    setLeftKeyHeld(state) {
        this.leftKeyHeld = state;
    }

    setUpKeyHeld(state) {
        this.upKeyHeld = state;
    }

    // Override velocity setters to cap hero speeds lower than default
    setYVelocity(yVelocity) {
        const maxUpwardSpeed = 8; // Much lower cap for upward movement
        const maxDownwardSpeed = 18; // Keep normal cap for falling/gravity
        
        if (yVelocity < 0) { // Moving up
            this.yVelocity = Math.max(-maxUpwardSpeed, yVelocity);
        } else { // Moving down  
            this.yVelocity = Math.min(maxDownwardSpeed, yVelocity);
        }
    }

    // Override setXVelocity to cap horizontal speed lower than default
    setXVelocity(xVelocity) {
        const maxHorizontalSpeed = 7.5; // Reduced by 25% from 10 for better control
        this.xVelocity = Math.sign(xVelocity) * Math.min(maxHorizontalSpeed, Math.abs(xVelocity));
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

        // Collision resolution logic - exactly matching Java Hero.collidewith
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

    // Joust mechanics: 0 = Enemy higher (player dies), 1 = Even (bounce), 2 = Player wins
    joust(enemy) {
        const bounceAreaWidth = 10;
        if (enemy.getYCent() < this.getYCent() + bounceAreaWidth && 
            enemy.getYCent() > this.getYCent() - bounceAreaWidth) {
            return 1; // Bounce off
        } else if (enemy.getYCent() > this.getYCent()) {
            return 2; // Player wins
        } else {
            return 0; // Player dies
        }
    }

    getSpeed() {
        return this.speed;
    }

    setSpeed(speed) {
        this.speed = speed;
    }

    isRightKeyHeld() {
        return this.rightKeyHeld;
    }

    isLeftKeyHeld() {
        return this.leftKeyHeld;
    }

    isUpKeyHeld() {
        return this.upKeyHeld;
    }
} 