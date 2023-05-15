package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Class: Platform
 * @author Team 303
 * <br>Purpose: Class for a stationary platform in game.
 * <br>Restrictions: None
 */
public class Platform extends GameObject {
	private final Color COLOR = Color.RED;
	private double WIDTH;
	private double HEIGHT;
	public Platform(double xCord,double yCord) {
		super(xCord, yCord);
		WIDTH = this.getWidth();
		HEIGHT = this.getHeight();
	}
	
	public void drawOn(Graphics2D g2) {
		g2.translate(this.getXCent(), this.getYCent());
		g2.setColor(COLOR);
		//g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		g2.setColor(Color.BLACK);
		g2.draw(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		
		g2.translate(-this.getXCent(), -this.getYCent());
		
	}
	
}
