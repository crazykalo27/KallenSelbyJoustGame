package mainApp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class: MoveableObject
 * @author Team 303
 * <br>Purpose: Parent class of any object that should be able to move (Enemies, the player, etc.)
 * <br>Restrictions: None
 */
public class MoveableObject extends GameObject {

	private static final double DEFAULT_MAX_SPEED = 10;
	public static final double GRAVITY_STRENGTH = .5;
	private static final double FRICTION_STRENGTH = .5;
	
	private double xVelocity;
	private double yVelocity;
	private boolean hasGravity;
	private boolean hasFriction;
	private String name;
	private double previousXPos;
	private double previousYPos;
	
	public MoveableObject(double xCent, double yCent, String name) {
		super(xCent, yCent);
		this.setYVelocity(0);
		this.setYVelocity(0);
		this.hasGravity = true;
		this.hasFriction = true;
		this.name = name;
		this.previousXPos = 0;
		this.previousYPos = 0;
	}

	@Override
	public void drawOn(Graphics2D g2) {
		//un comment to see bounding box
//		g2.setColor(Color.blue);
//		g2.translate(this.getXCent(), this.getYCent());
//		g2.fill(new Rectangle2D.Double(-this.getWidth()/2,-this.getHeight()/2,this.getWidth(),this.getHeight()));
//		g2.translate(-this.getXCent(), -this.getYCent());
		
		String fileName = "images/" + name;
		
		fileName += getDir() ? "Left" : "Right";
		fileName += ".PNG";
		BufferedImage img;
		try {
			img = ImageIO.read(new File(fileName));
			g2.drawImage(img, (int) (this.getXCent()-(this.getWidth()/2)), (int) (this.getYCent()-(this.getHeight()/2)), (int) this.getHeight(), (int) this.getWidth(), null);
		} catch (IOException e) {
		}
		
		super.drawOn(g2);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHasFriction() {
		return hasFriction;
	}

	public void setHasFriction(boolean hasFriction) {
		this.hasFriction = hasFriction;
	}

	public boolean isHasGravity() {
		return hasGravity;
	}

	public void setHasGravity(boolean hasGravity) {
		this.hasGravity = hasGravity;
	}

	@Override
	public void update() throws DeadException {
		this.updatePreviousPosition();
		super.update();
		this.move(getXVelocity(), getYVelocity());
		
		if (this.hasGravity) {
			this.setYVelocity(this.getYVelocity() + GRAVITY_STRENGTH);
		}
		
		if (this.hasFriction) {
			this.addXVelocity(-Math.signum(getXVelocity()) * FRICTION_STRENGTH);
		}
	}

	//Random edit to try to push this file
	public void move(double xDisplacement, double yDisplacement) {
		this.setXCent(this.getXCent() + xDisplacement);
		this.setYCent(this.getYCent() + yDisplacement);
	}
	
	public double getXVelocity() {
		return xVelocity;
	}

	public void setXVelocity(double xVelocity) {
		this.xVelocity = Math.signum(xVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(xVelocity));
	}
	
	public void addXVelocity(double amount) {
		this.setXVelocity(this.getXVelocity() + amount);
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = Math.signum(yVelocity) * Math.min(DEFAULT_MAX_SPEED, Math.abs(yVelocity));
	}
	
	public void addYVelocity(double amount) {
		this.setYVelocity(this.getYVelocity() + amount);
	}
	
	public boolean getDir() {
		if(getXVelocity() >= 0) {
			return false;
		}
		return true;
	}

	public double getPreviousXPos() {
		return previousXPos;
	}

	public void setPreviousXPos(double previousXPos) {
		this.previousXPos = previousXPos;
	}

	public double getPreviousYPos() {
		return previousYPos;
	}

	public void setPreviousYPos(double previousYPos) {
		this.previousYPos = previousYPos;
	}
	
	public void updatePreviousPosition() {
		this.previousXPos = this.getXCent();
		this.previousYPos = this.getYCent();
	}
}
