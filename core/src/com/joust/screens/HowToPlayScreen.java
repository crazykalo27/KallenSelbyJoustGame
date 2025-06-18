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
 * How To Play Screen - Game rules and objectives
 */
public class HowToPlayScreen implements Screen {
    private final JoustGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont headerFont;
    private final BitmapFont bodyFont;
    private final BitmapFont backFont;
    private boolean enterPressed = false;
    
    public HowToPlayScreen(JoustGame game) {
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
        titleFont.draw(batch, "HOW TO PLAY", 
                camera.viewportWidth / 2 - 160, 750);
        
        // Objective - Better positioning
        headerFont.draw(batch, "OBJECTIVE:", 80, 680);
        bodyFont.draw(batch, "• Defeat all enemies to advance to the next level", 80, 635);
        bodyFont.draw(batch, "• Survive as long as possible!", 80, 600);
        
        // Combat Rules - Better spacing
        headerFont.draw(batch, "COMBAT RULES:", 80, 540);
        bodyFont.draw(batch, "• Higher position wins in combat", 80, 495);
        bodyFont.draw(batch, "• Land on enemies to defeat them", 80, 460);
        bodyFont.draw(batch, "• Defeated enemies become eggs", 80, 425);
        bodyFont.draw(batch, "• Collect eggs for bonus points", 80, 390);
        bodyFont.draw(batch, "• Eggs hatch into new enemies if ignored", 80, 355);
        
        // Special Platforms - Better spacing
        headerFont.draw(batch, "SPECIAL PLATFORMS:", 80, 295);
        bodyFont.draw(batch, "• Ice platforms make you slide", 80, 250);
        bodyFont.draw(batch, "• Lava platforms are deadly", 80, 215);
        bodyFont.draw(batch, "• Health platforms restore life", 80, 180);
        bodyFont.draw(batch, "• Slime platforms slow you down", 80, 145);
        
        // Tips - Better positioning
        headerFont.draw(batch, "STRATEGY TIPS:", 80, 105);
        bodyFont.draw(batch, "• Stay high to have combat advantage", 80, 60);
        
        // Back instruction - Better positioned
        backFont.draw(batch, "Press ENTER to return to Main Menu", 
                camera.viewportWidth / 2 - 200, 20);
        
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
        backFont.dispose();
    }
} 