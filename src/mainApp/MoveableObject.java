package mainApp;

public class MoveableObject extends GameObject {

	private double xVelocity;
	private double yVelocity;
	
	public MoveableObject(int xCent, int yCent) {
		super(xCent, yCent);
		this.xVelocity = 0;
		this.yVelocity = 0;
	}
	
	@Override
	public void update() {
		super.update();
		this.move(xVelocity, yVelocity);
	}

	public void move(double xDisplacement, double yDisplacement) {
		this.setXCent(this.getXCent() + xDisplacement);
		this.setYCent(this.getYCent() + yDisplacement);
	}
	
	public double getXVelocity() {
		return xVelocity;
	}

	public void setXVelocity(double speed) {
		this.xVelocity = speed;
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}

}
