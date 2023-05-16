package mainApp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
		String fileName = "images/" + super.getName();
		
		fileName += ".PNG";
		BufferedImage img;
		try {
			img = ImageIO.read(new File(fileName));
			g2.drawImage(img, (int) (this.getXCent()-(this.getWidth()/2)), (int) (this.getYCent()-(this.getHeight()/2)), (int) this.getHeight(), (int) this.getWidth(), null);
		} catch (IOException e) {
		}
	}
	

}
