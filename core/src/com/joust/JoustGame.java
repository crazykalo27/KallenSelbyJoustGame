package com.joust;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joust.managers.GameAssetManager;
import com.joust.managers.LevelLoader;
import com.joust.managers.LevelLoaderInterface;
import com.joust.screens.LoadingScreen;

//DEBUG
import com.badlogic.gdx.Application;

public class JoustGame extends Game {
    private SpriteBatch batch;
    private GameAssetManager assetManager;
    private LevelLoaderInterface levelLoader;
    
    public JoustGame(LevelLoaderInterface levelLoader) {
        this.levelLoader = levelLoader;
    }

    @Override
    public void create() {
        // Set log level to reduce output
        Gdx.app.setLogLevel(Gdx.app.LOG_INFO);

        try {
            // Initialize sprite batch first
            batch = new SpriteBatch();
            
            // Initialize asset manager (starts async loading)
            assetManager = GameAssetManager.getInstance();
            
            // Initialize level loader
            LevelLoader.getInstance().setLoader(levelLoader);
            
            // Show loading screen while assets load
            setScreen(new LoadingScreen(this));
            
            Gdx.app.debug("JoustGame", "Game initialized successfully");
            
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

    public LevelLoader getLevelLoader() {
        return LevelLoader.getInstance();
    }
} 