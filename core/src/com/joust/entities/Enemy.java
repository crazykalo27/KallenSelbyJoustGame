package com.joust.entities;

import com.badlogic.gdx.math.Vector2;
import com.joust.managers.GameAssetManager;

public class Enemy extends GameObject {
    public enum EnemyType {
        GHOST,      // 'b' - LeftRightEnemy (timer-based direction changes, no gravity)
        KOOPA,      // 'e' - RandomMoveEnemy (moves in direction, jumps randomly, reverses on collision) 
        TRACKER     // 't' - Tracker (follows player)
    }

    // Reduced speeds for better gameplay (original scaling was too high)
    private static final float GHOST_SPEED = 80f;           // Reduced from 200f (4*50) - much slower
    private static final float KOOPA_SPEED = 60f;           // Reduced from 150f (3*50) - much slower
    private static final float TRACKER_SPEED = 40f;         // Reduced from 100f (2*50) - much slower
    private static final float KOOPA_JUMP_VELOCITY = 800f;  // Much higher jumps for better gameplay
    private static final float KOOPA_JUMP_CHANCE = 0.02f;   // 2% chance per frame (60fps = 0.02/frame)
    
    // Ghost enemy timer variables (matching original LeftRightEnemy)
    private int ticks = 0;
    private int waitNum;
    private boolean useTimer = false; // For ghost enemy direction changes
    
    // Koopa enemy timer for random jumping (2-5 seconds as requested)
    private float koopaJumpTimer = 0f;
    private float nextKoopaJumpTime = 0f;
    
    private final EnemyType type;
    private int direction = 1; // -1 for left, 1 for right
    private boolean isTutorialMode = false;
    private Vector2 playerPosition = null; // Reference to player position for tracking

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
                // RandomMoveEnemy: has gravity, continuous movement, random jumping
                this.nextKoopaJumpTime = (float)(Math.random() * 3f) + 2f; // 2-5 seconds
                break;
            case TRACKER:
                // Tracker: NO gravity (flies), follows player
                break;
        }
        
        updateTexture();
    }
    
    public void setTutorialMode(boolean tutorialMode) {
        this.isTutorialMode = tutorialMode;
    }
    
    public void setPlayerPosition(Vector2 playerPosition) {
        this.playerPosition = playerPosition;
    }

    @Override
    public void update(float deltaTime) {
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

        // Apply gravity (only for KOOPA, not GHOST or TRACKER like original)
        if (type == EnemyType.KOOPA) {
            addVelocity(0, -800f * deltaTime);
        }

        // Update position
        addPosition(getVelocity().x * deltaTime, getVelocity().y * deltaTime);

        // Update texture based on direction
        updateTexture();

        // Update bounds
        updateBounds();
    }

    private void updateGhost(float deltaTime) {
        // LeftRightEnemy behavior - EXACT port from original
        if (isTutorialMode) {
            // Stationary in tutorial (speed = 0)
            setVelocity(0, 0);
        } else if (useTimer) {
            // Timer-based direction changes (matching original ticks system)
            // Convert deltaTime to approximate tick count (assuming 60fps)
            ticks += (int)(deltaTime * 60);
            
            if (ticks >= waitNum) {
                // Change direction and Y velocity (matching original)
                int diry = -1;
                if (Math.random() < 0.5) {
                    diry *= -1;
                }
                setVelocity(0, GHOST_SPEED * diry);
                
                // Reverse X direction (matching original logic)
                direction *= -1;
                
                // Reset timer with new random wait time
                ticks = 0;
                waitNum = (int)(Math.random() * 50) + 50; // 50-100 ticks
            }
            
            // Set horizontal velocity based on direction (matching original)
            float xVel = GHOST_SPEED * GHOST_SPEED / GHOST_SPEED; // Match original Math.pow logic
            setVelocity(xVel * Math.signum(direction), getVelocity().y);
        }
    }
    
    private void updateKoopa(float deltaTime) {
        // RandomMoveEnemy behavior - Enhanced with timer-based jumping (2-5 seconds)
        koopaJumpTimer += deltaTime;
        
        // Random jumping based on probability per frame
        if (Math.random() < KOOPA_JUMP_CHANCE) {
            addVelocity(0, KOOPA_JUMP_VELOCITY);
        }
        
        // Additional random jumps every 2-5 seconds for variety
        if (koopaJumpTimer >= nextKoopaJumpTime) {
            addVelocity(0, KOOPA_JUMP_VELOCITY);
            koopaJumpTimer = 0f;
            nextKoopaJumpTime = (float)(Math.random() * 3f) + 2f; // Next jump in 2-5 seconds
        }
        
        // Continuous horizontal movement in current direction
        setVelocity(KOOPA_SPEED * direction, getVelocity().y);    }
    
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
} 