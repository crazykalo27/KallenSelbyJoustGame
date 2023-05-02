package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;



public class GameComponent extends JComponent {
	
	private ArrayList<GameObject> GameObjects = new ArrayList<GameObject>();
	
	public GameComponent() {

	}
	
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		for (GameObject gO: this.GameObjects) {
			gO.drawOn(g2);
		}
	}
	
	public void updateObjects() {
		for (GameObject gO: this.GameObjects) {
			gO.update();
		}
	}
	
	public void drawScreen() {
		this.repaint();
	}
	
	public void addGameObject(GameObject gameObject) {
		this.GameObjects.add(gameObject);
	}
}
