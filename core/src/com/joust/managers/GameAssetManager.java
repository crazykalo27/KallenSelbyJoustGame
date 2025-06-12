package com.joust.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;

/**
 * AssetManager - handles loading and managing all game assets
 * Web-compatible with asynchronous loading
 */
public class GameAssetManager implements Disposable {
    private static GameAssetManager instance;
    private final AssetManager manager;
    private final ObjectMap<String, TextureRegion> textures;
    private final Array<String> pendingAssets;
    private boolean assetsLoaded = false;
    
    private GameAssetManager() {
        manager = new AssetManager();
        textures = new ObjectMap<>();
        pendingAssets = new Array<>();
        startAsyncLoading();
    }
    
    public static GameAssetManager getInstance() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }
    
    private void startAsyncLoading() {
        Gdx.app.log("GameAssetManager", "Starting async asset loading...");
        
        // Queue all assets for loading (non-blocking)
        queueTexture("hero_left", "images/DigDugLeft.png");
        queueTexture("hero_right", "images/DigDugRight.PNG");
        
        // Enemy textures
        queueTexture("ghost_left", "images/WaddleDLeft.PNG");
        queueTexture("ghost_right", "images/WaddleDRight.PNG");
        queueTexture("blue_koopa_left", "images/BlueKoopaLeft.PNG");
        queueTexture("blue_koopa_right", "images/BlueKoopaRight.PNG");
        queueTexture("pacman_left", "images/PacManLeft.PNG");
        queueTexture("pacman_right", "images/PacManRight.PNG");
        
        // Platform textures
        queueTexture("platform_truss", "images/PlatTruss.PNG");
        queueTexture("platform_health", "images/PlatHealth.png");
        queueTexture("platform_slime", "images/PlatSlime.png");
        queueTexture("platform_ice", "images/PlatIce.PNG");
        queueTexture("platform_lava", "images/PlatLava.PNG");
        
        // Egg texture
        queueTexture("egg", "images/Egg.png");
        
        // Menu textures (placeholders)
        queueTexture("menu_background", "images/PlatHealth.png");
        queueTexture("title", "images/PlatHealth.png");
        
        Gdx.app.log("GameAssetManager", "Queued " + pendingAssets.size + " assets for loading");
    }
    
    private void queueTexture(String name, String fileName) {
        try {
            manager.load(fileName, Texture.class);
            pendingAssets.add(name + "|" + fileName); // Store mapping
            Gdx.app.log("GameAssetManager", "Queued " + fileName + " as " + name);
        } catch (Exception e) {
            Gdx.app.error("GameAssetManager", "Error queuing " + fileName + ": " + e.getMessage());
        }
    }
    
    /**
     * Update asset loading progress (call this every frame)
     * @return true if all assets are loaded
     */
    public boolean updateLoading() {
        if (assetsLoaded) return true;
        
        // Update manager (processes queued assets)
        boolean isFinished = manager.update();
        
        if (isFinished && !assetsLoaded) {
            Gdx.app.log("GameAssetManager", "All assets loaded, creating texture regions...");
            createAllTextureRegions();
            assetsLoaded = true;
            Gdx.app.log("GameAssetManager", "Asset loading complete! " + textures.size + " textures ready");
        }
        
        return assetsLoaded;
    }
    
    private void createAllTextureRegions() {
        for (String assetMapping : pendingAssets) {
            String[] parts = assetMapping.split("\\|");
            if (parts.length == 2) {
                String name = parts[0];
                String fileName = parts[1];
                
                try {
                    if (manager.isLoaded(fileName)) {
                        Texture texture = manager.get(fileName, Texture.class);
                        textures.put(name, new TextureRegion(texture));
                        Gdx.app.log("GameAssetManager", "Created texture region: " + name);
                    } else {
                        Gdx.app.error("GameAssetManager", "Asset not loaded: " + fileName);
                    }
                } catch (Exception e) {
                    Gdx.app.error("GameAssetManager", "Error creating texture region for " + name + ": " + e.getMessage());
                }
            }
        }
    }
    
    public TextureRegion getTexture(String name) {
        if (!assetsLoaded) {
            Gdx.app.error("GameAssetManager", "Assets not loaded yet, returning null for: " + name);
            return null;
        }
        
        TextureRegion region = textures.get(name);
        if (region == null) {
            Gdx.app.error("GameAssetManager", "Texture not found: " + name);
        }
        return region;
    }
    
    public boolean isLoaded() {
        return assetsLoaded;
    }
    
    public float getProgress() {
        return manager.getProgress();
    }
    
    public void dispose() {
        manager.dispose();
        textures.clear();
        pendingAssets.clear();
        assetsLoaded = false;
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