package mainApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

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
	private static final String EGG_FILE = "Egg";
	private boolean tutorial;
	private int pointsLoss;
	public static final int NUM_LEVELS = 12;

	Random r;
	private double xstart;
	private double ystart;

	public GameComponent() {
		this.pointsLoss = 0;
		this.GameObjects = new ArrayList<GameObject>();
		this.hero = null;
		this.levelNum = 1;
		this.fileReader = new FileReader();
		points = 0;
		lives = 4;
		this.xstart = 0;
		this.ystart = 0;
		r = new Random();
		this.setTutorial(true);
	}

	public void newGame() {
		this.levelNum = 1;
		points = 0;
		this.pointsLoss = 0;
		lives = 4;
		gameOver = false;
		this.setTutorial(true);
		loadLevel(0);
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.black);

		if (tutorial) {
			g2.setColor(Color.white);
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 60));
			g2.drawString("Welcome to Joust!", 150, 150);

			g2.setFont(new Font("TimesRoman", Font.PLAIN, 25));
			g2.drawString("Use Arrow Keys to fly", 500, 430);
			g2.drawString("up to this health box!", 500, 455);

			g2.drawString("During Collisions, if you are higher", 75, 200);
			g2.drawString("than the enemy it will die. Don't hit", 75, 225);
			g2.drawString("them while they are higher!", 75, 250);

			g2.drawString("Kill all enemies to move", 75, 525);
			g2.drawString("to the next level!", 75, 550);

			g2.drawString("Press 'N' to quick restart!", 75, 600);

			g2.drawString("Watch out for blocks", 450, 675);
			g2.drawString("with special properties!", 450, 700);

			drawScore(this.getWidth() / 2 - 182, 32, g2);
		}

		if (gameOver == true) {
			g2.setColor(Color.white);
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 80));
			g2.drawString("GAME OVER", 150, 250);

			g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));
			g2.drawString("Press 'N' to quick restart!", 150, 400);

			drawScore(this.getWidth() / 2 - 182, 32, g2);

			ArrayList<String> fileNames = new ArrayList<String>();
			for (int i = 0; i < 4; i++) {
				fileNames.add("images/gameover" + i + ".png");
			}

			BufferedImage img;
			try {
				for (int i = 0; i < 4; i++) {
					img = ImageIO.read(new File(fileNames.get(i)));
					g2.drawImage(img, 25 + (215 * i), 450, 190, 190, null);
				}

			} catch (IOException e) {
				System.out.println("fail");
			}

			return;
		}

		for (GameObject gO : new ArrayList<GameObject>(this.GameObjects)) {
			gO.drawOn(g2);
		}

		drawScore(this.getWidth() / 2 - 182, 32, g2);
	}

	public void drawScore(int x, int y, Graphics2D g2) {
		g2.setColor(Color.black);
		g2.fillRect(100, y - 38, 550, 42);

		g2.setColor(Color.white);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 35));
		g2.drawString("Points: " + this.points + "  ||  Lives: " + this.lives, x, y);
	}

	public void setTutorial(boolean bruh) {
		this.fileReader.setTutorial(bruh);
		this.tutorial = bruh;
	}

	public void updateObjects() {
		ArrayList<GameObject> enemiesToRemove = new ArrayList<GameObject>();

		if (!this.gameOver) {
			for (GameObject gO : new ArrayList<GameObject>(this.GameObjects)) {
				try {
					gO.update();
				} catch (DeadException e) { // removes the enemy
					removeDeadEnemy((Enemy) gO, enemiesToRemove);
				}
			}
			this.enemies.removeAll(enemiesToRemove);
			this.GameObjects.removeAll(enemiesToRemove);
			handleColisions();
		}

		// moves to next level when all enemies are dead
		if (this.enemies.size() == 0 && this.eggs.size() == 0) {
			if (!(levelNum == GameComponent.NUM_LEVELS)) {
				this.setTutorial(false);
				this.pointsLoss = 0;
				loadLevel(levelNum + 1);
			}
		}
	}

	public void removeDeadEnemy(Enemy enemy, ArrayList<GameObject> enemiesToRemove) {
		double xegg = enemy.getXCent();
		double yegg = enemy.getYCent();
		Egg egg = new Egg(xegg, yegg, enemy, GameComponent.EGG_FILE);
		this.GameObjects.add(egg);
		this.eggs.add(egg);

		Timer time = new Timer();
		time.schedule(new TimerTask() {

			@Override
			public void run() {
				replaceEgg(egg.getXCent(), egg.getYCent(), egg);
			}

		}, 7000);

		this.times.put(egg, time);

		this.points += GameComponent.POINTS_FOR_ENEMY_KILL;
		this.pointsLoss += GameComponent.POINTS_FOR_ENEMY_KILL;
		enemiesToRemove.add(enemy);
	}

	public void drawScreen() {
		this.repaint();
	}

	public void handleColisions() {

		ArrayList<Platform> playerPlatformCollisions = new ArrayList<Platform>();

		for (int p = 0; p < this.platforms.size(); p++) {
			Platform platform = this.platforms.get(p);
			if (hero.overlaps(platform)) {
				if (platform.isLava()) {
					respawn();
					return;
				}
				if (platform.isIce()) {
					hero.addXVelocity(5 * Math.signum(hero.getXVelocity()));
				}
				if (platform.isSlime()) {
					hero.setXVelocity(0);
				}
				if (platform.isCool()) {
					platform.setName(0);
					platform.SetCool(false);
					this.lives++;
				}
				playerPlatformCollisions.add(platform);
			}

			for (int e = 0; e < this.enemies.size(); e++) {
				if (this.enemies.get(e).overlaps(platform)) {
					this.enemies.get(e).collidewith(platform);
				}
			}

			for (int i = 0; i < this.eggs.size(); i++) {

				if (this.eggs.get(i).overlaps(platform)) {
					if (platform.isLava()) {
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

		int bounceStrength = 25; // Reduced for more natural collision response

		for (int e = 0; e < this.enemies.size(); e++) {
			Enemy enemy = this.enemies.get(e);
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

		// eggs collide with hero
		for (int i = 0; i < this.eggs.size(); i++) {
			Egg egg = this.eggs.get(i);

			if (egg.overlaps(hero)) {
				this.GameObjects.remove(egg);
				this.times.get(egg).cancel();
				this.eggs.remove(egg);
				this.pointsLoss += GameComponent.POINTS_FOR_EGG;
				this.points += GameComponent.POINTS_FOR_EGG;
			}
		}

	}

	public void respawn() {
		// when you die, you lose the amount of points equal to enemies on the screen
		this.points -= pointsLoss;

		this.lives--;
		if (this.lives == 0) {
			this.setTutorial(false);
			gameOver = true;
		} else {
			pointsLoss = 0;
			loadLevel(this.levelNum);
		}
	}

	public void addGameObject(GameObject gameObject) {
		this.GameObjects.add(gameObject);
	}

	public void setGameObjectsArray(ArrayList<GameObject> array) {
		this.GameObjects = array;
	}

	public void loadLevel(String levelNameToLoad) {
		ArrayList<GameObject> objects = fileReader.getObjectsFromFile(levelNameToLoad);
		if (!objects.isEmpty()) {

			for (Egg key : this.times.keySet()) {
				this.times.get(key).cancel();
				this.eggs.remove(key);
				// this.times.remove(key);
			}
			this.times.clear();

			this.setGameObjectsArray(objects);
			this.enemies = this.fileReader.getBad();
			this.platforms = this.fileReader.getPlatforms();
			this.player = this.fileReader.getPlayer();
			// this.hero = (Hero) this.player.get(0);
			this.hero = this.fileReader.getHero();
			// this.hero.move(this.hero.getXCent(), this.hero.getYCent());
			this.xstart = this.hero.getXCent();
			this.ystart = this.hero.getYCent();
		}
	}
	
	public void loadLevel(int levelNumberToLoad) {
		this.levelNum = levelNumberToLoad;
		this.loadLevel(Integer.toString(levelNumberToLoad) + "level");
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
			this.pointsLoss = 0;
			if (!(levelNum == GameComponent.NUM_LEVELS)) {
				this.setTutorial(false);
				if (levelNum < GameComponent.NUM_LEVELS) {
					levelNum++;
				}
			}
			this.loadLevel(this.levelNum);
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			this.pointsLoss = 0;
			if (this.levelNum > 0) {
				this.levelNum--;
				if (this.levelNum == 0) {
					this.setTutorial(true);
				}
				this.loadLevel(this.levelNum);
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_N) {
			this.newGame();
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
			
		//Open level editor
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			PrintWriter pw = null;
			try {
				String content = "Welcome to the Level Editor!\nEach character below the line represents a platform, enemy, or empty space.\nSymbols are case-sensitive!\nThere must also be at least 1 Enemy and 1 Player in your level.\n. - Air\no - Platform\nl - Lava\ni - Ice\ns - Slime\nc - Extra Life\nh - Hero Spawn (1 per level!)\nb - Ghost\ne - Koopa\nt - Tracker\nREMOVE ALL TEXT ABOVE THE LINE\n---------------------------------------\noooooooooooooooo\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..............o\no..h...........o\noooooooooooooooo";
				pw = new PrintWriter("new_level.txt");
				pw.append(content);
				pw.close();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if (pw != null) {
				File file = new File("new_level.txt");
				try {
					java.awt.Desktop.getDesktop().edit(file);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		//Open file chooser to select custom levels
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(GameComponent.this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String fileName = file.getAbsolutePath();
	            fileName = fileName.substring(0, fileName.length() - 4);
	            this.tutorial = false;
	            loadLevel(fileName);
	        } else {
	        	System.out.println("Loading custom level cancelled");
	        }
		}
	}

	public void replaceEgg(double x, double y, Egg egg) {
		this.GameObjects.remove(egg);
		this.eggs.remove(egg);
		this.times.remove(egg);

		Enemy newEnemy = egg.getContainedEnemy().getCopy();
		newEnemy.setXCent(egg.getXCent());
		newEnemy.setYCent(egg.getYCent() - newEnemy.getHeight() / 2);

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
