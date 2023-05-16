package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

// TODO: Make tracker follow player at a more constant rate? (Currently slows significantly when near player)

/**
 * Class: Tracker
 * @author Team 303
 * <br>Purpose: Class for enemy that follows the player
 * <br>Restrictions: None
 */
public class Tracker extends Enemy{
	private Hero hero;
	private final Color COL = Color.yellow;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	

	public Tracker(double xCent, double yCent, double speed, GameObject hero, String name) {
		super(xCent,yCent, speed, name);
		this.hero = (Hero) hero;
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		//this.setHasGravity(false);
	}
	public void drawOn(Graphics2D g2) {
		g2.setColor(COL);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth()/2,-this.getHeight()/2,this.getWidth(),this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
		
		super.drawOn(g2);
	}
	
	@Override
	//TODO could make this not dependent on location so it has a constant speed
	public void update() throws DeadException {
		this.setXVelocity((this.getXCent()-hero.getXCent())*-.03);
		this.setYVelocity((this.getYCent()-hero.getYCent())*-.03);
		if(Math.abs(this.getXVelocity())<=2) {
			this.setXVelocity(this.getXVelocity()*2);
			this.setYVelocity(this.getYVelocity()*2);
		}

		super.update();
	}
	
	@Override
	public Enemy getCopy() {
		return new Tracker(this.getXCent(), this.getYCent(), this.getSpeed(), this.hero, super.getName());
	}
}

