package mainApp;

public class MoveableObject extends GameObject {

	private static final double DEFAULT_MAX_SPEED = 10;
	private static final double GRAVITY_STRENGTH = .5;
	private static final double FRICTION_STRENGTH = .5;
	
	private double xVelocity;
	private double yVelocity;
	private boolean hasGravity;
	private boolean hasFriction;
	
	public MoveableObject(int xCent, int yCent) {
		super(xCent, yCent);
		this.xVelocity = 0;
		this.yVelocity = 0;
		this.hasGravity = true;
		this.hasFriction = true;
	}
	
	@Override
	public void update() {
		super.update();
		this.move(xVelocity, yVelocity);
		
		if (this.hasGravity) {
			this.yVelocity += GRAVITY_STRENGTH;
		}
		
		if (this.hasFriction) {
			this.addXVelocity(-Math.signum(xVelocity) * FRICTION_STRENGTH);
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
		this.setXVelocity(this.xVelocity + amount);
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = Math.signum(yVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(yVelocity));
	}
	
	public void addYVelocity(double amount) {
		this.setYVelocity(this.yVelocity + amount);
	}

}
