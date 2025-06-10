package mainApp;
/**
 * Class: mainApp
 * @author Team 303
 * <br>Purpose: Super class that parents all possible objects that will be on the screen
 * <br>Restrictions: Needs x and y coordinates to be passed in that are valid for the screen
 * <br>For example: 
 * <pre>
 *    GameObject g = new GameObject(10, 10);
 * </pre>
 */
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

// TODO: Make abstract?

/**
 * Class: GameObject
 * @author Team 303
 * <br>Purpose: Parent class for every object in the game (Platforms, the player, enemies, etc.)
 */
public class GameObject {
	private double xCent;
	private double yCent;
	private double Width;
	private double Height;
	private boolean shouldRemove;
	
	public GameObject(double xCord, double yCord) {
			this.xCent = xCord;
			this.yCent = yCord;
			this.Width = 50;
			this.Height = 50;
			this.shouldRemove = false;
	}

	public double getWidth() {
		return Width;
	}

	public void setWidth(double width) {
		Width = width;
	}

	public double getHeight() {
		return Height;
	}

	public void setHeight(double height) {
		Height = height;
	}

	public void update() throws DeadException {
		
	}
	
	public void drawOn(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

	public double getXCent() {
		return xCent;
	}

	public void setXCent(double d) {
		this.xCent = d;
	}

	public double getYCent() {
		return yCent;
	}

	public void setYCent(double yCent) {
		this.yCent = yCent;
	}
	public Rectangle2D.Double getBoundingBox() {
		return new Rectangle2D.Double(this.xCent - this.getWidth()/2, this.yCent - this.getHeight()/2, this.getWidth(), this.getHeight() );
	}
	public boolean overlaps(GameObject other) {
		return getBoundingBox().intersects(other.getBoundingBox());
	}
	
	public void markForRemoval() {
		this.shouldRemove = true;
	}
	
	public boolean shouldBeRemoved() {
		return this.shouldRemove;
	}
	
	public double getDistance(double x, double y) {
		return Math.sqrt(Math.pow(x - this.getXCent(), 2) + Math.pow(y - this.getYCent(), 2));
	}

}
