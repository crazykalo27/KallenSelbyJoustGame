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

public class GameObject {
	private double xCent;
	private double yCent;
	private double Width;
	private double Height;
	
	public GameObject(double xCord, double yCord) {
			this.xCent = xCord;
			this.yCent = yCord;
			this.Width = 100;
			this.Height = 100;
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

	public void update() {
		
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

}
