package mainApp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class: Platform
 * @author Team 303
 * <br>Purpose: Class for a stationary platform in game.
 * <br>Restrictions: None
 */
public class Platform extends GameObject {

	private double WIDTH;
	private double HEIGHT;
	private boolean isLava;
	private boolean isIce;
	private boolean isSlime;
	private boolean isCool;
	private String name;

	
	public Platform(double xCord,double yCord, int type) {
		super(xCord, yCord);
		WIDTH = this.getWidth();
		HEIGHT = this.getHeight();
		this.isLava = false;
		this.isIce = false;
		this.isSlime = false;
		this.isCool = false;
		this.setName(type);
		if(isLava) {
			HEIGHT = HEIGHT*.6;
			this.setHeight(HEIGHT);
		}
	}
	
	public void setName(int a) { 
		switch (a) {
		case 0:
			this.name = "Truss";
			break;
		case 1:
			this.name = "Lava";
			this.isLava = true;
			break;
		case 2:
			this.name = "Ice";
			this.isIce = true;
			break;
		case 3:
			this.name = "Slime";
			this.isSlime = true;
			break;
		case 4:
			this.name = "Health";
			this.isCool = true;
			break;
		}
	}
	
	public void drawOn(Graphics2D g2) {
		g2.translate(this.getXCent(), this.getYCent());
		
		String fileName = "images/" + "Plat" + name;
		
		fileName += ".PNG";
		BufferedImage img;
		try {
			img = ImageIO.read(new File(fileName));
			g2.drawImage(img, (int) -WIDTH/2, (int) -HEIGHT/2, (int) WIDTH, (int) HEIGHT, null);
		} catch (IOException e) {
		}
		
//		g2.setColor(Color.red);
//		//g2.translate(this.getXCent(), this.getYCent());
//		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
//		g2.setColor(Color.BLACK);
//		g2.draw(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
		
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

	public boolean isIce() {
		// TODO Auto-generated method stub
		return isIce;
	}

	public boolean isSlime() {
		// TODO Auto-generated method stub
		return isSlime;
	}
	public boolean isCool() {
		return isCool;
	}
	public void SetCool(boolean isCool) {
		this.isCool = isCool;
	}
}
