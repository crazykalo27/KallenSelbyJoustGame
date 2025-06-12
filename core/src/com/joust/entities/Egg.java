package com.joust.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.joust.managers.GameAssetManager;

/**
 * Egg class - appears when enemies are killed
 * After a timeout, spawns a new enemy (matching original 7-second timer)
 * Converted from Swing to LibGDX implementation
 */
public class Egg extends GameObject {
    
    private static final float EGG_WIDTH = 24;
    private static final float EGG_HEIGHT = 24;
    private static final float HATCH_TIME = 7.0f; // 7 seconds like original Swing version
    private static final float GRAVITY = 800;
    
    private float hatchTimer;
    private boolean isHatching;
    private boolean collected;
    private boolean shouldSpawnEnemy;
    private Enemy.EnemyType enemyType; // Store the type of enemy that was killed
    
    public Egg(Vector2 position, Enemy.EnemyType enemyType) {
        super(position, 16, 16);
        this.hatchTimer = 0;
        this.isHatching = true; // Start hatching immediately like original
        this.collected = false;
        this.shouldSpawnEnemy = false;
        this.enemyType = enemyType;
        setTexture(GameAssetManager.getInstance().getTexture("egg"));
    }
    
    @Override
    public void update(float deltaTime) {
        if (!collected) {
            // Apply gravity
            addVelocity(0, -GRAVITY * deltaTime);

            // Update position
            addPosition(getVelocity().x * deltaTime, getVelocity().y * deltaTime);
            updateBounds();

            // Update hatching timer
            if (isHatching) {
                hatchTimer += deltaTime;
                if (hatchTimer >= HATCH_TIME) {
                    // Signal that this egg should spawn an enemy
                    shouldSpawnEnemy = true;
                    destroy(); // This will be handled by GameScreen
                }
            }
        }
    }
    
    public void handleCollision(GameObject other) {
        if (other instanceof Platform) {
            Platform platform = (Platform) other;
            
            // Check for lava platforms - eggs are destroyed by lava (like original)
            if (platform.getType() == Platform.PlatformType.LAVA) {
                destroy();
                return;
            }
            
            // Normal platform collision for landing
            float overlapY = Math.min(
                bounds.y + bounds.height - platform.getBoundingBox().y,
                platform.getBoundingBox().y + platform.getBoundingBox().height - bounds.y
            );

            if (velocity.y < 0) { // Falling down
                position.y += overlapY;
                velocity.y = 0;
                updateBounds();
            }
        }
    }
    
    public void startHatching() {
        isHatching = true;
    }
    
    public void collect() {
        collected = true;
        destroy();
    }
    
    public boolean isHatching() {
        return isHatching;
    }
    
    public boolean isCollected() {
        return collected;
    }
    
    public boolean shouldSpawnEnemy() {
        return shouldSpawnEnemy;
    }
    
    public Enemy.EnemyType getEnemyType() {
        return enemyType;
    }
    
    public float getHatchProgress() {
        return hatchTimer / HATCH_TIME;
    }
} 