package com.joust.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.joust.JoustGame;
import com.joust.managers.GameAssetManager;

/**
 * Character Screen - Shows hero and enemy information with sprites
 */
public class CharacterScreen implements Screen {
    private final JoustGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont headerFont;
    private final BitmapFont bodyFont;
    private final BitmapFont backFont;
    private final GameAssetManager assetManager;
    private boolean enterPressed = false;
    
    // Animation
    private float animationTimer = 0f;
    private static final float ANIMATION_SPEED = 1.5f;
    
    public CharacterScreen(JoustGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 800);
        this.batch = new SpriteBatch();
        this.assetManager = GameAssetManager.getInstance();
        
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(4.0f);
        this.titleFont.setColor(Color.YELLOW);
        
        this.headerFont = new BitmapFont();
        this.headerFont.getData().setScale(2.5f);
        this.headerFont.setColor(Color.CYAN);
        
        this.bodyFont = new BitmapFont();
        this.bodyFont.getData().setScale(1.8f);
        this.bodyFont.setColor(Color.WHITE);
        
        this.backFont = new BitmapFont();
        this.backFont.getData().setScale(1.8f);
        this.backFont.setColor(Color.LIME);
    }
    
    @Override
    public void render(float delta) {
        animationTimer += delta;
        
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Title
        titleFont.draw(batch, "CHARACTERS", 
                camera.viewportWidth / 2 - 150, 750);
        
        // Hero Section
        headerFont.draw(batch, "YOUR HERO:", 80, 650);
        drawCharacterSprite("hero", 80, 580);
        bodyFont.draw(batch, "Brave Knight", 80, 540);
        bodyFont.draw(batch, "• Flies with UP arrow", 80, 510);
        bodyFont.draw(batch, "• Moves with LEFT/RIGHT arrows", 80, 485);
        bodyFont.draw(batch, "• Defeats enemies by landing on them", 80, 460);
        
        // Enemy Forces
        headerFont.draw(batch, "ENEMY FORCES:", 80, 400);
        
        // Ghost
        drawCharacterSprite("ghost", 80, 330);
        bodyFont.draw(batch, "Ghost - Patrols left and right", 160, 350);
        bodyFont.draw(batch, "Moves in timed patterns", 160, 325);
        bodyFont.draw(batch, "Basic enemy type", 160, 305);
        
        // Koopa
        drawCharacterSprite("koopa", 80, 250);
        bodyFont.draw(batch, "Koopa - Jumps randomly", 160, 270);
        bodyFont.draw(batch, "Bounces off walls and platforms", 160, 245);
        bodyFont.draw(batch, "Unpredictable movement", 160, 225);
        
        // Tracker
        drawCharacterSprite("tracker", 80, 170);
        bodyFont.draw(batch, "Tracker - Follows you!", 160, 190);
        bodyFont.draw(batch, "Flies directly toward player", 160, 165);
        bodyFont.draw(batch, "Most dangerous enemy", 160, 145);
        
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
    
    private void drawCharacterSprite(String characterType, float x, float y) {
        boolean showLeft = (animationTimer % (ANIMATION_SPEED * 2)) < ANIMATION_SPEED;
        TextureRegion sprite = null;
        
        switch (characterType) {
            case "hero":
                sprite = showLeft ? assetManager.getTexture("hero_left") : assetManager.getTexture("hero_right");
                break;
            case "ghost":
                sprite = showLeft ? assetManager.getTexture("pacman_left") : assetManager.getTexture("pacman_right");
                break;
            case "koopa":
                sprite = showLeft ? assetManager.getTexture("blue_koopa_left") : assetManager.getTexture("blue_koopa_right");
                break;
            case "tracker":
                sprite = showLeft ? assetManager.getTexture("ghost_left") : assetManager.getTexture("ghost_right");
                break;
        }
        
        if (sprite != null) {
            float spriteWidth = 60f;
            float spriteHeight = 60f;
            batch.draw(sprite, x, y, spriteWidth, spriteHeight);
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