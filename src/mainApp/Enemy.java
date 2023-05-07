package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Enemy extends MoveableObject {
	
	private final double DIST = 50;
	private final Color COL = Color.blue;
	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;
	private double speed;
	

	public Enemy(int xCent, int yCent, double speed) {
		super(xCent,yCent);
		this.speed = speed;
		this.setHasGravity(false);
	}
	public void drawOn(Graphics2D g2) {
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		g2.translate(-this.getXCent(), -this.getYCent());
	}
	
	@Override
	public void update() {
		double i = Math.random();
		if(i>=.5) {
			this.speed = -this.speed;
		}else {
			this.addYVelocity(speed);
		}
		this.setXVelocity(this.getXVelocity()+this.speed);
		super.update();
	}
}

