package mainApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

//TODO: Dedicated input manager class?

/**
 * Class: GameComponent
 * 
 * @author Team 303 <br>
 *         Purpose: Runs game logic and draws objects to screen <br>
 *         For example:
 * 
 *         <pre>
 *         GameComponent gameComponent = new GameComponent();
 *         frame.add(gameComponent);
 *         </pre>
 */
public class GameComponent extends JComponent implements KeyListener {

	public static final int POINTS_FOR_ENEMY_KILL = 750;
	public static final int POINTS_FOR_EGG = 500;

	private ArrayList<GameObject> GameObjects;
	private int levelNum;
	private FileReader fileReader;
	private Hero hero;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Egg> eggs = new ArrayList<Egg>();
	private HashMap<Egg, Timer> times = new HashMap<Egg, Timer>();
	private ArrayList<GameObject> platforms = new ArrayList<GameObject>();
	private ArrayList<GameObject> player = new ArrayList<GameObject>();
	private int points;
	private int lives;
	private boolean gameOver = false;

	Random r;

	public GameComponent() {
		this.GameObjects = new ArrayList<GameObject>();
		this.hero = null;
		this.levelNum = 1;
		this.fileReader = new FileReader();
		points = 0;
		lives = 4;
		r = new Random();
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (gameOver == true) {
			g2.setColor(Color.black);
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 80));
			g2.drawString("GAME OVER", 250, 250);

			g2.setFont(new Font("TimesRoman", Font.PLAIN, 40));
			g2.drawString("Points: " + this.points + "  ||  Lives: " + this.lives, 300, 550);
			return;
		}

		for (int i = 0; i < this.GameObjects.size(); i++) {
			if (this.GameObjects.get(i) != null) {
				try {
					this.GameObjects.get(i).drawOn(g2);
				} catch (DeadException e) { // removes the enemy
					double xegg = this.GameObjects.get(i).getXCent();
					double yegg = this.GameObjects.get(i).getYCent();
					Egg egg = new Egg(xegg, yegg);
					this.GameObjects.add(egg);
					this.eggs.add(egg);

					Timer time = new Timer();
					time.schedule(new TimerTask() {
						
						 @Override
				            public void run() {
				                replaceEgg(egg.getXCent(), egg.getYCent(), egg);
				            }
						
					}, 10000);
					
					this.times.put(egg, time);
					
					this.points += this.POINTS_FOR_ENEMY_KILL;
					this.enemies.remove(this.GameObjects.get(i));
					this.GameObjects.remove(this.GameObjects.get(i));
				}
			}
		}

		g2.setColor(Color.black);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 40));
		g2.drawString("Points: " + this.points + "  ||  Lives: " + this.lives, 300, 550);
	}

	public void updateObjects() {
		for (GameObject gO : this.GameObjects) {
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
		
		//TODO only have enemies turn into eggs if they are killed in the air
		//TODO enemy and player bounce off eachother cases
		
		for (GameObject platform : this.platforms) {
			if (hero.overlaps(platform)) {
				hero.collidewith(platform);
			}

			// causes enemies to collide with platforms
			for (Enemy enemy : this.enemies) {
				if (enemy.overlaps(platform)) {
					enemy.collidewith(platform);
				}
			}

			for (Egg eggs : this.eggs) {
				if (eggs.overlaps(platform)) {
					eggs.collidewith(platform);
				}
			}
		}

		for (Enemy enemy : this.enemies) {
			if (hero.overlaps(enemy)) {
				if (hero.joust(enemy)) {
					enemy.markForRemoval();
				} else {
					this.lives--;
					if (this.lives == 0) {
						gameOver = true;
					}
					hero.setXCent(100);
					hero.setYCent(10);
				}
			}
		}

		for (int i = 0; i < this.eggs.size(); i++) {

			if (this.eggs.get(i).overlaps(hero)) {
				this.GameObjects.remove(this.eggs.get(i));
				this.times.get(this.eggs.get(i)).cancel();
				this.eggs.remove(this.eggs.get(i));
				points += this.POINTS_FOR_EGG;
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
			this.enemies = this.fileReader.getBad();
			this.platforms = this.fileReader.getPlatforms();
			this.player = this.fileReader.getPlayer();
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

		// TODO: Find a better system for movement
		// Hero Movement
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.hero.setLeftKeyHeld(true);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.hero.setRightKeyHeld(true);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.hero.setUpKeyHeld(true);
		}

		// Level Loading
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
		// Hero Movement
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.hero.setLeftKeyHeld(false);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.hero.setRightKeyHeld(false);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.hero.setUpKeyHeld(false);
		}
	}

	public void replaceEgg(double x, double y, Egg egg) {
		this.GameObjects.remove(egg);
		this.eggs.remove(egg);
		this.times.remove(egg);
		
		int en = r.nextInt(1,4);
		Enemy a;
		
		switch (en) {
		case 1:
			a = new RandomMoveEnemy(x, y, 2);
			break;
		case 2:
			a = new LeftRightEnemy(x, y, 2);
			break;
		case 3:
			a = new Tracker(x, y, 2, hero);
			break;
		default:
			return;
		}
		
		this.GameObjects.add(a);
		this.enemies.add(a);
	}

	public ArrayList<GameObject> getGameObjects() {
		return GameObjects;
	}

	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		GameObjects = gameObjects;
	}

	public int getLevelNum() {
		return levelNum;
	}

	public void setLevelNum(int levelNum) {
		this.levelNum = levelNum;
	}

	public FileReader getFileReader() {
		return fileReader;
	}

	public void setFileReader(FileReader fileReader) {
		this.fileReader = fileReader;
	}

	public Hero getHero() {
		return hero;
	}

	public void setHero(Hero hero) {
		this.hero = hero;
	}

	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}

	public void setEnemies(ArrayList<Enemy> enemies) {
		this.enemies = enemies;
	}

	public ArrayList<GameObject> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(ArrayList<GameObject> platforms) {
		this.platforms = platforms;
	}

	public ArrayList<GameObject> getPlayer() {
		return player;
	}

	public void setPlayer(ArrayList<GameObject> player) {
		this.player = player;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public static int getPointsForEnemyKill() {
		return POINTS_FOR_ENEMY_KILL;
	}

	public static int getPointsForEgg() {
		return POINTS_FOR_EGG;
	}

}
