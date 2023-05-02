package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

public class Hero extends MoveableObject implements KeyListener {

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
	
	@Override
	public void update() {
		super.update();
		System.out.println("xCent: " + this.getXCent());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A) {
			this.setXVelocity(5);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
