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
            this.addYVelocity(-12); // Matches original Java value
        }
        
        // Debug: Log hero position occasionally 
        if (this.debugCounter === undefined) this.debugCounter = 0;
        this.debugCounter++;
        if (this.debugCounter % 120 === 1) { // Every 2 seconds, just once
            console.log(`Hero position: (${this.getXCent().toFixed(1)}, ${this.getYCent().toFixed(1)}), velocity: (${this.getXVelocity().toFixed(1)}, ${this.getYVelocity().toFixed(1)})`);
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
        
        console.log(`COLLISION: Hero at (${this.getXCent().toFixed(1)}, ${this.getYCent().toFixed(1)}) previous (${this.getPreviousXPos().toFixed(1)}, ${this.getPreviousYPos().toFixed(1)})`);
        console.log(`  Platform at (${other.getXCent().toFixed(1)}, ${other.getYCent().toFixed(1)})`);
        console.log(`  Previous distances: X=${previousXDist.toFixed(1)}, Y=${previousYDist.toFixed(1)}`);
        
        const rectH = this.getBoundingBox();
        const rectO = other.getBoundingBox();
        
        // Calculate intersection exactly like Java's createIntersection
        const intersectionLeft = Math.max(rectH.x, rectO.x);
        const intersectionRight = Math.min(rectH.x + rectH.width, rectO.x + rectO.width);
        const intersectionTop = Math.max(rectH.y, rectO.y);
        const intersectionBottom = Math.min(rectH.y + rectH.height, rectO.y + rectO.height);
        
        const overlapWidth = intersectionRight - intersectionLeft;
        const overlapHeight = intersectionBottom - intersectionTop;
        
        console.log(`  Overlap: W=${overlapWidth.toFixed(1)}, H=${overlapHeight.toFixed(1)}`);

        // Collision resolution logic - exactly matching Java Hero.collidewith
        if (Math.abs(previousYDist) === Math.abs(previousXDist)) {
            console.log(`  DIAGONAL case - no resolution`);
            return;
        } else if (Math.abs(previousYDist) < Math.abs(previousXDist)) {
            console.log(`  HORIZONTAL collision - stopping X velocity, moving horizontally`);
            this.setXVelocity(0);
            const direction = Math.sign(other.getXCent() - this.getXCent());
            this.move(-direction * overlapWidth, 0);
            this.updatePreviousPosition();
        } else {
            console.log(`  VERTICAL collision - stopping Y velocity, moving vertically`);
            this.move(0, -Math.sign(previousYDist) * overlapHeight);
            this.setYVelocity(0);
            this.updatePreviousPosition();
        }
        
        console.log(`  After collision: Hero at (${this.getXCent().toFixed(1)}, ${this.getYCent().toFixed(1)}), velocity (${this.getXVelocity().toFixed(1)}, ${this.getYVelocity().toFixed(1)})`);
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