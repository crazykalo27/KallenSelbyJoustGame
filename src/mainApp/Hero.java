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

// TODO: Collisions with enemies

/**
 * Class: Hero
 * @author Team 303
 * <br>Purpose: The player character class
 * <br>Restrictions: Only one Hero instance should exist at a time
 */
public class Hero extends MoveableObject {

	private static final int WIDTH = 75;
	private static final int HEIGHT = 75;
	
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
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
	}

	public void drawOn(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.translate(this.getXCent(), this.getYCent());
		g2.fill(new Rectangle2D.Double(-this.getWidth()/2,-this.getHeight()/2,this.getWidth(),this.getHeight()));
		g2.translate(-this.getXCent(), -this.getYCent());
	}
	
	@Override
	public void update() {
		if (this.rightKeyHeld) {
			this.addXVelocity(this.speed);
		}
		if (this.leftKeyHeld) {
			this.addXVelocity(-this.speed);
		}
		if (this.upKeyHeld) {
			this.addYVelocity(-12);
		}
		super.update();
	}
	
	public void setRightKeyHeld(boolean state) {
		this.rightKeyHeld = state;
	}
	
	public void setLeftKeyHeld(boolean state) {
		this.leftKeyHeld = state;
	}
	
	public void setUpKeyHeld(boolean state) {
		if(state == false) {
			//on up button release there is a hard stop of y movement for hero
			//this.setYVelocity(super.GRAVITY_STRENGTH);
		}
		this.upKeyHeld = state;
	}

	public void collidewith(GameObject other) {
		Rectangle2D Recth = this.getBoundingBox();
		Rectangle2D Recto = other.getBoundingBox();
		Rectangle2D overlap = Recth.createIntersection(Recto);
		double otherh = overlap.getHeight();
		double otherw = overlap.getWidth();
		if(otherh>=otherw) {
			// TODO: Fix getting caught on ceiling
			int direction = (int) Math.signum(other.getXCent() - this.getXCent());
			this.move(-direction*otherw, 0);
			this.setXVelocity(0);
		}else {
			this.move(0, -Math.signum(this.getYVelocity())*otherh);
			this.setYVelocity(0);
		}
		
	}

	public boolean joust(GameObject enem) {		
		
		//case of enemy is lower on the screen: enemy should die
		if(enem.getYCent() > this.getYCent()) {
			//System.out.println("Enemy is lower!");
			return true;
		}
		return false;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isRightKeyHeld() {
		return rightKeyHeld;
	}

	public boolean isLeftKeyHeld() {
		return leftKeyHeld;
	}

	public boolean isUpKeyHeld() {
		return upKeyHeld;
	}
}
