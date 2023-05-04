package mainApp;
/**
 * Class: Hero
 * @author Team 303
 * <br>Purpose: Hero --> MoveableObject --> GameObject there is one moveable hero in the game that the player
 * controls and is used to defeat baddies and accomplish other tasks
 * <br>Restrictions: Needs to be given a x pos y pos and speed 
 * <br>For example: 
 * <pre>
 *    Hero h = new Hero(10, 10, 10);
 * </pre>
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

public class Hero extends MoveableObject {

	private final int WIDTH = 100;
	private final int HEIGHT = 100;
	private boolean rightKeyHeld;
	private boolean leftKeyHeld;
	private boolean upKeyHeld;
	private double speed;
	
	public Hero(int xCent, int yCent, double speed) {
		super(xCent, yCent);
		this.speed = speed;
		this.rightKeyHeld = false;
		this.leftKeyHeld = false;
		this.upKeyHeld = false;
	}

	public void drawOn(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-WIDTH/2,-HEIGHT/2,WIDTH,HEIGHT));
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
	
	public void setRightKeyHeld(boolean state) {
		this.rightKeyHeld = state;
	}
	
	public void setLeftKeyHeld(boolean state) {
		this.leftKeyHeld = state;
	}
	
	public void setUpKeyHeld(boolean state) {
		this.upKeyHeld = state;
	}
}
