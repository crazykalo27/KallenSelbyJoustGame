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

		this.addGameObject(new Hero(200, 200, 50, 50));
		
		for (GameObject gO: this.GameObjects) {
			gO.drawOn(g2);
		}
	}
	
	private void addGameObject(GameObject gameObject) {
		this.GameObjects.add(gameObject);
	}
}
