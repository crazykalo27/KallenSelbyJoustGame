package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Platform extends GameObject {
	private final int WIDTH = 100;
	private final int HEIGHT = 100;
	private final Color COLOR = Color.RED;
	public Platform(double xCord,double yCord) {
		super(xCord, yCord);
	}
	public void drawOn(Graphics2D g2) {
		g2.setColor(COLOR);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		g2.translate(-this.getXCent(), -this.getYCent());
	}
	
}
