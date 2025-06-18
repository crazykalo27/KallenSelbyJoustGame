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
 * Controls Screen - Game controls and keyboard shortcuts
 */
public class ControlsScreen implements Screen {
    private final JoustGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont headerFont;
    private final BitmapFont bodyFont;
    private final BitmapFont keyFont;
    private final BitmapFont backFont;
    private boolean enterPressed = false;
    
    public ControlsScreen(JoustGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 800);
        this.batch = new SpriteBatch();
        
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(4.0f);
        this.titleFont.setColor(Color.YELLOW);
        
        this.headerFont = new BitmapFont();
        this.headerFont.getData().setScale(2.5f);
        this.headerFont.setColor(Color.CYAN);
        
        this.bodyFont = new BitmapFont();
        this.bodyFont.getData().setScale(2.0f);
        this.bodyFont.setColor(Color.WHITE);
        
        this.keyFont = new BitmapFont();
        this.keyFont.getData().setScale(2.2f);
        this.keyFont.setColor(Color.LIME);
        
        this.backFont = new BitmapFont();
        this.backFont.getData().setScale(1.8f);
        this.backFont.setColor(Color.LIME);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Title
        titleFont.draw(batch, "CONTROLS", 
                camera.viewportWidth / 2 - 120, 750);
        
        // Movement Controls
        headerFont.draw(batch, "MOVEMENT:", 120, 650);
        keyFont.draw(batch, "↑", 120, 600);
        bodyFont.draw(batch, "Fly / Jump", 160, 600);
        
        keyFont.draw(batch, "←", 120, 550);
        bodyFont.draw(batch, "Move Left", 160, 550);
        
        keyFont.draw(batch, "→", 120, 500);
        bodyFont.draw(batch, "Move Right", 160, 500);
        
        // Game Controls
        headerFont.draw(batch, "GAME CONTROLS:", 120, 420);
        keyFont.draw(batch, "N", 120, 370);
        bodyFont.draw(batch, "Restart Level", 160, 370);
        
        keyFont.draw(batch, "ESC", 120, 320);
        bodyFont.draw(batch, "Quit Game", 160, 320);
        
        keyFont.draw(batch, "ENTER", 120, 270);
        bodyFont.draw(batch, "Select / Confirm", 160, 270);
        
        // Tips
        headerFont.draw(batch, "TIPS:", 120, 200);
        bodyFont.draw(batch, "• Hold UP to fly continuously", 120, 160);
        bodyFont.draw(batch, "• Use momentum for better control", 120, 130);
        bodyFont.draw(batch, "• Practice timing your landings", 120, 100);
        
        // Back instruction
        backFont.draw(batch, "Press ENTER to return to Main Menu", 
                camera.viewportWidth / 2 - 180, 15);
        
        batch.end();
        
        // Handle input
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && !enterPressed) {
            enterPressed = true;
            game.setScreen(new MenuScreen(game));
            dispose();
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            enterPressed = false;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void show() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        headerFont.dispose();
        bodyFont.dispose();
        keyFont.dispose();
        backFont.dispose();
    }
} 