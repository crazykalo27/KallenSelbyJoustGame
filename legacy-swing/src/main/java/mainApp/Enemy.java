package mainApp;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Enemy extends MoveableObject{
	
	private double speed;

	public Enemy(double xCent, double yCent, double speed, String name) {
		super(xCent, yCent, name);
		
		this.speed = speed;
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update() throws DeadException {
		super.update();
		if(shouldBeRemoved()) {
			throw new DeadException("This RandomMoveEnemy is Dead!");
		}
	}


	public double getSpeed() {
		return speed;
	}


	public void setSpeed(double speed) {
		this.speed = speed;
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
			updatePreviousPosition();
		} else {
			this.move(0, -Math.signum(previousYDist)*overlapHeight);
			this.setYVelocity(0);
			updatePreviousPosition();
		}
	}
	
	public Enemy getCopy() {
		return new Enemy(this.getXCent(), this.getYCent(), speed, super.getName());
	}
	
}
