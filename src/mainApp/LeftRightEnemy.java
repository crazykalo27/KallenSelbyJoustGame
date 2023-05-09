package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

//TODO: add collionsons and gravity 
/**
 * Class: LeftRightEnemy
 * @author CSSE Faculty
 * <br>Purpose: Swaps between moving left and right on a random interval
 */
public class LeftRightEnemy extends MoveableObject {
	Random r = new Random();
	private final double DIST = 50;
	private final Color COL = Color.green;
	// private static final int WIDTH = 100;
	// private static final int HEIGHT = 100;
	private double speed;
	private int ticks;
	private int waitNum;

	public LeftRightEnemy(int xCent, int yCent, double speed) {
		super(xCent, yCent);
		this.speed = speed;
		this.setHasGravity(false);
		this.ticks = 0;
		this.waitNum = r.nextInt(60, 160);

	}

	public void drawOn(Graphics2D g2) {
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth() / 2, -this.getHeight() / 2, this.getWidth(), this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
	}

	@Override
	public void update() {
		this.setxVelocity(Math.pow(speed, 2) * Math.signum(speed));

		ticks++;

		if (ticks == waitNum) {
			int diry = -1;
			if(r.nextBoolean()) {
				diry *= -1;
			}
			this.setyVelocity(speed * diry);
			System.out.println("updated " + waitNum);
			speed *= -1;
			ticks = 0;
			waitNum = r.nextInt(60, 160);
		}

		super.update();
	}
}