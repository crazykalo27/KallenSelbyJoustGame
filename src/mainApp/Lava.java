package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Lava extends Platform{
	private final int WIDTH = 100;
	private final int HEIGHT = 100;
	private final Color COLOR = Color.RED;

	public Lava(double xCord, double yCord) {
		super(xCord, yCord);
	}
	
	@Override
	public void drawOn(Graphics2D g2) {
		g2.translate(this.getXCent(), this.getYCent());
		g2.setColor(this.COLOR);
		//g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		g2.setColor(Color.BLACK);
		g2.draw(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		
		g2.translate(-this.getXCent(), -this.getYCent());
		
	}

}
