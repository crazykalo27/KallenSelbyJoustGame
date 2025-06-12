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
            System.out.println("JoustGame: Starting initialization...");
            
            // Initialize sprite batch first
            batch = new SpriteBatch();
            System.out.println("JoustGame: SpriteBatch created");
            
            // Initialize asset manager (starts async loading)
            assetManager = GameAssetManager.getInstance();
            System.out.println("JoustGame: Asset manager initialized, starting async loading");
            
            // Show loading screen while assets load
            setScreen(new LoadingScreen(this));
            System.out.println("JoustGame: LoadingScreen set");
            
            System.out.println("JoustGame: Initialization complete");
            
        } catch (Exception e) {
            System.err.println("JoustGame: Error during initialization: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("JoustGame: Error during disposal: " + e.getMessage());
        }
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
    
    public GameAssetManager getAssetManager() {
        return assetManager;
    }
} 