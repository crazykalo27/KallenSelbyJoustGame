package mainApp;

public class MoveableObject extends GameObject {

	private int xVelocity;
	private int yVelocity;
	
	public MoveableObject(int xCent, int yCent) {
		super(xCent, yCent);
		this.xVelocity = 0;
		this.yVelocity = 0;
	}
	
	@Override
	public void update() {
		this.setXCent(this.getXCent() + this.getXVelocity());
		this.setYCent(this.getYCent() + this.getYVelocity());
	}

	public int getXVelocity() {
		return xVelocity;
	}

	public void setXVelocity(int xVelocity) {
		this.xVelocity = xVelocity;
	}

	public int getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(int yVelocity) {
		this.yVelocity = yVelocity;
	}

}
