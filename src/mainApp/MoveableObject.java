package mainApp;

/**
 * Class: MoveableObject
 * @author Team 303
 * <br>Purpose: Parent class of any object that should be able to move (Enemies, the player, etc.)
 * <br>Restrictions: None
 */
public class MoveableObject extends GameObject {

	private static final double DEFAULT_MAX_SPEED = 10;
	public static final double GRAVITY_STRENGTH = .5;
	private static final double FRICTION_STRENGTH = .5;
	
	private double xVelocity;
	private double yVelocity;
	private boolean hasGravity;
	private boolean hasFriction;
	
	public MoveableObject(double xCent, double yCent) {
		super(xCent, yCent);
		this.setYVelocity(0);
		this.setYVelocity(0);
		this.hasGravity = true;
		this.hasFriction = true;
	}
	
	public boolean isHasGravity() {
		return hasGravity;
	}

	public void setHasGravity(boolean hasGravity) {
		this.hasGravity = hasGravity;
	}

	@Override
	public void update() throws DeadException {
		super.update();
		this.move(getXVelocity(), getYVelocity());
		
		if (this.hasGravity) {
			this.setYVelocity(this.getYVelocity() + GRAVITY_STRENGTH);
		}
		
		if (this.hasFriction) {
			this.addXVelocity(-Math.signum(getXVelocity()) * FRICTION_STRENGTH);
		}
	}

	//Random edit to try to push this file
	public void move(double xDisplacement, double yDisplacement) {
		this.setXCent(this.getXCent() + xDisplacement);
		this.setYCent(this.getYCent() + yDisplacement);
	}
	
	public double getXVelocity() {
		return xVelocity;
	}

	public void setXVelocity(double xVelocity) {
		this.xVelocity = Math.signum(xVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(xVelocity));
	}
	
	public void addXVelocity(double amount) {
		this.setXVelocity(this.getXVelocity() + amount);
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = Math.signum(yVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(yVelocity));
	}
	
	public void addYVelocity(double amount) {
		this.setYVelocity(this.getYVelocity() + amount);
	}

}
