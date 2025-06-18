package com.joust.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.joust.entities.Enemy;
import com.joust.entities.Hero;
import com.joust.entities.Platform;
import com.joust.entities.Platform.PlatformType;
import com.joust.entities.Egg;

import java.util.ArrayList;
import java.util.function.Consumer;

//DEBUG
//import com.badlogic.gdx.Application;

/**
 * LevelLoader - EXACT port of original Swing FileReader
 * Handles loading and parsing level files using original methodology
 * Properly handles coordinate system conversion from Swing (Y-down) to LibGDX (Y-up)
 */
public class LevelLoader {
    
    private static LevelLoader instance;
    private LevelLoaderInterface loader;
    
    private boolean tutorial;
    
    // EXACT constants from original Swing FileReader
    private static final String AIR_STRING = ".";
    private static final String PLATFORM_STRING = "o";
    private static final String HERO_STRING = "h";
    private static final String GHOST_STRING = "b";           // Ghost enemy (stationary in tutorial)
    private static final String KOOPA_STRING = "e";           // Random move enemy (flies randomly)
    private static final String TRACKER_STRING = "t";         // Tracker enemy (follows player)
    private static final String LAVA_STRING = "l";
    private static final String ICE_STRING = "i";
    private static final String SLIME_STRING = "s";
    private static final String REGEN_STRING = "c";           // Health platform
    private static final int COORDINATE_SCALE = 50;           // Each grid cell = 50x50 pixels
    
    // World dimensions - match level size (16x16 grid at 50px each = 800x800)
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 800f;
    
    // Data collections (matching original FileReader structure)
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Platform> platforms = new ArrayList<Platform>();
    private Hero hero;
    
    private LevelLoader() {
        this.tutorial = false;
        Gdx.app.log("LevelLoader", "🔥🔥🔥 NEW LEVELLOADER CONSTRUCTOR CALLED - CHANGES ARE ACTIVE! 🔥🔥🔥");
    }
    
    public static LevelLoader getInstance() {
        if (instance == null) {
            instance = new LevelLoader();
        }
        return instance;
    }

    public void setLoader(LevelLoaderInterface loader) {
        if (loader == null) {
            throw new IllegalArgumentException("LevelLoader cannot be null");
        }
        this.loader = loader;
    }
    
    /**
     * Synchronously loads a level by number. This is primarily for desktop platforms.
     * For web/HTML platform, use loadLevelAsync instead.
     * 
     * @param levelNumber The level number to load
     * @return The loaded level data
     * @throws RuntimeException if loading fails or loader is not set
     */
    public LevelData getObjectsFromFile(String levelNumber) {
        if (loader == null) {
            throw new IllegalStateException("Loader not set");
        }

        // Strip non-numeric characters from levelNumber
        String cleanNumber = levelNumber.replaceAll("[^0-9]", "");
        if (cleanNumber.isEmpty()) {
            throw new IllegalArgumentException("Invalid level number format: " + levelNumber);
        }

        // For HTML platform, use async loading
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            final LevelData[] result = new LevelData[1];
            loader.loadLevel(Integer.parseInt(cleanNumber), content -> {
                try {
                    ArrayList<ArrayList<String>> chars = parseContentToGrid(content);
                    result[0] = convertStringsToObjects(chars);
                } catch (Exception e) {
                    Gdx.app.error("LevelLoader", "Failed to load level: " + e.getMessage());
                    result[0] = createEmptyLevel();
                }
            }, throwable -> {
                Gdx.app.error("LevelLoader", "Failed to load level: " + throwable.getMessage());
                result[0] = createEmptyLevel();
            });
            return result[0] != null ? result[0] : createEmptyLevel();
        }

        // For desktop platform, use synchronous loading
        try {
            final LevelData[] result = new LevelData[1];
            loader.loadLevel(Integer.parseInt(cleanNumber), content -> {
                ArrayList<ArrayList<String>> chars = parseContentToGrid(content);
                result[0] = convertStringsToObjects(chars);
            }, throwable -> {
                Gdx.app.error("LevelLoader", "Failed to load level: " + throwable.getMessage());
                result[0] = createEmptyLevel();
            });
            return result[0] != null ? result[0] : createEmptyLevel();
        } catch (Exception e) {
            Gdx.app.error("LevelLoader", "Failed to load level: " + e.getMessage());
            return createEmptyLevel();
        }
    }

    /**
     * Asynchronously loads a level. This is the preferred method for web/HTML platform.
     */
    public void loadLevelAsync(int levelNumber, Consumer<LevelData> onSuccess, Consumer<Throwable> onFailure) {
        if (loader == null) {
            onFailure.accept(new IllegalStateException("LevelLoader not initialized"));
            return;
        }
        
        loader.loadLevel(levelNumber, levelContent -> {
            try {
                ArrayList<ArrayList<String>> chars = parseContentToGrid(levelContent);
                onSuccess.accept(convertStringsToObjects(chars));
            } catch (Exception e) {
                onFailure.accept(e);
            }
        }, onFailure);
    }

    /**
     * Parses level content string into a 2D grid of characters
     */
    private ArrayList<ArrayList<String>> parseContentToGrid(String content) {
        ArrayList<ArrayList<String>> chars = new ArrayList<>();
        String[] lines = content.split("\n");
        
        Gdx.app.log("LevelLoader", "Parsing " + lines.length + " lines");
        
        for (String line : lines) {
            // Remove carriage returns and trim whitespace
            line = line.replaceAll("\\r", "").trim();
            if (line.isEmpty()) continue; // Skip empty lines
            
            String[] fromLine = line.split("");
            
            ArrayList<String> temp = new ArrayList<>();
            for (String character : fromLine) {
                temp.add(character);
            }
            chars.add(temp);
        }
        
        return chars;
    }

    private LevelData convertStringsToObjects(ArrayList<ArrayList<String>> change) {
        // Create new collections for this level load
        ArrayList<Enemy> enemies = new ArrayList<>();
        ArrayList<Platform> platforms = new ArrayList<>();
        Hero hero = null;
        
        // Calculate level dimensions
        int levelRows = change.size(); // Should be 16
        int levelCols = change.get(0).size(); // Should be 16
        
        // LibGDX coordinate system: Y=0 at bottom, Y increases upward
        // We need to flip the Y coordinates to match the target layout
        for(int i = 0; i < change.size(); i++) {
            ArrayList<String> row = change.get(i);
            for(int j = 0; j < row.size(); j++) {
                // Calculate coordinates with proper scaling for LibGDX
                // Convert grid coordinates (j,i) to world coordinates
                // Each grid cell is 50x50, entities are 32x32, so offset by 25px to center
                float x = j * COORDINATE_SCALE + COORDINATE_SCALE/2f - 25f; // Center and offset for size difference
                float y = (15 - i) * COORDINATE_SCALE + COORDINATE_SCALE/2f - 25f; // Center and offset for size difference
                
                String character = row.get(j);
                
                if (character.equals(AIR_STRING)) {
                    continue;
                } else if (character.equals(PLATFORM_STRING)) {
                    // Platforms are 50x50, use grid position directly
                    float platformX = j * COORDINATE_SCALE;
                    float platformY = (15 - i) * COORDINATE_SCALE;
                    platforms.add(new Platform(new Vector2(platformX, platformY), PlatformType.NORMAL));
                } else if (character.equals(HERO_STRING)) {
                    // Calculate hero position with proper centering
                    float heroX = j * COORDINATE_SCALE + COORDINATE_SCALE/2f;
                    float heroY = (15 - i) * COORDINATE_SCALE + COORDINATE_SCALE/2f;
                    hero = new Hero(heroX, heroY);
                    hero.setVelocity(0, 0); // Explicitly reset velocity on spawn
                    Gdx.app.log("LevelLoader", "Spawning HERO at grid(" + j + "," + i + ") -> world(" + heroX + "," + heroY + ")");
                } else if (character.equals(GHOST_STRING)) {
                    // Calculate ghost position using grid coordinates directly
                    float ghostX = j * COORDINATE_SCALE;
                    float ghostY = (15 - i) * COORDINATE_SCALE;
                    Gdx.app.log("LevelLoader", "Spawning GHOST at grid(" + j + "," + i + ") -> world(" + ghostX + "," + ghostY + ")");
                    Enemy ghost = new Enemy(new Vector2(ghostX, ghostY), Enemy.EnemyType.GHOST);
                    ghost.setTutorialMode(tutorial);
                    enemies.add(ghost);
                } else if (character.equals(KOOPA_STRING)) {
                    Gdx.app.log("LevelLoader", "Spawning KOOPA at grid(" + j + "," + i + ") -> world(" + x + "," + y + ")");
                    enemies.add(new Enemy(new Vector2(x, y), Enemy.EnemyType.KOOPA));
                } else if (character.equals(TRACKER_STRING)) {
                    Gdx.app.log("LevelLoader", "Spawning TRACKER at grid(" + j + "," + i + ") -> world(" + x + "," + y + ")");
                    enemies.add(new Enemy(new Vector2(x, y), Enemy.EnemyType.TRACKER));
                } else if (character.equals(LAVA_STRING)) {
                    float platformX = j * COORDINATE_SCALE;
                    float platformY = (15 - i) * COORDINATE_SCALE;
                    platforms.add(new Platform(new Vector2(platformX, platformY), PlatformType.LAVA));
                } else if (character.equals(ICE_STRING)) {
                    float platformX = j * COORDINATE_SCALE;
                    float platformY = (15 - i) * COORDINATE_SCALE;
                    platforms.add(new Platform(new Vector2(platformX, platformY), PlatformType.ICE));
                } else if (character.equals(SLIME_STRING)) {
                    float platformX = j * COORDINATE_SCALE;
                    float platformY = (15 - i) * COORDINATE_SCALE;
                    platforms.add(new Platform(new Vector2(platformX, platformY), PlatformType.SLIME));
                } else if (character.equals(REGEN_STRING)) {
                    float platformX = j * COORDINATE_SCALE;
                    float platformY = (15 - i) * COORDINATE_SCALE;
                    platforms.add(new Platform(new Vector2(platformX, platformY), PlatformType.HEALTH));
                }
            }
        }
        
        // Create and return the level data
        LevelData data = new LevelData();
        data.hero = hero;
        data.platforms = platforms;
        data.enemies = enemies;
        data.eggs = new ArrayList<>();
        
        // Log only essential information
        Gdx.app.debug("LevelLoader", "Created level data: " + platforms.size() + " platforms, " + enemies.size() + " enemies");
            
        return data;
    }
    
    private LevelData createEmptyLevel() {
        return new LevelData();
    }
    
    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
    }
    
    public boolean isTutorial() {
        return tutorial;
    }
    
    /**
     * Level data container class
     */
    public static class LevelData {
        public Hero hero;
        public ArrayList<Platform> platforms;
        public ArrayList<Enemy> enemies;
        public ArrayList<Egg> eggs;
        
        public LevelData() {
            this.platforms = new ArrayList<>();
            this.enemies = new ArrayList<>();
            this.eggs = new ArrayList<>();
        }
    }
} 