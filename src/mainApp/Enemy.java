package mainApp;

import java.awt.geom.Rectangle2D;

public class Enemy extends MoveableObject{
	
	private double speed;


	public Enemy(double xCent, double yCent, double speed) {
		super(xCent, yCent);
		
		this.speed = speed;
		
		// TODO Auto-generated constructor stub
	}


	public double getSpeed() {
		return speed;
	}


	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	
	public void collidewith(GameObject other) {
		Rectangle2D Recth = this.getBoundingBox();
		Rectangle2D Recto = other.getBoundingBox();
		Rectangle2D overlap = Recth.createIntersection(Recto);
		double otherh = overlap.getHeight();
		double otherw = overlap.getWidth();
		if(otherh>=otherw) {
			this.move(-Math.signum(this.getXVelocity())*otherw, 0);
			this.setXVelocity(0);
		}else {
			this.move(0, -Math.signum(this.getYVelocity())*otherh);
			this.setYVelocity(0);
		}
		
	}

}
