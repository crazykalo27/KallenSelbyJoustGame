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

	private final Color DEFAULT_COLOR = Color.ORANGE;
	private final Color LAVA_COLOR = Color.RED;
	private double WIDTH;
	private double HEIGHT;
	private boolean isLava;
	
	public Platform(double xCord,double yCord, boolean isLava) {
		super(xCord, yCord);
		WIDTH = this.getWidth();
		HEIGHT = this.getHeight();
		this.isLava = isLava;
	}
	
	public void drawOn(Graphics2D g2) {
		g2.translate(this.getXCent(), this.getYCent());
		Color temp = DEFAULT_COLOR;
		if(isLava) {
			temp = LAVA_COLOR;
		}
		g2.setColor(temp);
		//g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		g2.setColor(Color.BLACK);
		g2.draw(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		
		g2.translate(-this.getXCent(), -this.getYCent());
		
	}

	public double getWIDTH() {
		return WIDTH;
	}

	public void setWIDTH(double wIDTH) {
		WIDTH = wIDTH;
	}

	public double getHEIGHT() {
		return HEIGHT;
	}

	public void setHEIGHT(double hEIGHT) {
		HEIGHT = hEIGHT;
	}

	public boolean isLava() {
		return isLava;
	}

	public void setLava(boolean isLava) {
		this.isLava = isLava;
	}

	public Color getDEFAULT_COLOR() {
		return DEFAULT_COLOR;
	}

	public Color getLAVA_COLOR() {
		return LAVA_COLOR;
	}
	
}
