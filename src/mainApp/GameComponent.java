package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JComponent;



public class GameComponent extends JComponent implements KeyListener{
	
	private ArrayList<GameObject> GameObjects = new ArrayList<GameObject>();
	private int levelNum = 1;
	private FileReader fileReader;
	
	public GameComponent() {
		fileReader = new FileReader();
	}
	
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		for (GameObject gO: this.GameObjects) {
			if (gO != null) {
				gO.drawOn(g2);
			}
		}
	}
	
	public void updateObjects() {
		for (GameObject gO: this.GameObjects) {
			if (gO != null) {
				gO.update();
			}
		}
	}
	
	public void drawScreen() {
		this.repaint();
	}
	
	public void addGameObject(GameObject gameObject) {
		this.GameObjects.add(gameObject);
	}

	public void setGameObjectsArray(ArrayList<GameObject> array) {
		this.GameObjects = array;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		
		if (e.getKeyCode() == KeyEvent.VK_U) {
			this.levelNum++;
			objects = fileReader.getObjectsFromFile(Integer.toString(this.levelNum) + "level");
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			this.levelNum--;
			objects = fileReader.getObjectsFromFile(Integer.toString(this.levelNum) + "level");
		}
		
		this.setGameObjectsArray(objects);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
