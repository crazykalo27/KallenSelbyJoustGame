package com.joust;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joust.managers.GameAssetManager;
import com.joust.screens.LoadingScreen;

public class JoustGame extends Game {
    private SpriteBatch batch;
    private GameAssetManager assetManager;
    
    @Override
    public void create() {
        try {
            Gdx.app.log("JoustGame", "Starting initialization...");
            
            // Initialize sprite batch first
            batch = new SpriteBatch();
            Gdx.app.log("JoustGame", "SpriteBatch created");
            
            // Initialize asset manager (starts async loading)
            assetManager = GameAssetManager.getInstance();
            Gdx.app.log("JoustGame", "Asset manager initialized, starting async loading");
            
            // Show loading screen while assets load
            setScreen(new LoadingScreen(this));
            Gdx.app.log("JoustGame", "LoadingScreen set");
            
            Gdx.app.log("JoustGame", "Initialization complete");
            
        } catch (Exception e) {
            Gdx.app.error("JoustGame", "Error during initialization: " + e.getMessage(), e);
            Gdx.app.exit();
        }
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        try {
            if (batch != null) {
                batch.dispose();
            }
            if (assetManager != null) {
                assetManager.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("JoustGame", "Error during disposal: " + e.getMessage(), e);
        }
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
    
    public GameAssetManager getAssetManager() {
        return assetManager;
    }
} 