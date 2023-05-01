package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Hero extends MoveableObject {

	private int x;
	private int y;
	private int width;
	private int height;
	
	public Hero(int x, int y, int width, int height) {
		// TODO Auto-generated constructor stub
	}

	public void drawOn(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.translate(x, y);
		g2.fill(new Rectangle2D.Double(0,0,this.width,this.height));
		g2.translate(-x, -y);
	}
}
