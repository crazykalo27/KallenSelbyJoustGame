package mainApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;



public class GameComponent extends JComponent implements KeyListener {
	
	private ArrayList<GameObject> GameObjects;
	private int levelNum;
	private FileReader fileReader;
	private Hero hero;
	private ArrayList<GameObject> enemies = new ArrayList<GameObject>();
	private ArrayList<GameObject> platforms = new ArrayList<GameObject>();
	private ArrayList<GameObject> player = new ArrayList<GameObject>();
	
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
		handleColisions();
	}
	
	public void drawScreen() {
		this.repaint();
	}
	public void handleColisions() {
			for(GameObject plat : this.platforms) {
				if(hero.overlaps(plat)){
					hero.collidewith(plat);
				}
				}
			}
	public void addGameObject(GameObject gameObject) {
		this.GameObjects.add(gameObject);
	}

	public void setGameObjectsArray(ArrayList<GameObject> array) {
		this.GameObjects = array;
	}
	
	public void loadLevel(int levelNumberToLoad) {
		ArrayList<GameObject> objects = fileReader.getObjectsFromFile(Integer.toString(levelNumberToLoad) + "level");
		if (!objects.isEmpty()) {
			this.setGameObjectsArray(objects);
			this.enemies = fileReader.getBad();
			this.platforms = fileReader.getPlatforms();
			this.player = fileReader.getPlayer();
			this.hero = (Hero) this.player.get(0);
		} 
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
			hero.setLeftKeyHeld(true);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			hero.setRightKeyHeld(true);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			hero.setUpKeyHeld(true);
		}
		
		
		//Level Loading
		boolean shouldUpdateLevel = false;
		
		if (e.getKeyCode() == KeyEvent.VK_U) {
				levelNum++;
				this.loadLevel(this.levelNum);
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			if (this.levelNum > 1) {
				this.levelNum--;
				this.loadLevel(this.levelNum);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//Hero Movement
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			hero.setLeftKeyHeld(false);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			hero.setRightKeyHeld(false);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			hero.setUpKeyHeld(false);
		}
	}
}
