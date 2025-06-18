package com.joust.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.joust.managers.GameAssetManager;
import com.badlogic.gdx.Gdx;

public class Enemy extends GameObject {
    public enum EnemyType {
        GHOST,      // 'b' - LeftRightEnemy (timer-based direction changes, no gravity)
        KOOPA,      // 'e' - Walks slowly left/right, occasional small jumps
        TRACKER     // 't' - Tracker (follows player)
    }

    // Speed constants relative to hero's max speed (which is 4 * 60 = 240 units/sec)
    private static final float GHOST_SPEED = 4.5f;           // Slightly faster than hero's base speed
    private static final float KOOPA_SPEED = 1.0f;           // Very slow walking speed
    private static final float TRACKER_SPEED = 3.5f;         // Slightly slower for balance
    private static final float KOOPA_JUMP_VELOCITY = 5f;     // Small jump height
    private static final float KOOPA_GRAVITY = -10f;         // Gentle gravity
    private static final float STARTUP_DELAY = 1.0f;  // 1 second delay before movement starts
    private static final float MIN_DIRECTION_TIME = 2.0f; // Minimum time before changing direction
    private static final float MAX_DIRECTION_TIME = 5.0f; // Maximum time before changing direction
    
    // Movement vectors for 8 directions (normalized)
    private static final Vector2[] DIRECTIONS = {
        new Vector2(1, 0),      // Right
        new Vector2(1, 1).nor(),  // Up-Right
        new Vector2(0, 1),      // Up
        new Vector2(-1, 1).nor(), // Up-Left
        new Vector2(-1, 0),     // Left
        new Vector2(-1, -1).nor(), // Down-Left
        new Vector2(0, -1),     // Down
        new Vector2(1, -1).nor()  // Down-Right
    };
    
    // Ghost enemy timer variables (matching original LeftRightEnemy)
    private int ticks = 0;
    private int waitNum;
    private boolean useTimer = false; // For ghost enemy direction changes
    private float startupTimer = 0f;  // Timer for initial movement delay
    
    // Koopa jump timing
    private float koopaJumpTimer = 0f;
    private float nextKoopaJumpTime = 0f;
    
    private final EnemyType type;
    private int direction = 1; // -1 for left, 1 for right
    private boolean isTutorialMode = false;
    private Vector2 playerPosition = null; // Reference to player position for tracking
    private float verticalDirection = 0; // Track vertical movement direction
    private Vector2 proposedPosition = new Vector2(); // For movement validation
    private float directionTimer = 0f;
    private float currentDirectionDuration = 0f;
    private Vector2 currentDirection = new Vector2();

    public Enemy(Vector2 position, EnemyType type) {
        super(position, 32, 32);
        this.type = type;
        this.direction = 1; // Start moving right
        
        // Initialize based on type (matching original constructors)
        switch (type) {
            case GHOST:
                // LeftRightEnemy: no gravity, timer-based movement
                this.useTimer = true;
                this.waitNum = (int)(Math.random() * 50) + 40; // Random 40-90 ticks (matching original)
                break;
            case KOOPA:
                // Simple initialization for Koopa
                this.nextKoopaJumpTime = (float)(Math.random() * 4f) + 3f; // First jump in 3-7 seconds
                setVelocity(KOOPA_SPEED * direction, 0);
                break;
            case TRACKER:
                // Tracker: NO gravity (flies), follows player
                break;
        }
        
        updateTexture();
    }
    
    @Override
    public void update(float deltaTime) {
        Vector2 oldPos = new Vector2(position);
        
        // Handle startup delay
        if (startupTimer < STARTUP_DELAY) {
            startupTimer += deltaTime;
            setVelocity(0, 0);
            return;
        }
        
        switch (type) {
            case GHOST:
                updateGhost(deltaTime);
                break;
            case KOOPA:
                updateKoopa(deltaTime);
                break;
            case TRACKER:
                updateTracker(deltaTime);
                break;
        }

        // Apply gravity only to Koopa
        if (type == EnemyType.KOOPA) {
            addVelocity(0, KOOPA_GRAVITY * deltaTime);
        }

        // Move based on velocity
        addPosition(getVelocity().x * deltaTime * 60f, getVelocity().y * deltaTime * 60f);
        
        // Handle bounds checking
        if (type == EnemyType.KOOPA) {
            // Keep Koopas within horizontal bounds and make them turn around
            if (getPosition().x < 0) {
                setPosition(0, getPosition().y);
                direction *= -1;
                setVelocity(KOOPA_SPEED * direction, getVelocity().y);
            } else if (getPosition().x > 800 - width) {
                setPosition(800 - width, getPosition().y);
                direction *= -1;
                setVelocity(KOOPA_SPEED * direction, getVelocity().y);
            }
            
            // Keep within vertical bounds
            if (getPosition().y < 50) {
                setPosition(getPosition().x, 50);
                setVelocity(getVelocity().x, 0); // Stop vertical movement on landing
            } else if (getPosition().y > 750 - height) {
                setPosition(getPosition().x, 750 - height);
                setVelocity(getVelocity().x, 0); // Stop vertical movement on ceiling hit
            }
        } else {
            // Keep existing wrapping behavior for ghosts and trackers
            if (getPosition().x < -16f) {
                setPosition(800 - 16f, getPosition().y);
            } else if (getPosition().x > 800 + 16f) {
                setPosition(16f, getPosition().y);
            }
        }

        updateTexture();
        updateBounds();
    }

    private void updateGhost(float deltaTime) {
        // LeftRightEnemy behavior - EXACT port from original
        if (isTutorialMode) {
            // Stationary in tutorial (speed = 0)
            setVelocity(0, 0);
            return;
        }
        
        directionTimer += deltaTime;
        
        // Change direction if timer expires or if we hit a boundary
        if (directionTimer >= currentDirectionDuration) {
            // Pick a random direction from the 8 possible directions
            Vector2 newDir = DIRECTIONS[(int)(Math.random() * DIRECTIONS.length)];
            currentDirection.set(newDir);
            
            // Set new random duration between MIN and MAX
            currentDirectionDuration = MIN_DIRECTION_TIME + (float)(Math.random() * (MAX_DIRECTION_TIME - MIN_DIRECTION_TIME));
            directionTimer = 0;
            
            // Update velocity based on new direction
            setVelocity(
                currentDirection.x * GHOST_SPEED,
                currentDirection.y * GHOST_SPEED
            );
            
            // Update facing direction for sprite
            direction = (currentDirection.x < 0) ? -1 : 1;
        }
    }
    
    private void updateKoopa(float deltaTime) {
        // Update jump timer
        koopaJumpTimer += deltaTime;
        
        // Check if it's time to jump and we're on ground (y position near ground)
        if (koopaJumpTimer >= nextKoopaJumpTime && getPosition().y <= 51) {
            setVelocity(getVelocity().x, KOOPA_JUMP_VELOCITY);
            koopaJumpTimer = 0f;
            nextKoopaJumpTime = (float)(Math.random() * 4f) + 3f; // Next jump in 3-7 seconds
        }
        
        // Maintain slow horizontal movement
        setVelocity(KOOPA_SPEED * direction, getVelocity().y);
    }
    
    private void updateTracker(float deltaTime) {
        // Tracker behavior - follows player when available
        if (playerPosition != null) {
            // Calculate velocity towards player using simplified approach
            float dx = playerPosition.x - getPosition().x;
            float dy = playerPosition.y - getPosition().y;
            float length = (float)Math.sqrt(dx * dx + dy * dy);
            
            if (length > 0) {
                // Normalize and apply tracker speed
                float velX = (dx / length) * TRACKER_SPEED;
                float velY = (dy / length) * TRACKER_SPEED;
                setVelocity(velX, velY);
                
                // Update direction based on horizontal movement
                direction = (velX < 0) ? -1 : 1;
            }
        } else {
            // No player reference, hover in current direction
            setVelocity(TRACKER_SPEED * direction, 0);
        }
    }

    /**
     * Validates if a proposed position is within game bounds
     */
    private boolean validateMove(Vector2 proposedPos) {
        // Basic bounds checking
        if (proposedPos.x < 0 || proposedPos.x > 800 - width ||
            proposedPos.y < 50 || proposedPos.y > 750 - height) {
            return false;
        }
        return true;
    }

    public void handlePlatformCollision(Platform platform) {
        // If it's a Koopa and we hit a platform horizontally, reverse direction
        if (type == EnemyType.KOOPA) {
            direction *= -1;
            setVelocity(KOOPA_SPEED * direction, getVelocity().y);
        }
    }
    
    public void setTutorialMode(boolean tutorialMode) {
        this.isTutorialMode = tutorialMode;
    }
    
    public void setPlayerPosition(Vector2 playerPosition) {
        this.playerPosition = playerPosition;
    }

    private void updateTexture() {
        String textureName;
        boolean facingRight = direction > 0;
        
        switch (type) {
            case GHOST:    // 'b' - Ghost enemy uses PacMan sprites
                textureName = facingRight ? "pacman_right" : "pacman_left";
                break;
            case KOOPA:    // 'e' - Koopa enemy uses blue koopa sprites  
                textureName = facingRight ? "blue_koopa_right" : "blue_koopa_left";
                break;
            case TRACKER:  // 't' - Tracker enemy uses WaddleD sprites
                textureName = facingRight ? "ghost_right" : "ghost_left";
                break;
            default:
                textureName = "pacman_right";
        }
        setTexture(GameAssetManager.getInstance().getTexture(textureName));
    }

    public EnemyType getType() {
        return type;
    }

    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    @Override
    protected void updateBounds() {
        bounds.set(position.x, position.y, width, height);
    }
} 