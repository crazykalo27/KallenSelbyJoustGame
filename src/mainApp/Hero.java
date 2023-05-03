package mainApp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

public class Hero extends MoveableObject implements KeyListener {

	private int width;
	private int height;
	private boolean rightKeyHeld;
	private boolean leftKeyHeld;
	private boolean upKeyHeld;
	private double speed;
	
	public Hero(int xCent, int yCent, int width, int height, double speed) {
		super(xCent, yCent);
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.rightKeyHeld = false;
		this.leftKeyHeld = false;
		this.upKeyHeld = false;
	}

	public void drawOn(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.width/2,-this.height/2,this.width,this.height));
		g2.translate(-this.getXCent(), -this.getYCent());
	}
	
	@Override
	public void update() {
		if (this.rightKeyHeld) {
			this.setXVelocity(this.speed);
		} else if (this.leftKeyHeld) {
			this.setXVelocity(-this.speed);
		} else if (this.upKeyHeld) {
			this.setYVelocity(-this.speed);
		}
		super.update();
		this.setXVelocity(0);
		this.setYVelocity(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.leftKeyHeld = true;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.rightKeyHeld = true;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.upKeyHeld = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.leftKeyHeld = false;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.rightKeyHeld = false;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.upKeyHeld = false;
		}
	}


}
