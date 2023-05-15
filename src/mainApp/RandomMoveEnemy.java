package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
// TODO: add better movement and gravity and collsions. 
// TODO: Combine with LeftRightEnemy.
/**
 * Class: RandomMoveEnemy
 * @author Team 303
 * <br>Purpose: Moves wildly in random directions.
 */
public class RandomMoveEnemy extends Enemy {
	
	private final Color COL = Color.blue;
	//private static final int WIDTH = 100;
	//private static final int HEIGHT = 100;
	private int direction;

	public RandomMoveEnemy(double xCent, double yCent, double speed) {
		super(xCent, yCent, speed);
		this.setHasGravity(true);
		this.setSpeed(this.getSpeed()*1.5);
		this.direction = 1;
	}
	
	@Override
	public void drawOn(Graphics2D g2) {
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth()/2,-this.getHeight()/2,this.getWidth(),this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
		super.drawOn(g2);
	}
	
	@Override
	public void update() throws DeadException {
		if(this.shouldBeRemoved()) {
			throw new DeadException("This RandomMoveEnemy is Dead!");
		}
		double i = Math.random();
		if(i < .02) {
			this.addYVelocity(-20);
		}
		this.setXVelocity(super.getSpeed() * direction);
		super.update();
	}
	
	@Override
	public Enemy getCopy() {
		// TODO Auto-generated method stub
		return new RandomMoveEnemy(this.getXCent(), this.getYCent(), super.getSpeed());
	}
	
	@Override
	public void collidewith(GameObject other) {
		Rectangle2D Recth = this.getBoundingBox();
		Rectangle2D Recto = other.getBoundingBox();
		Rectangle2D overlap = Recth.createIntersection(Recto);
		double otherh = overlap.getHeight();
		double otherw = overlap.getWidth();
		if(otherh>=otherw) {
			this.direction *= -1;
			int direction = (int) Math.signum(other.getXCent() - this.getXCent());
			this.move(-direction*otherw, 0);
			this.setXVelocity(0);
		}else {
			this.move(0, -Math.signum(this.getYVelocity())*otherh);
			this.setYVelocity(0);
		}
		
	}
}

