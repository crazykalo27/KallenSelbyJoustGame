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
            this.addYVelocity(-12); // Increased for more responsive flying
        }
        
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