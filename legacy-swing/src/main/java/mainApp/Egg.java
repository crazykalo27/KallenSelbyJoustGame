package mainApp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Egg extends Enemy {
	private Enemy containedEnemy;

	public Egg(double xCent, double yCent, Enemy containedEnemy, String name) {
		super(xCent, yCent, 0, name);
		this.setHasGravity(true);
		this.setHeight(20);
		this.setWidth(20);
		this.containedEnemy = containedEnemy;
		// TODO Auto-generated constructor stub
	}

	public Enemy getContainedEnemy() {
		return containedEnemy;
	}

	@Override
	public void drawOn(Graphics2D g2) {
		BufferedImage img = ResourceManager.loadImage(super.getName());
		if (img != null) {
			g2.drawImage(img, (int) (this.getXCent()-(this.getWidth()/2)), (int) (this.getYCent()-(this.getHeight()/2)), (int) this.getHeight(), (int) this.getWidth(), null);
		}
	}
	

}
