package com.joust.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.joust.JoustGame;
import com.joust.entities.Hero;
import com.joust.entities.Enemy;
import com.joust.entities.Platform;
import com.joust.entities.Egg;
import com.joust.managers.LevelLoader;
import com.joust.managers.CollisionManager;
import com.joust.entities.GameEntity;
import com.joust.managers.LevelLoader.LevelData;
import com.joust.managers.InputManager;
import com.joust.entities.GameObject;

import java.util.ArrayList;

/**
 * Main game screen - replaces Swing GameComponent
 * Handles all game logic, rendering, and input
 */
public class GameScreen implements Screen {
    
    // Game constants
    public static final int POINTS_FOR_ENEMY_KILL = 750;
    public static final int POINTS_FOR_EGG = 500;
    public static final int NUM_LEVELS = 13; // 0-12 levels to match original
    
    private final JoustGame game;
    
    // Camera and viewport
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private Viewport viewport;
    
    // Game entities
    private ArrayList<GameObject> gameObjects;
    private Hero hero;
    private ArrayList<Enemy> enemies;
    private ArrayList<Platform> platforms;
    private ArrayList<Egg> eggs;
    
    // Managers
    private final LevelLoader levelLoader;
    private final CollisionManager collisionManager;
    private InputManager inputManager;
    
    // Game state
    private int currentLevel;
    private int points;
    private int lives;
    private boolean gameOver;
    private boolean tutorial;
    private float gameTime;
    private boolean isPaused = false;
    
    // Rendering
    private BitmapFont font;
    private BitmapFont largeFont;
    
    // World dimensions - updated to match level size (16x16 grid at 50px each = 800x800)
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 800f;
    
    public GameScreen(JoustGame game) {
        this.game = game;
        
        // Set up camera and viewport to match original game dimensions EXACTLY
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        // Use FitViewport to maintain aspect ratio and prevent stretching
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        largeFont = new BitmapFont();
        largeFont.getData().setScale(2.0f); // For large text like titles
        
        // Initialize managers
        levelLoader = new LevelLoader();
        collisionManager = new CollisionManager();
        
        // Set up input and initialize arrays BEFORE loading level
        setupInput();
        
        // Initialize game state and load initial level
        newGame();
        
        Gdx.app.log("GameScreen", "Game screen initialized");
    }
    
    public void newGame() {
        currentLevel = 0; // Start with tutorial level
        points = 0;
        lives = 4;
        gameOver = false;
        tutorial = true;
        gameTime = 0;
        
        loadLevel(0);
    }
    
    public void restartGame() {
        newGame();
    }
    
    @Override
    public void show() {
        Gdx.app.log("GameScreen", "Game screen shown");
        Gdx.input.setInputProcessor(inputManager);
    }
    
    @Override
    public void render(float delta) {
        if (isPaused) return;
        
        gameTime += delta;
        
        // Update game state
        if (!gameOver) {
            update(delta);
            checkLevelCompletion();
        }
        
        // Clear screen
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1); // Black background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Handle input
        handleInput();
        
        // Render game
        batch.begin();
        
        if (tutorial) {
            renderTutorial();
        } else if (gameOver) {
            renderGameOver();
        } else {
            renderGame();
        }
        
        renderUI();
        
        batch.end();
    }
    
    private void handleInput() {
        if (hero == null) return;
        
        // Movement input
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE);
        
        hero.setLeftKeyHeld(leftPressed);
        hero.setRightKeyHeld(rightPressed);
        hero.setUpKeyHeld(upPressed);
        
        // Game control input
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            restartGame();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        
        // Tutorial interaction - press ENTER to start/advance
        if (tutorial && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            tutorial = false; // End tutorial and start actual game
        }
    }
    
    /**
     * Update game logic
     */
    private void update(float delta) {
        // Update hero
        if (hero != null) {
            hero.update(delta);
            
            // Check hero collisions with platforms
            for (Platform platform : platforms) {
                if (hero.overlaps(platform)) {
                    hero.handleCollision(platform);
                    // Check for health platform collision
                    if (platform.getType() == Platform.PlatformType.HEALTH && !platform.isDestroyed()) {
                        lives++; // Give extra life
                        platform.destroy(); // Remove health block
                        Gdx.app.log("GameScreen", "Health collected! Lives: " + lives);
                    }
                }
            }
            
            // Check hero collisions with enemies (let hero handle the logic)
            for (Enemy enemy : enemies) {
                if (hero.overlaps(enemy)) {
                    hero.handleCollision(enemy);
                }
            }
            
            // Check if hero should die (from enemy collision or lava)
            if (hero.shouldDie()) {
                playerDied();
                return; // Don't process other updates this frame
            }
            
            // Check hero collisions with eggs
            for (Egg egg : eggs) {
                if (hero.overlaps(egg) && !egg.isDestroyed()) {
                    egg.destroy(); // Collect the egg (points added in removeDestroyedEntities)
                }
            }
        }
        
        // Update all entities
        for (Enemy enemy : enemies) {
            // Provide player position to tracker enemies
            if (hero != null && enemy.getType() == Enemy.EnemyType.TRACKER) {
                enemy.setPlayerPosition(hero.getPosition());
            }
            enemy.update(delta);
        }
        
        for (Platform platform : platforms) {
            platform.update(delta);
        }
        
        for (Egg egg : eggs) {
            egg.update(delta);
        }
        
        // Only use collision manager for enemy-platform and egg-platform collisions
        for (Enemy enemy : enemies) {
            for (Platform platform : platforms) {
                if (enemy.overlaps(platform)) {
                    collisionManager.handleEnemyPlatformCollision(enemy, platform);
                }
            }
        }
        
        for (Egg egg : eggs) {
            for (Platform platform : platforms) {
                if (egg.overlaps(platform)) {
                    collisionManager.handleEggPlatformCollision(egg, platform);
                }
            }
        }
        
        // Remove destroyed entities and handle enemy deaths
        removeDestroyedEntities();
        
        // Check if level is completed after removing entities
        checkLevelCompletion();
    }
    
    /**
     * Render all game entities
     */
    private void renderEntities() {
        // Render platforms first (background)
        for (Platform platform : platforms) {
            platform.render(batch);
        }
        
        // Render hero
        if (hero != null) {
            hero.render(batch);
        }
        
        // Render enemies
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
        
        // Render eggs
        for (Egg egg : eggs) {
            egg.render(batch);
        }
    }
    
    /**
     * Remove destroyed entities from arrays and handle enemy death (drop eggs)
     */
    private void removeDestroyedEntities() {
        // Remove destroyed enemies and create eggs
        ArrayList<Enemy> toRemoveEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isDestroyed()) {
                toRemoveEnemies.add(enemy);
                // Create egg at enemy position when killed, storing enemy type for respawning
                Egg egg = new Egg(new Vector2(enemy.getPosition().x, enemy.getPosition().y), enemy.getType());
                eggs.add(egg);
                // Add points for killing enemy
                addPoints(POINTS_FOR_ENEMY_KILL);
                Gdx.app.log("GameScreen", "Enemy destroyed at (" + enemy.getPosition().x + ", " + enemy.getPosition().y + ") - creating egg");
            }
        }
        if (toRemoveEnemies.size() > 0) {
            enemies.removeAll(toRemoveEnemies);
            Gdx.app.log("GameScreen", "Removed " + toRemoveEnemies.size() + " enemies. Remaining: " + enemies.size());
        }
        
        // Remove destroyed eggs (collected by player or hatched)
        ArrayList<Egg> toRemoveEggs = new ArrayList<>();
        for (Egg egg : eggs) {
            if (egg.isDestroyed()) {
                toRemoveEggs.add(egg);
                
                if (egg.shouldSpawnEnemy()) {
                    // Egg hatched - spawn new enemy of same type at egg position
                    Enemy newEnemy = new Enemy(new Vector2(egg.getPosition().x, egg.getPosition().y), egg.getEnemyType());
                    newEnemy.setTutorialMode(tutorial);
                    enemies.add(newEnemy);
                    Gdx.app.log("GameScreen", "Egg hatched - spawning new " + egg.getEnemyType() + " enemy");
                } else {
                    // Egg was collected by player
                    addPoints(POINTS_FOR_EGG);
                    Gdx.app.log("GameScreen", "Egg collected - removing from game");
                }
            }
        }
        if (toRemoveEggs.size() > 0) {
            eggs.removeAll(toRemoveEggs);
            Gdx.app.log("GameScreen", "Removed " + toRemoveEggs.size() + " eggs. Remaining: " + eggs.size());
        }
        
        // Remove destroyed platforms (health blocks, breakable platforms)
        ArrayList<Platform> toRemovePlatforms = new ArrayList<>();
        for (Platform platform : platforms) {
            if (platform.isDestroyed()) {
                toRemovePlatforms.add(platform);
            }
        }
        if (toRemovePlatforms.size() > 0) {
            platforms.removeAll(toRemovePlatforms);
            Gdx.app.log("GameScreen", "Removed " + toRemovePlatforms.size() + " platforms");
        }
    }
    
    /**
     * Load a specific level - UPDATED for new LevelManager
     */
    public void loadLevel(int levelNumber) {
        this.currentLevel = levelNumber;
        
        // Clear existing entities
        gameObjects.clear();
        enemies.clear();
        platforms.clear();
        eggs.clear();
        
        // Set tutorial mode for level 0
        levelLoader.setTutorial(levelNumber == 0);
        
        // Load level data using original FileReader methodology
        LevelData levelData = levelLoader.getObjectsFromFile(levelNumber + "level");
        
        // Set hero from level data
        hero = levelData.hero;
        if (hero != null) {
            gameObjects.add(hero);
            hero.setVelocity(0, 0);
            hero.resetDeathFlag(); // Clear any death flags
        }
        
        // Add platforms to both collections
        for (Platform platform : levelData.platforms) {
            platforms.add(platform);
            gameObjects.add(platform);
        }
        
        // Add enemies to both collections
        for (Enemy enemy : levelData.enemies) {
            enemies.add(enemy);
            gameObjects.add(enemy);
        }
        
        // Add eggs to both collections (usually empty at level start)
        for (Egg egg : levelData.eggs) {
            eggs.add(egg);
            gameObjects.add(egg);
        }
        
        Gdx.app.log("GameScreen", "Loaded level " + levelNumber + " with " + 
                   platforms.size() + " platforms and " + enemies.size() + " enemies, hero at (" +
                   (hero != null ? hero.getPosition().x + ", " + hero.getPosition().y : "null") + ")");
    }
    
    private void renderGame() {
        // Render all entities
        renderEntities();
    }
    
    private void renderTutorial() {
        // Render the game first (platforms, enemies, etc.)
        renderGame();
        
        // Then overlay the tutorial text (repositioned to avoid covering game elements)
        largeFont.draw(batch, "Welcome to Joust!", 250, 650);
        
        // Top area text - collision rules
        font.draw(batch, "During Collisions, if you are higher", 80, 600);
        font.draw(batch, "than the enemy it will die. Don't hit", 80, 575);
        font.draw(batch, "them while they are higher!", 80, 550);
        
        // Right side text - health box instruction  
        font.draw(batch, "Use Arrow Keys to fly", 500, 450);
        font.draw(batch, "up to this health box!", 500, 425);
        
        // Bottom area text - objectives and controls
        font.draw(batch, "Kill all enemies to move", 80, 200);
        font.draw(batch, "to the next level!", 80, 175);
        
        font.draw(batch, "Press 'N' to quick restart!", 80, 130);
        
        font.draw(batch, "Watch out for blocks", 450, 130);
        font.draw(batch, "with special properties!", 450, 105);
        
        // Instructions to proceed - moved to clear center area
        font.setColor(Color.YELLOW);
        font.draw(batch, "Press ENTER to begin playing!", 230, 350);
        font.setColor(Color.WHITE);
    }
    
    private void renderGameOver() {
        largeFont.draw(batch, "GAME OVER", 150, 500);
        font.draw(batch, "Press 'N' to quick restart!", 150, 400);
        
        // TODO: Render game over images like original
    }
    
    private void renderUI() {
        // Draw score and lives - positioned for 800x800 window
        String scoreText = "Points: " + points + "  ||  Lives: " + lives;
        font.draw(batch, scoreText, 100, WORLD_HEIGHT - 30); // Moved down slightly from top
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Use viewport for proper aspect ratio handling
        camera.update();
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (largeFont != null) largeFont.dispose();
        batch.dispose();
    }
    
    // Entity management methods
    public void addEntity(GameObject entity) {
        gameObjects.add(entity);
        
        if (entity instanceof Hero) {
            hero = (Hero) entity;
        } else if (entity instanceof Enemy) {
            enemies.add((Enemy) entity);
        } else if (entity instanceof Platform) {
            platforms.add((Platform) entity);
        } else if (entity instanceof Egg) {
            eggs.add((Egg) entity);
        }
    }
    
    public void removeEntity(GameObject entity) {
        gameObjects.remove(entity);
        
        if (entity instanceof Enemy) {
            enemies.remove((Enemy) entity);
        } else if (entity instanceof Platform) {
            platforms.remove((Platform) entity);
        } else if (entity instanceof Egg) {
            eggs.remove((Egg) entity);
        }
    }
    
    // Game state methods
    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
    }
    
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameOver = true;
        }
    }
    
    /**
     * Handle player death - lose life and restart level or game over
     */
    private void playerDied() {
        lives--;
        hero.resetDeathFlag(); // Clear death flag
        
        if (lives <= 0) {
            gameOver = true;
        } else {
            // Restart current level
            restartLevel();
        }
    }
    
    /**
     * Restart the current level (when player dies but has lives left)
     */
    private void restartLevel() {
        // Clear all entities
        gameObjects.clear();
        enemies.clear();
        platforms.clear();
        eggs.clear();
        
        // Reload the same level
        loadLevel(currentLevel);
    }
    
    public void eggCollected() {
        addPoints(POINTS_FOR_EGG);
    }
    
    // Getters
    public Hero getHero() { return hero; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<Platform> getPlatforms() { return platforms; }
    public ArrayList<Egg> getEggs() { return eggs; }
    public int getCurrentLevel() { return currentLevel; }
    public int getPoints() { return points; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }
    
    private void checkLevelCompletion() {
        // Check if all enemies are defeated and all eggs collected
        Gdx.app.log("GameScreen", "Checking level completion: " + enemies.size() + " enemies, " + eggs.size() + " eggs remaining");
        
        if (enemies.isEmpty() && eggs.isEmpty()) {
            Gdx.app.log("GameScreen", "Level completed! Moving to next level");
            if (currentLevel < NUM_LEVELS - 1) {
                loadNextLevel();
            } else {
                // Game completed - all levels finished
                Gdx.app.log("GameScreen", "All levels completed! Game finished");
                gameOver = true;
            }
        }
    }
    
    private void levelCompleted() {
        if (currentLevel < NUM_LEVELS - 1) {
            tutorial = false;
            loadNextLevel();
        } else {
            // All levels completed - game finished
            Gdx.app.log("GameScreen", "All levels completed! Game finished");
            gameOver = true;
        }
    }
    
    private void loadNextLevel() {
        currentLevel++;
        tutorial = false;
        Gdx.app.log("GameScreen", "Advancing to level " + currentLevel);
        loadLevel(currentLevel);
    }
    
    private void initializeGame() {
        // Initialize game state
        points = 0;
        lives = 4;
        gameOver = false;
        gameTime = 0;
        loadLevel(currentLevel);
    }
    
    private void setupInput() {
        // Initialize input manager
        inputManager = new InputManager();
        
        // Initialize fonts
        font = new BitmapFont(); // Default font
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        
        largeFont = new BitmapFont();
        largeFont.getData().setScale(3f);
        largeFont.setColor(Color.WHITE);
        
        // Initialize game arrays
        gameObjects = new ArrayList<>();
        enemies = new ArrayList<>();
        platforms = new ArrayList<>();
        eggs = new ArrayList<>();
    }
} 