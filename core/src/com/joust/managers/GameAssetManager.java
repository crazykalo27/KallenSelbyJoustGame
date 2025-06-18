package com.joust.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.google.gwt.core.client.GWT;

//DEBUG
//import com.badlogic.gdx.Application;


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
    private boolean isGwtPlatform;
    
    private GameAssetManager() {
        manager = new AssetManager();
        textures = new ObjectMap<>();
        pendingAssets = new Array<>();
        isGwtPlatform = Gdx.app.getType().toString().equals("WebGL");
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
        
        // Game over textures
        queueTexture("gameover0", "images/gameover0.png");
        queueTexture("gameover1", "images/gameover1.png");
        queueTexture("gameover2", "images/gameover2.png");
        queueTexture("gameover3", "images/gameover3.png");
        
        Gdx.app.log("GameAssetManager", "Queued " + pendingAssets.size + " assets for loading");
    }
    
    private void queueTexture(String name, String fileName) {
        try {
            if (isGwtPlatform) {
                // In GWT, we need to ensure the asset is in the preload list
                boolean exists = true; // Assets are embedded in GWT
                if (!exists) {
                    Gdx.app.error("GameAssetManager", "[GWT] Asset not found in preload list: " + fileName);
                    return;
                }
            } else {
                // Desktop platform - check if file exists
                //DEBUG
                // Gdx.app.log("Debug", "Trying to load: " + fileName);
                // if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
                //     System.out.println("[WEB DEBUG] Trying to load: " + fileName);
                // }
                
                
                FileHandle file = Gdx.files.internal(fileName);
                if (!file.exists()) {
                    Gdx.app.error("GameAssetManager", "Asset file not found: " + fileName);
                    return;
                }
            }
            
            manager.load(fileName, Texture.class);
            pendingAssets.add(name + "|" + fileName);
            Gdx.app.log("GameAssetManager", "Queued " + fileName + " as " + name);
        } catch (Exception e) {
            String errorMsg = "Error queuing " + fileName + ": " + e.getMessage();
            Gdx.app.error("GameAssetManager", errorMsg);
            if (isGwtPlatform) {
                handleGwtError(errorMsg);
            }
        }
    }
    
    /**
     * Update asset loading progress (call this every frame)
     * @return true if all assets are loaded
     */
    public boolean updateLoading() {
        if (assetsLoaded) return true;
        
        try {
            // Update manager (processes queued assets)
            boolean isFinished = manager.update();
            
            if (isFinished && !assetsLoaded) {
                Gdx.app.log("GameAssetManager", "All assets loaded, creating texture regions...");
                createAllTextureRegions();
                assetsLoaded = true;
                Gdx.app.log("GameAssetManager", "Asset loading complete! " + textures.size + " textures ready");
            }
            
            return assetsLoaded;
        } catch (Exception e) {
            String errorMsg = "Error updating asset load: " + e.getMessage();
            Gdx.app.error("GameAssetManager", errorMsg);
            if (isGwtPlatform) {
                handleGwtError(errorMsg);
            }
            return false;
        }
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
                        String errorMsg = "Asset not loaded: " + fileName;
                        Gdx.app.error("GameAssetManager", errorMsg);
                        if (isGwtPlatform) {
                            handleGwtError(errorMsg);
                        }
                    }
                } catch (Exception e) {
                    String errorMsg = "Error creating texture region for " + name + ": " + e.getMessage();
                    Gdx.app.error("GameAssetManager", errorMsg);
                    if (isGwtPlatform) {
                        handleGwtError(errorMsg);
                    }
                }
            }
        }
    }
    
    private native void handleGwtError(String message) /*-{
        if ($wnd.handleGameError) {
            $wnd.handleGameError(message);
        }
    }-*/;
    
    public TextureRegion getTexture(String name) {
        if (!assetsLoaded) {
            String errorMsg = "Assets not loaded yet, returning null for: " + name;
            Gdx.app.error("GameAssetManager", errorMsg);
            if (isGwtPlatform) {
                handleGwtError(errorMsg);
            }
            return null;
        }
        
        TextureRegion region = textures.get(name);
        if (region == null) {
            String errorMsg = "Texture not found: " + name;
            Gdx.app.error("GameAssetManager", errorMsg);
            if (isGwtPlatform) {
                handleGwtError(errorMsg);
            }
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