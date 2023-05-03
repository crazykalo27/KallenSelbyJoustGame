package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JComponent;



public class GameComponent extends JComponent implements KeyListener{
	
	private ArrayList<GameObject> GameObjects;
	private Hero hero;
	private int levelNum;
	private FileReader fileReader;
	
	public GameComponent() {
		this.GameObjects = new ArrayList<GameObject>();
		this.hero = null;
		this.levelNum = 1;
		this.fileReader = new FileReader();
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
	
	public void loadLevel(int levelNumberToLoad) {
		ArrayList<GameObject> objects = fileReader.getObjectsFromFile(Integer.toString(levelNumberToLoad) + "level");
		this.setGameObjectsArray(objects);
		this.hero = findHeroInArray();
		
	}
	
	public Hero findHeroInArray() {
		for (GameObject gO : this.GameObjects) {
			if (gO instanceof Hero) {
				return (Hero) gO;
			}
		}
		
		return null;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		//TODO: Find a better system for movement
		//Hero Movement
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			hero.toggleLeftKeyHeld();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			hero.toggleRightKeyHeld();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			hero.toggleUpKeyHeld();
		}
		
		
		//Level Loading
		boolean shouldUpdateLevel = false;
		
		if (e.getKeyCode() == KeyEvent.VK_U) {
			this.levelNum++;
			shouldUpdateLevel = true;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			this.levelNum--;
			shouldUpdateLevel = true;
		}
		
		if (shouldUpdateLevel) {
			this.loadLevel(this.levelNum);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//Hero Movement
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			hero.toggleLeftKeyHeld();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			hero.toggleRightKeyHeld();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			hero.toggleUpKeyHeld();
		}
	}
}
