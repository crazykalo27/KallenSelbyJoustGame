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
	private double speed;	

	public RandomMoveEnemy(double xCent, double yCent, double speed) {
		super(xCent, yCent, speed);
		this.speed = speed;
		this.setHasGravity(false);
	}
	
	@Override
	public void drawOn(Graphics2D g2) throws DeadEnemyException {
		if(super.getDead()) {
			throw new DeadEnemyException("This RandomMoveEnemy is Dead!");
		}
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth()/2,-this.getHeight()/2,this.getWidth(),this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
		super.drawOn(g2);
	}
	
	@Override
	public void update() {
		double i = Math.random();
		if(i>=.5) {
			this.speed = -this.speed;
		}else {
			this.addYVelocity(speed);
			if(Math.abs(this.getYVelocity()) >=5) {
				this.setYVelocity(this.getYVelocity()*.5);
			}
		}
		this.setXVelocity(this.getXVelocity()+this.speed);
		super.update();
	}
	
}

