package com.joust;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joust.managers.GameAssetManager;
import com.joust.screens.MenuScreen;

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
            
            // Set initial screen WITHOUT asset manager initially
            setScreen(new MenuScreen(this));
            System.out.println("JoustGame: MenuScreen set");
            
            System.out.println("JoustGame: Initialization complete");
            
        } catch (Exception e) {
            System.err.println("JoustGame: Error during initialization: " + e.getMessage());
            e.printStackTrace();
            Gdx.app.exit();
        }
    }
    
    public void initializeAssets() {
        try {
            if (assetManager == null) {
                System.out.println("JoustGame: Initializing asset manager...");
                assetManager = GameAssetManager.getInstance();
                System.out.println("JoustGame: Asset manager initialized");
            }
        } catch (Exception e) {
            System.err.println("JoustGame: Error initializing assets: " + e.getMessage());
            e.printStackTrace();
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
        if (assetManager == null) {
            initializeAssets();
        }
        return assetManager;
    }
} 