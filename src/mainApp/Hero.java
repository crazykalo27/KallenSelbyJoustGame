package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Hero extends MoveableObject {

	private int width;
	private int height;
	
	public Hero(int xCent, int yCent, int width, int height) {
		super(xCent, yCent);
		this.width = width;
		this.height = height;
	}

	public void drawOn(Graphics2D g2) {
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.width/2,-this.height/2,this.width,this.height));
		g2.translate(-this.getXCent(), -this.getYCent());
	}
}
