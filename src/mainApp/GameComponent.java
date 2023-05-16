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
	private ArrayList<Platform> platforms = new ArrayList<Platform>();
	private ArrayList<GameObject> player = new ArrayList<GameObject>();
	private int points;
	private int lives;
	private boolean gameOver = false;

	Random r;
	private double xstart;
	private double ystart;

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
			g2.drawString("GAME OVER", 150, 250);

			drawScore(this.getWidth()/2 - 182, 40, g2);
			return;
		}
		
		for (GameObject gO : new ArrayList<GameObject>(this.GameObjects)) {
			gO.drawOn(g2);
		}

		// TODO: Move game logic to update()
		/* for (int i = 0; i < this.GameObjects.size(); i++) {
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
		}*/

		drawScore(this.getWidth()/2 - 182, 40, g2);
	}
	
	public void drawScore(int x, int y, Graphics2D g2) {
		g2.setColor(Color.black);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 40));
		g2.drawString("Points: " + this.points + "  ||  Lives: " + this.lives, x, y);
	}

	public void updateObjects() {
		ArrayList<GameObject> enemiesToRemove = new ArrayList<GameObject>();
		
		for (GameObject gO : new ArrayList<GameObject>(this.GameObjects)) {
			try {
				gO.update();
			} catch (DeadException e) { // removes the enemy
				removeDeadEnemy((Enemy) gO, enemiesToRemove);
			}
		}
		this.enemies.removeAll(enemiesToRemove);
		this.GameObjects.removeAll(enemiesToRemove);
		
		//moves to next level when all enemies are dead
		if(this.enemies.size() == 0 && this.eggs.size() == 0) {
			if(!(levelNum == 10)) {
				loadLevel(levelNum + 1);
			}
		}
		
		handleColisions();
	}
	
	public void removeDeadEnemy(Enemy enemy, ArrayList<GameObject> enemiesToRemove) {
		double xegg = enemy.getXCent();
		double yegg = enemy.getYCent();
		Egg egg = new Egg(xegg, yegg, enemy);
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
		enemiesToRemove.add(enemy);
	}

	public void drawScreen() {
		this.repaint();
	}

	public void handleColisions() {
		
		//TODO only have enemies turn into eggs if they are killed in the air
		//TODO enemy and player bounce off eachother cases
		ArrayList<Platform> playerPlatformCollisions = new ArrayList<Platform>();
		for (Platform platform : this.platforms) {
			if (hero.overlaps(platform)) {
				if(platform.isLava()) {
					respawn();
				}
				if(platform.isIce()) {
					hero.addXVelocity(5*Math.signum(hero.getXVelocity()));
				}
				if(platform.isSlime()) {
					hero.setXVelocity(0);
				}if(platform.isCool()) {
					platform.SetCool(false);
					this.lives++;
				}
				playerPlatformCollisions.add(platform);
				//hero.collidewith(platform);
			}

			// causes enemies to collide with platforms
			for (Enemy enemy : this.enemies) {
				if (enemy.overlaps(platform)) {
					enemy.collidewith(platform);
				}
			}
			
			for (int i = 0; i < this.eggs.size(); i++) {

				if (this.eggs.get(i).overlaps(platform)) {
					if(platform.isLava()) {
						this.GameObjects.remove(this.eggs.get(i));
						this.times.get(this.eggs.get(i)).cancel();
						this.eggs.remove(this.eggs.get(i));
					} else {
					this.eggs.get(i).collidewith(platform);
					}
				}
			}

		}
		if (!playerPlatformCollisions.isEmpty()) {
			Platform closestPlatform = playerPlatformCollisions.get(0);
			double shortestDistance = Double.MAX_VALUE;
			for (Platform platform : playerPlatformCollisions) {
				double distance = platform.getDistance(hero.getXCent(), hero.getYCent());
				if (distance < shortestDistance) {
					shortestDistance = distance;
					closestPlatform = platform;
				}
			}
			hero.collidewith(closestPlatform);
		}

		int bounceStrength = 99999999;
		
		for (Enemy enemy : this.enemies) {
			if (hero.overlaps(enemy)) {
				int joustResult = hero.joust(enemy);
				if (joustResult == 2) {
					enemy.markForRemoval();
				} else if (joustResult == 0) {
					respawn();
				} else {
					int direction = (int) Math.signum(enemy.getXCent() - hero.getXCent());
					hero.setXVelocity(-direction * bounceStrength);
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
	
	private double getXVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void respawn() {
		this.lives--;
		if (this.lives == 0) {
			gameOver = true;
		}
		hero.setXCent(xstart);
		hero.setYCent(ystart);
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
			this.xstart = this.hero.getXCent();
			this.ystart = this.hero.getYCent();
			this.levelNum = levelNumberToLoad;
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
		
		Enemy newEnemy = egg.getContainedEnemy().getCopy();
		newEnemy.setXCent(egg.getXCent());
		newEnemy.setYCent(egg.getYCent() - newEnemy.getHeight()/2);
		
		this.GameObjects.add(newEnemy);
		this.enemies.add(newEnemy);
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

	public ArrayList<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(ArrayList<Platform> platforms) {
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
