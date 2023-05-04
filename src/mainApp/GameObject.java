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

public class GameObject {
	private double xCent;
	private double yCent;
	
	public GameObject(double xCord, double yCord) {
			this.xCent = xCord;
			this.yCent = yCord;
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

}
