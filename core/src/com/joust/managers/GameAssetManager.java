package com.joust.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

/**
 * AssetManager - handles loading and managing all game assets
 * Replaces the old ResourceManager from the Swing version
 */
public class GameAssetManager implements Disposable {
    private static GameAssetManager instance;
    private final AssetManager manager;
    private final ObjectMap<String, TextureRegion> textures;
    
    private GameAssetManager() {
        manager = new AssetManager();
        textures = new ObjectMap<>();
        loadTextures();
    }
    
    public static GameAssetManager getInstance() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }
    
    private void loadTextures() {
        // Load hero textures (using DigDug sprites)
        loadTexture("hero_left", "images/DigDugLeft.png");
        loadTexture("hero_right", "images/DigDugRight.PNG");
        
        // Load enemy textures
        loadTexture("ghost_left", "images/WaddleDLeft.PNG");      // Ghost enemy sprites
        loadTexture("ghost_right", "images/WaddleDRight.PNG");    // Ghost enemy sprites
        loadTexture("blue_koopa_left", "images/BlueKoopaLeft.PNG");  // Koopa enemy sprites
        loadTexture("blue_koopa_right", "images/BlueKoopaRight.PNG"); // Koopa enemy sprites
        loadTexture("pacman_left", "images/PacManLeft.PNG");      // Tracker enemy sprites
        loadTexture("pacman_right", "images/PacManRight.PNG");    // Tracker enemy sprites
        
        // Load platform textures - using original naming convention
        loadTexture("platform_truss", "images/PlatTruss.PNG");    // Normal platforms
        loadTexture("platform_health", "images/PlatHealth.png");  // Health platforms
        loadTexture("platform_slime", "images/PlatSlime.png");    // Slime platforms
        loadTexture("platform_ice", "images/PlatIce.PNG");        // Ice platforms
        loadTexture("platform_lava", "images/PlatLava.PNG");      // Lava platforms
        
        // Load egg textures
        loadTexture("egg", "images/Egg.png");
        
        // Load menu textures (using placeholders for now)
        loadTexture("menu_background", "images/PlatHealth.png"); // Placeholder
        loadTexture("title", "images/PlatHealth.png"); // Placeholder
    }
    
    private void loadTexture(String name, String fileName) {
        manager.load(fileName, Texture.class);
        manager.finishLoading();
        Texture texture = manager.get(fileName, Texture.class);
        textures.put(name, new TextureRegion(texture));
    }
    
    private void createTextureRegion(String name, String fileName) {
        Texture texture = manager.get(fileName, Texture.class);
        textures.put(name, new TextureRegion(texture));
    }
    
    public TextureRegion getTexture(String name) {
        TextureRegion region = textures.get(name);
        if (region == null) {
            Gdx.app.error("GameAssetManager", "Texture not found: " + name);
            return null;
        }
        return region;
    }
    
    public void dispose() {
        manager.dispose();
        textures.clear();
    }
    
    public int getTextureCount() {
        return textures.size;
    }
    
    public Array<String> getTextureNames() {
        Array<String> names = new Array<>();
        for (String key : textures.keys()) {
            names.add(key);
        }
        return names;
    }
} 