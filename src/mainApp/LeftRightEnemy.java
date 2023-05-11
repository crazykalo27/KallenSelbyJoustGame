package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

//TODO: add collionsons and gravity 
/**
 * Class: LeftRightEnemy
 * @author Team 303
 * <br>Purpose: Swaps between moving left and right on a random interval
 */
public class LeftRightEnemy extends Enemy {
	Random r = new Random();
	private final double DIST = 50;
	private final Color COL = Color.green;
	// private static final int WIDTH = 100;
	// private static final int HEIGHT = 100;
	private int ticks;
	private int waitNum;

	public LeftRightEnemy(double xCent, double yCent, double speed) {
		super(xCent, yCent, speed);
		this.setHasGravity(false);
		this.ticks = 0;
		this.waitNum = r.nextInt(80, 160);

	}

	public void drawOn(Graphics2D g2) throws DeadException {
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth() / 2, -this.getHeight() / 2, this.getWidth(), this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
		
		super.drawOn(g2);
	}

	@Override
	public void update() {
		this.setXVelocity(Math.pow(super.getSpeed(), 2) * Math.signum(super.getSpeed()));

		ticks++;

		if (ticks == waitNum) {
			int diry = -1;
			if(r.nextBoolean()) {
				diry *= -1;
			}
			this.setYVelocity(super.getSpeed() * diry);
			//System.out.println("updated " + waitNum);
			super.setSpeed(super.getSpeed() * -1);
			ticks = 0;
			waitNum = r.nextInt(90, 140);
		}

		super.update();
	}
}