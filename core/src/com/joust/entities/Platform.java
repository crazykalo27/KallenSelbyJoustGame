package com.joust.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import com.joust.managers.GameAssetManager;

/**
 * Platform - represents platforms in the game world
 * Converted from the original Swing version to LibGDX
 */
public class Platform extends GameObject {
    
    private static final float PLATFORM_WIDTH = 50f; // Match tile size
    private static final float PLATFORM_HEIGHT = 50f; // Match tile size
    private static final float MOVE_SPEED = 100;
    
    public enum PlatformType {
        NORMAL,    // Regular platform (o)
        MOVING,    // Moving platform
        BREAKABLE, // Breakable platform
        HEALTH,    // Health platform (was PlatHealth)
        SLIME,     // Slime platform (s)
        ICE,       // Ice platform (i)
        LAVA,      // Lava platform (l)
        TRUSS      // Truss platform (t)
    }
    
    private final PlatformType type;
    private float effectTimer = 0f;
    private float moveTimer;
    private float moveRange;
    private Vector2 startPosition;
    private boolean movingRight;
    private float moveSpeed;
    private float moveDistance;
    private float startX;
    
    /**
     * Constructor with default size
     */
    public Platform(Vector2 position, PlatformType type) {
        super(position, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        this.type = type;
        this.moveTimer = 0;
        this.moveRange = 200;
        this.startPosition = new Vector2(position);
        this.movingRight = true;
        
        if (type == PlatformType.MOVING) {
            this.moveSpeed = 100f;
            this.moveDistance = 200f;
            this.startX = position.x;
        }
        
        loadTextures();
    }
    
    private void loadTextures() {
        String textureName;
        switch (type) {
            case NORMAL:
                textureName = "platform_truss"; // Normal platforms use truss texture (from original 'o' blocks)
                break;
            case HEALTH:
                textureName = "platform_health"; // PlatHealth.png
                break;
            case SLIME:
                textureName = "platform_slime"; // PlatSlime.png
                break;
            case ICE:
                textureName = "platform_ice"; // PlatIce.PNG
                break;
            case LAVA:
                textureName = "platform_lava"; // PlatLava.PNG
                break;
            case TRUSS:
                textureName = "platform_truss"; // PlatTruss.PNG
                break;
            default:
                textureName = "platform_truss";
                break;
        }
        setTexture(GameAssetManager.getInstance().getTexture(textureName));
    }
    
    @Override
    public void update(float deltaTime) {
        if (type == PlatformType.MOVING) {
            float currentX = getPosition().x;
            float targetX = movingRight ? startX + moveDistance : startX;
            
            if (Math.abs(currentX - targetX) < 5f) {
                movingRight = !movingRight;
            }
            
            float moveX = (targetX - currentX) * moveSpeed * deltaTime;
            setPosition(currentX + moveX, getPosition().y);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null) {
            batch.draw(getTexture(), getPosition().x, getPosition().y, getWidth(), getHeight());
        }
    }
    
    /**
     * Check if this platform has special effects
     */
    public boolean hasSpecialEffect() {
        return type != PlatformType.NORMAL;
    }
    
    /**
     * Apply platform effect to an entity
     */
    public void applyEffect(GameObject entity) {
        switch (type) {
            case HEALTH:
                // Health platform - could heal player
                break;
            case SLIME:
                // Slime platform - makes entities bouncy
                entity.addVelocity(0, 2f);
                break;
            case ICE:
                // Ice platform - slippery (reduce friction, gentler effect)
                if (Math.abs(entity.getVelocity().x) > 0.1f) {
                    entity.setVelocity(entity.getVelocity().x * 1.02f, entity.getVelocity().y); // Much gentler
                }
                break;
            case LAVA:
                // Lava platform - damages player
                if (entity instanceof Hero) {
                    entity.destroy();
                }
                break;
            case TRUSS:
                // Truss platform - normal behavior
                break;
        }
    }
    
    /**
     * Get platform type
     */
    public PlatformType getType() {
        return type;
    }
    
    public boolean isBreakable() {
        return type == PlatformType.BREAKABLE;
    }
    
    public boolean isHealthPlatform() {
        return type == PlatformType.HEALTH;
    }
    
    public void handleCollision(GameObject other) {
        if (other instanceof Hero || other instanceof Enemy) {
            if (type == PlatformType.BREAKABLE) {
                destroy();
            }
            applyEffect(other);
        }
    }
    
    public void setMoveRange(float range) {
        this.moveRange = range;
    }
    
    public void setStartPosition(Vector2 position) {
        this.startPosition.set(position);
    }
} 