package com.joust.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.joust.managers.CollisionManager;
import com.joust.managers.GameAssetManager;

/**
 * Hero class - the player character
 * Converted from Swing to LibGDX with improved physics and rendering
 */
public class Hero extends GameObject {
    
    private static final float HERO_WIDTH = 32;
    private static final float HERO_HEIGHT = 48;
    // Reduced speeds for better gameplay feel
    private static final float SPEED = 0.3f; // Much slower movement speed
    private static final float FLY_FORCE = 1.5f; // Much slower flying force
    private static final float GRAVITY_STRENGTH = -0.2f; // Gentler gravity
    private static final float FRICTION_STRENGTH = 0.2f; // Gentler friction
    private static final float MAX_SPEED = 4f; // Lower max speed
    private static final float BOUNCE_AREA_WIDTH = 10;
    
    private boolean facingRight = true;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean onGround = false;
    private boolean hasGravity = true;
    private boolean hasFriction = true;
    private boolean shouldDie = false; // Flag for GameScreen to handle death
    
    // Previous position for collision detection
    private Vector2 previousPosition;

    public Hero(float x, float y) {
        super(new Vector2(x, y), HERO_WIDTH, HERO_HEIGHT);
        this.previousPosition = new Vector2(x, y);
        setTexture(GameAssetManager.getInstance().getTexture("hero_right"));
    }
    
    @Override
    public void update(float deltaTime) {
        // Store previous position for collision detection
        updatePreviousPosition();
        
        // Handle horizontal movement (additive like original)
        if (rightPressed) {
            addVelocity(SPEED, 0);
            facingRight = true;
        }
        if (leftPressed) {
            addVelocity(-SPEED, 0);
            facingRight = false;
        }
        
        // Handle vertical movement (flying) - additive like original
        if (upPressed) {
            addVelocity(0, FLY_FORCE); // Positive Y is up in LibGDX
        }
        
        // Apply physics in the correct order like original
        
        // 1. Move based on current velocity (with deltaTime for smooth movement)
        addPosition(getVelocity().x * deltaTime * 60f, getVelocity().y * deltaTime * 60f); // Scale for 60fps equivalent
        
        // 2. Apply gravity
        if (hasGravity) {
            addVelocity(0, GRAVITY_STRENGTH); // Negative Y is down in LibGDX
        }
        
        // 3. Apply friction to X velocity
        if (hasFriction) {
            float frictionAmount = -Math.signum(getVelocity().x) * FRICTION_STRENGTH;
            addVelocity(frictionAmount, 0);
        }
        
        // 4. Cap velocities to max speed (like original)
        float newVelX = Math.signum(getVelocity().x) * Math.min(MAX_SPEED, Math.abs(getVelocity().x));
        float newVelY = Math.signum(getVelocity().y) * Math.min(MAX_SPEED, Math.abs(getVelocity().y));
        setVelocity(newVelX, newVelY);
        
        // Note: No screen bounds - levels provide walls for containment
        
        // Update texture based on direction
        updateTexture();
        
        // Update bounds
        updateBounds();
        
        // Reset ground state (will be set by collision detection)
        onGround = false;
    }
    
    private void updatePreviousPosition() {
        previousPosition.set(getPosition());
    }
    
    public Vector2 getPreviousPosition() {
        return previousPosition;
    }
    
    private void updateTexture() {
        String textureName = facingRight ? "hero_right" : "hero_left";
        setTexture(GameAssetManager.getInstance().getTexture(textureName));
    }
    
    public void setLeftKeyHeld(boolean held) {
        leftPressed = held;
    }
    
    public void setRightKeyHeld(boolean held) {
        rightPressed = held;
    }
    
    public void setUpKeyHeld(boolean held) {
        upPressed = held;
    }
    
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    public boolean isFacingRight() {
        return facingRight;
    }
    
    public boolean isOnGround() {
        return onGround;
    }
    
    public boolean shouldDie() {
        return shouldDie;
    }
    
    public void resetDeathFlag() {
        shouldDie = false;
    }
    
    /**
     * Handle collision with another object - using original logic
     */
    public void handleCollision(GameObject other) {
        if (other instanceof Platform) {
            handlePlatformCollision((Platform) other);
        } else if (other instanceof Enemy) {
            handleEnemyCollision((Enemy) other);
        } else if (other instanceof Egg) {
            handleEggCollision((Egg) other);
        }
    }
    
    private void handlePlatformCollision(Platform platform) {
        // Check for special platform effects first
        if (platform.getType() == Platform.PlatformType.LAVA) {
            shouldDie = true; // Lava kills player
            return;
        } else if (platform.getType() == Platform.PlatformType.HEALTH) {
            // Health platform gives life (handled by GameScreen)
            // Don't destroy here - let GameScreen handle it to increment lives
        } else if (platform.getType() == Platform.PlatformType.ICE) {
            // Ice platform makes player slide more gently (reduce friction effect)
            if (Math.abs(getVelocity().x) > 0.1f) {
                addVelocity(1.2f * Math.signum(getVelocity().x), 0); // Much gentler than original 5x
            }
        } else if (platform.getType() == Platform.PlatformType.SLIME) {
            // Slime platform stops X movement
            setVelocity(0, getVelocity().y);
        }
        
        // Use original collision logic - based on previous position
        Vector2 otherPos = platform.getPosition();
        float previousXDist = otherPos.x - previousPosition.x;
        float previousYDist = otherPos.y - previousPosition.y;
        
        // Calculate overlap
        float overlapX = Math.min(
            getPosition().x + getWidth() - platform.getPosition().x,
            platform.getPosition().x + platform.getWidth() - getPosition().x
        );
        float overlapY = Math.min(
            getPosition().y + getHeight() - platform.getPosition().y,
            platform.getPosition().y + platform.getHeight() - getPosition().y
        );
        
        if (Math.abs(previousYDist) == Math.abs(previousXDist)) {
            return; // Corner collision, ignore
        } else if (Math.abs(previousYDist) < Math.abs(previousXDist)) {
            // Horizontal collision
            setVelocity(0, getVelocity().y);
            float direction = Math.signum(platform.getPosition().x - getPosition().x);
            addPosition(-direction * overlapX, 0);
        } else {
            // Vertical collision
            addPosition(0, -Math.signum(previousYDist) * overlapY);
            setVelocity(getVelocity().x, 0);
            if (getVelocity().y >= 0) { // Landing on platform
                onGround = true;
            }
        }
        updateBounds();
    }
    
    private void handleEnemyCollision(Enemy enemy) {
        // Original joust logic from Swing Hero.joust() method 
        float enemyY = enemy.getPosition().y;
        float heroY = getPosition().y;
        int bounceAreaWidth = 10; // From original
        
        // Original joust logic: In Swing Y-down, in LibGDX Y-up - need to flip comparison
        // In original: enemy.getYCent() > this.getYCent() meant enemy LOWER on screen (wins)
        // In LibGDX: enemy.getPosition().y < hero.getPosition().y means enemy LOWER on screen
        if (Math.abs(enemyY - heroY) < bounceAreaWidth) {
            // Approximately even height - bounce off horizontally (result = 1)
            if (getPosition().x < enemy.getPosition().x) {
                addVelocity(-3f, 0); // Bounce left
            } else {
                addVelocity(3f, 0); // Bounce right
            }
        } else if (enemyY < heroY) {
            // Enemy is LOWER on screen - hero wins (result = 2) 
            enemy.destroy();
            addVelocity(0, FLY_FORCE * 0.5f); // Bounce up
            // Enemy death handled by enemy.destroy() - will create egg
        } else {
            // Enemy is HIGHER on screen - hero loses (result = 0) 
            // Don't destroy hero directly - let GameScreen handle player death
            shouldDie = true; // Mark for death
            setVelocity(0, 0); // Stop movement
        }
    }
    
    private void handleEggCollision(Egg egg) {
        if (!egg.isCollected()) {
            egg.collect();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null) {
            batch.draw(getTexture(), getPosition().x, getPosition().y, getWidth(), getHeight());
        }
    }
    
    private void keepInBounds() {
        // Screen dimensions matching the viewport
        float screenWidth = 800f;
        float screenHeight = 600f;
        
        // Horizontal bounds
        if (getPosition().x < 0) {
            setPosition(0, getPosition().y);
            setVelocity(0, getVelocity().y);
        } else if (getPosition().x + getWidth() > screenWidth) {
            setPosition(screenWidth - getWidth(), getPosition().y);
            setVelocity(0, getVelocity().y);
        }
        
        // Vertical bounds - add ground floor
        if (getPosition().y < 0) {
            setPosition(getPosition().x, 0);
            setVelocity(getVelocity().x, 0);
            onGround = true;
        } else if (getPosition().y > screenHeight) {
            setPosition(getPosition().x, screenHeight - getHeight());
            setVelocity(getVelocity().x, Math.min(0, getVelocity().y)); // Only allow downward movement
        }
    }
} 