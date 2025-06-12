package com.joust.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joust.JoustGame;

/**
 * Simple, Crash-Resistant Menu Screen
 * Minimal graphics to avoid native OpenGL crashes
 */
public class MenuScreen implements Screen {
    private final JoustGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private boolean enterPressed = false;
    
    // Simple menu state
    private int selectedOption = 0;
    private final String[] menuOptions = {
        "START GAME",
        "EXIT"
    };
    
    private float blinkTimer = 0f;
    
    public MenuScreen(JoustGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 800);
        this.batch = new SpriteBatch();
        
        // Use single, simple font to avoid crashes
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.font.setColor(Color.WHITE);
    }
    
    @Override
    public void render(float delta) {
        blinkTimer += delta;
        
        // Handle input first
        handleInput();
        
        // Simple rendering
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Title - simple and safe
        font.setColor(Color.YELLOW);
        font.draw(batch, "JOUST", 350, 600);
        
        // Subtitle
        font.setColor(Color.CYAN);
        font.draw(batch, "Classic Arcade Game", 280, 550);
        
        // Menu options - simple list
        float startY = 400;
        float spacing = 60;
        
        for (int i = 0; i < menuOptions.length; i++) {
            float y = startY - (i * spacing);
            
            if (i == selectedOption) {
                // Selected option - blink effect
                if ((blinkTimer % 1.0f) < 0.5f) {
                    font.setColor(Color.LIME);
                    font.draw(batch, "> " + menuOptions[i] + " <", 320, y);
                }
            } else {
                font.setColor(Color.WHITE);
                font.draw(batch, menuOptions[i], 350, y);
            }
        }
        
        // Instructions
        font.setColor(Color.GRAY);
        font.draw(batch, "UP/DOWN: Navigate   ENTER: Select   ESC: Exit", 200, 100);
        
        batch.end();
    }
    
    private void handleInput() {
        // Navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }
        
        // Selection
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && !enterPressed) {
            enterPressed = true;
            selectOption();
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            enterPressed = false;
        }
        
        // Quick exit
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
    
    private void selectOption() {
        switch (selectedOption) {
            case 0: // START GAME
                try {
                    game.setScreen(new GameScreen(game));
                    dispose();
                } catch (Exception e) {
                    Gdx.app.error("MenuScreen", "Error starting game: " + e.getMessage(), e);
                    // Don't crash, just stay on menu
                }
                break;
            case 1: // EXIT
                Gdx.app.exit();
                break;
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void show() {
        Gdx.app.log("MenuScreen", "MenuScreen shown - Simple mode active");
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
    }
} 