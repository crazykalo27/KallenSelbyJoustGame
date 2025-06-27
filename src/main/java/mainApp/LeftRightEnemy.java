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
	private static final Color COL = Color.green;
	// private static final int WIDTH = 100;
	// private static final int HEIGHT = 100;
	private int ticks;
	private int waitNum;

	public LeftRightEnemy(double xCent, double yCent, double speed, String name) {
		super(xCent, yCent, speed, name);
		this.setHasGravity(false);
		this.ticks = 0;
		this.waitNum = r.nextInt(50) + 40; // generates 40-89
		this.setSpeed(this.getSpeed()*.6);

	}

	@Override
	public void update() throws DeadException {
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
			waitNum = r.nextInt(50) + 50; // generates 50-99
		}

		super.update();
	}
	
	@Override
	public Enemy getCopy() {
		return new LeftRightEnemy(this.getXCent(), this.getYCent(), super.getSpeed(), super.getName());
	}
}