package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Egg extends Enemy {

	private final Color COLOR = Color.gray;
	// private static final int WIDTH = 100;
	// private static final int HEIGHT = 100;
	
	private Enemy containedEnemy;

	public Egg(double xCent, double yCent, Enemy containedEnemy) {
		super(xCent, yCent, 0);
		this.setHasGravity(true);
		this.setHeight(20);
		this.setWidth(20);
		this.containedEnemy = containedEnemy;
		// TODO Auto-generated constructor stub
	}

	public Enemy getContainedEnemy() {
		return containedEnemy;
	}

	@Override
	public void drawOn(Graphics2D g2) {
		g2.setColor(COLOR);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth() / 2, -this.getHeight() / 2, this.getWidth(), this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
		super.drawOn(g2);
	}
	

}
