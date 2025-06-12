package com.joust.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.joust.JoustGame;

/**
 * Loading Screen - Shows asset loading progress and transitions to MenuScreen when complete
 */
public class LoadingScreen implements Screen {
    private final JoustGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    
    private float loadingTimer = 0f;
    private final String[] loadingDots = {"", ".", "..", "..."};
    
    public LoadingScreen(JoustGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 800);
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.font.setColor(Color.WHITE);
        
        System.out.println("LoadingScreen: Initialized");
    }
    
    @Override
    public void render(float delta) {
        loadingTimer += delta;
        
        // Update asset loading
        boolean assetsLoaded = game.getAssetManager().updateLoading();
        float progress = game.getAssetManager().getProgress();
        
        // Check if loading is complete
        if (assetsLoaded) {
            System.out.println("LoadingScreen: Assets loaded, transitioning to MenuScreen");
            game.setScreen(new MenuScreen(game));
            dispose();
            return;
        }
        
        // Clear screen
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Render loading UI
        batch.begin();
        
        // Title
        font.setColor(Color.YELLOW);
        font.draw(batch, "JOUST", 350, 600);
        
        // Loading text with animated dots
        String loadingText = "Loading Assets" + loadingDots[(int)(loadingTimer * 2) % loadingDots.length];
        font.setColor(Color.WHITE);
        font.draw(batch, loadingText, 300, 450);
        
        // Progress percentage
        font.setColor(Color.CYAN);
        int progressPercent = (int)(progress * 100);
        font.draw(batch, progressPercent + "%", 375, 350);
        
        // Instructions
        font.setColor(Color.GRAY);
        font.getData().setScale(1.0f);
        font.draw(batch, "Please wait while game assets are loading...", 250, 150);
        font.getData().setScale(2.0f);
        
        batch.end();
        
        // Progress bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Background bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(200, 300, 400, 20);
        
        // Progress bar
        shapeRenderer.setColor(Color.LIME);
        shapeRenderer.rect(200, 300, 400 * progress, 20);
        
        shapeRenderer.end();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void show() {
        System.out.println("LoadingScreen: Shown");
    }
    
    @Override
    public void hide() {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        System.out.println("LoadingScreen: Disposed");
    }
} 