package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Tracker extends MoveableObject{
	private Hero hero;
	private final Color COL = Color.yellow;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private double speed;
	

	public Tracker(double xCent, double yCent, double speed, GameObject hero) {
		super(xCent,yCent);
		this.speed = speed;
		this.hero = (Hero) hero;
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
		this.setXVelocity((this.getXCent()-hero.getXCent())*-.03);
		this.setYVelocity((this.getYCent()-hero.getYCent())*-.03);
		super.update();
	}
}

