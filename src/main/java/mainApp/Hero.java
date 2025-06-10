package mainApp;
/**
 * Class: Hero
 * @author Team 303
 * <br>Purpose: Hero --> MoveableObject --> GameObject there is one moveable hero in the game that the player
 * controls and is used to defeat baddies and accomplish other tasks
 * <br>Restrictions: Needs to be given a x pos y pos and speed 
 * <br>For example: 
 * <pre>
 *    Hero h = new Hero(10, 10, 10);
 * </pre>
 */

import java.awt.geom.Rectangle2D;

/**
 * Class: Hero
 * @author Team 303
 * <br>Purpose: The player character class
 * <br>Restrictions: Only one Hero instance should exist at a time
 */
public class Hero extends MoveableObject {

	private static final double SCALER = .75;
	
	private boolean rightKeyHeld;
	private boolean leftKeyHeld;
	private boolean upKeyHeld;
	private double speed;
	
	public Hero(int xCent, int yCent, double speed, String name) {
		super(xCent, yCent, name);
		this.speed = speed;
		this.rightKeyHeld = false;
		this.leftKeyHeld = false;
		this.upKeyHeld = false;
		this.setWidth(this.getWidth()*SCALER);
		this.setHeight(this.getHeight()*SCALER);
	}
	
	@Override
	public void update() throws DeadException {
		updatePreviousPosition();
		if (this.rightKeyHeld) {
			this.addXVelocity(this.speed);
		}
		if (this.leftKeyHeld) {
			this.addXVelocity(-this.speed);
		}
		if (this.upKeyHeld) {
			this.addYVelocity(-8);
		}
		super.update();
	}
	
	public void setRightKeyHeld(boolean state) {
		this.rightKeyHeld = state;
	}
	
	public void setLeftKeyHeld(boolean state) {
		this.leftKeyHeld = state;
	}
	
	public void setUpKeyHeld(boolean state) {
		if(state == false) {
			//on up button release there is a hard stop of y movement for hero
			//this.setYVelocity(super.GRAVITY_STRENGTH);
		}
		this.upKeyHeld = state;
	}

	public void collidewith(GameObject other) {
		double previousXDist = other.getXCent() - this.getPreviousXPos();
		double previousYDist = other.getYCent() - this.getPreviousYPos();
		Rectangle2D Recth = this.getBoundingBox();
		Rectangle2D Recto = other.getBoundingBox();
		Rectangle2D overlap = Recth.createIntersection(Recto);
		double overlapHeight = overlap.getHeight();
		double overlapWidth = overlap.getWidth();
		if (Math.abs(previousYDist) == Math.abs(previousXDist)) {
			return;
		} else if (Math.abs(previousYDist) < Math.abs(previousXDist)) {
			this.setXVelocity(0);
			double direction = Math.signum(other.getXCent() - this.getXCent());
			this.move(-direction*overlapWidth, 0);
			this.updatePreviousPosition();
		} else {
			this.move(0, -Math.signum(previousYDist)*overlapHeight);
			this.setYVelocity(0);
			this.updatePreviousPosition();
		}
	}
	
	// 0 = Enemy higher, player dies
	// 1 = Approximately even, bounce off
	// 2 = Player wins, enemy dies
	public int joust(GameObject enemy) {
		int bounceAreaWidth = 10;
	    if (enemy.getYCent() < this.getYCent() + bounceAreaWidth && enemy.getYCent() > this.getYCent() - bounceAreaWidth) {
			return 1;
		} else if(enemy.getYCent() > this.getYCent()) {
			return 2;
		} else {
			return 0;
		}
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isRightKeyHeld() {
		return rightKeyHeld;
	}

	public boolean isLeftKeyHeld() {
		return leftKeyHeld;
	}

	public boolean isUpKeyHeld() {
		return upKeyHeld;
	}
	
	
}
