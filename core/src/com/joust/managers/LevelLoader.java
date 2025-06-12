package com.joust.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.joust.entities.Enemy;
import com.joust.entities.Hero;
import com.joust.entities.Platform;
import com.joust.entities.Platform.PlatformType;
import com.joust.entities.Egg;

import java.util.ArrayList;

/**
 * LevelLoader - EXACT port of original Swing FileReader
 * Handles loading and parsing level files using original methodology
 * Properly handles coordinate system conversion from Swing (Y-down) to LibGDX (Y-up)
 */
public class LevelLoader {
    
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
    private boolean tutorial;
    
    public LevelLoader() {
        this.tutorial = false;
        Gdx.app.log("LevelLoader", "🔥🔥🔥 NEW LEVELLOADER CONSTRUCTOR CALLED - CHANGES ARE ACTIVE! 🔥🔥🔥");
    }
    
    /**
     * Main method to load level - matches original getObjectsFromFile()
     */
    public LevelData getObjectsFromFile(String filename) {
        ArrayList<ArrayList<String>> levelStringArray = readFile(filename);
        try {
            convertStringsToObjects(levelStringArray);
            return createLevelData();
        } catch (Exception e) {
            Gdx.app.error("LevelLoader", "Error loading level: " + filename, e);
            return createEmptyLevel();
        }
    }
    
    /**
     * Read level file - EXACT port of original readFile() method
     */
    private ArrayList<ArrayList<String>> readFile(String fileName) {
        ArrayList<ArrayList<String>> chars = new ArrayList<ArrayList<String>>();
        
        // Try to read from assets/levels first (LibGDX way)
        try {
            FileHandle file = Gdx.files.internal("levels/" + fileName + ".txt");
            if (file.exists()) {
                String content = file.readString();
                String[] lines = content.split("\n");
                
                Gdx.app.log("LevelLoader", "Reading " + lines.length + " lines from " + fileName);
                
                for (String line : lines) {
                    // Remove carriage returns and trim whitespace
                    line = line.replaceAll("\\r", "").trim();
                    if (line.isEmpty()) continue; // Skip empty lines
                    
                    String[] fromLine = line.split("");
                    
                    ArrayList<String> temp = new ArrayList<>();
                    for (int i = 0; i < fromLine.length; i++) {
                        temp.add(fromLine[i]);
                    }
                    chars.add(temp);
                    
                    Gdx.app.log("LevelLoader", "Line " + chars.size() + ": '" + line + "' (" + fromLine.length + " chars)");
                }
                
                return chars;
            }
        } catch (Exception e) {
            Gdx.app.error("LevelLoader", "Error reading file: " + fileName, e);
        }
        
        return new ArrayList<ArrayList<String>>();
    }
    
    /**
     * Convert 2D string array to game objects - EXACT port of original convertStringsToObjects()
     */
    private void convertStringsToObjects(ArrayList<ArrayList<String>> change) {
        // Clear collections like original
        platforms.clear();
        enemies.clear();
        hero = null;
        
        Gdx.app.log("LevelLoader", "🔥🔥🔥 NEW LEVELLOADER CONSTRUCTOR CALLED - CHANGES ARE ACTIVE! 🔥🔥🔥");
        Gdx.app.log("LevelLoader", "Converting level: " + change.size() + " rows");
        
        // Calculate level dimensions
        int levelRows = change.size(); // Should be 16
        int levelCols = change.get(0).size(); // Should be 16
        
        // LibGDX coordinate system: Y=0 at bottom, Y increases upward
        // We need to flip the Y coordinates to match the target layout
        for(int i = 0; i < change.size(); i++) {
            for(int j = 0; j < change.get(i).size(); j++) {
                // Calculate coordinates with proper scaling for LibGDX
                // Add small offsets to move level down and left for better centering
                float x = j * COORDINATE_SCALE + COORDINATE_SCALE/2f - 25f; // Move 25px left
                // CRITICAL: Flip Y-axis for LibGDX (Y=0 at bottom, not top like Swing)
                float y = (levelRows - 1 - i) * COORDINATE_SCALE + COORDINATE_SCALE/2f - 25f; // Move 25px down
                
                String character = change.get(i).get(j);
                
                // Debug key coordinates - show FINAL coordinates after offset
                if ((i < 3 && j < 3) || !character.equals(AIR_STRING)) {
                    float rawY = (levelRows - 1 - i) * COORDINATE_SCALE;
                    Gdx.app.log("LevelLoader", "GRID[" + i + "," + j + "] = '" + character + "' → FINAL(" + x + "," + y + ") [raw Y=" + rawY + " → offset Y=" + y + "]");
                }
                
                if (character.equals(AIR_STRING)) {
                    continue;
                } else if (character.equals(PLATFORM_STRING)) {
                    Platform temp = new Platform(new Vector2(x, y), PlatformType.NORMAL);
                    platforms.add(temp);
                } else if (character.equals(HERO_STRING)) {
                    hero = new Hero(x, y);
                } else if (character.equals(GHOST_STRING)) {
                    // 'b' = Ghost enemy (LeftRightEnemy - stationary in tutorial, timer-based movement otherwise)
                    Enemy temp = new Enemy(new Vector2(x, y), Enemy.EnemyType.GHOST);
                    temp.setTutorialMode(tutorial);
                    enemies.add(temp);
                } else if (character.equals(KOOPA_STRING)) {
                    // 'e' = Koopa enemy (RandomMoveEnemy - moves in direction, jumps randomly, reverses on collision)
                    Enemy temp = new Enemy(new Vector2(x, y), Enemy.EnemyType.KOOPA);
                    enemies.add(temp);
                } else if (character.equals(TRACKER_STRING)) {
                    // 't' = Tracker enemy (follows player)
                    Enemy temp = new Enemy(new Vector2(x, y), Enemy.EnemyType.TRACKER);
                    enemies.add(temp);
                } else if (character.equals(LAVA_STRING)) {
                    Platform temp = new Platform(new Vector2(x, y), PlatformType.LAVA);
                    platforms.add(temp);
                } else if (character.equals(ICE_STRING)) {
                    Platform temp = new Platform(new Vector2(x, y), PlatformType.ICE);
                    platforms.add(temp);
                } else if (character.equals(SLIME_STRING)) {
                    Platform temp = new Platform(new Vector2(x, y), PlatformType.SLIME);
                    platforms.add(temp);
                } else if (character.equals(REGEN_STRING)) {
                    Platform temp = new Platform(new Vector2(x, y), PlatformType.HEALTH);
                    platforms.add(temp);
                }
            }
        }
        
        Gdx.app.log("LevelLoader", "Level loaded: " + platforms.size() + " platforms, " + enemies.size() + " enemies");
        if (hero != null) {
            Gdx.app.log("LevelLoader", "Hero at: (" + hero.getPosition().x + "," + hero.getPosition().y + ")");
        }
    }
    
    /**
     * Create level data object
     */
    private LevelData createLevelData() {
        LevelData data = new LevelData();
        data.hero = hero;
        data.platforms = new ArrayList<Platform>(platforms);
        data.enemies = new ArrayList<Enemy>(enemies);
        data.eggs = new ArrayList<Egg>(); // Usually empty at level start
        return data;
    }
    
    /**
     * Create empty level as fallback
     */
    private LevelData createEmptyLevel() {
        LevelData data = new LevelData();
        data.hero = new Hero(100, 100);
        data.platforms = new ArrayList<Platform>();
        data.enemies = new ArrayList<Enemy>();
        data.eggs = new ArrayList<Egg>();
        return data;
    }
    
    // Getters matching original FileReader
    public Hero getHero() {
        return hero;
    }
    
    public ArrayList<Enemy> getBad() {
        return enemies;
    }
    
    public ArrayList<Platform> getPlatforms() {
        return platforms;
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
            this.platforms = new ArrayList<Platform>();
            this.enemies = new ArrayList<Enemy>();
            this.eggs = new ArrayList<Egg>();
        }
    }
} 