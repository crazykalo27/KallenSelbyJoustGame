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
		this.setxVelocity(0);
		this.setyVelocity(0);
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
	public void update() {
		super.update();
		this.move(getxVelocity(), getyVelocity());
		
		if (this.hasGravity) {
			this.setyVelocity(this.getyVelocity() + GRAVITY_STRENGTH);
		}
		
		if (this.hasFriction) {
			this.addXVelocity(-Math.signum(getxVelocity()) * FRICTION_STRENGTH);
		}
	}

	//Random edit to try to push this file
	public void move(double xDisplacement, double yDisplacement) {
		this.setXCent(this.getXCent() + xDisplacement);
		this.setYCent(this.getYCent() + yDisplacement);
	}
	
	public double getXVelocity() {
		return getxVelocity();
	}

	public void setXVelocity(double xVelocity) {
		this.setxVelocity(Math.signum(xVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(xVelocity)));
	}
	
	public void addXVelocity(double amount) {
		this.setXVelocity(this.getxVelocity() + amount);
	}

	public double getYVelocity() {
		return getyVelocity();
	}

	public void setYVelocity(double yVelocity) {
		this.setyVelocity(Math.signum(yVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(yVelocity)));
	}
	
	public void addYVelocity(double amount) {
		this.setYVelocity(this.getyVelocity() + amount);
	}

	public double getxVelocity() {
		return xVelocity;
	}

	public void setxVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}

	public double getyVelocity() {
		return yVelocity;
	}

	public void setyVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}

}
