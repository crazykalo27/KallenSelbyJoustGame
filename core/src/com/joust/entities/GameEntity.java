package com.joust.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for all game entities - replaces the old GameObject
 * Uses LibGDX math and rendering systems for better performance
 */
public abstract class GameEntity {
    
    // Position and movement
    protected Vector2 position;
    protected Vector2 velocity;
    protected Vector2 previousPosition;
    
    // Physics
    protected float width;
    protected float height;
    protected Rectangle boundingBox;
    
    // Rendering
    protected TextureRegion texture;
    protected boolean visible = true;
    
    // State
    protected boolean destroyed = false;
    protected boolean gravityEnabled = true;
    
    // Physics constants
    public static final float GRAVITY_STRENGTH = 0.5f;
    public static final float TERMINAL_VELOCITY = 15f;
    public static final float FRICTION = 0.92f;
    
    public GameEntity(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.previousPosition = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.boundingBox = new Rectangle(x - width/2, y - height/2, width, height);
    }
    
    /**
     * Update entity logic - called every frame
     * @param delta Time since last frame in seconds
     */
    public void update(float delta) {
        // Store previous position for collision detection
        previousPosition.set(position);
        
        // Apply gravity if enabled
        if (gravityEnabled) {
            velocity.y -= GRAVITY_STRENGTH;
        }
        
        // Apply terminal velocity
        if (velocity.y < -TERMINAL_VELOCITY) {
            velocity.y = -TERMINAL_VELOCITY;
        }
        
        // Apply friction to horizontal movement
        velocity.x *= FRICTION;
        
        // Update position based on velocity
        position.add(velocity.x * delta * 60, velocity.y * delta * 60); // Scale for 60 FPS consistency
        
        // Update bounding box
        updateBoundingBox();
        
        // Handle screen boundaries
        handleBoundaries();
    }
    
    /**
     * Render the entity
     * @param batch SpriteBatch for rendering
     */
    public void render(SpriteBatch batch) {
        if (visible && texture != null) {
            batch.draw(texture, 
                      position.x - width/2, 
                      position.y - height/2, 
                      width, 
                      height);
        }
    }
    
    /**
     * Debug render (for debugging collision boxes)
     */
    public void renderDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
    
    protected void updateBoundingBox() {
        boundingBox.setPosition(position.x - width/2, position.y - height/2);
    }
    
    protected void handleBoundaries() {
        // Keep within screen bounds (horizontal wrapping like original)
        if (position.x < 0) {
            position.x = 800; // Wrap to right side
        } else if (position.x > 800) {
            position.x = 0; // Wrap to left side
        }
        
        // Ground collision (basic)
        if (position.y < height/2) {
            position.y = height/2;
            velocity.y = 0;
        }
        
        // Ceiling collision
        if (position.y > 850 - height/2) {
            position.y = 850 - height/2;
            velocity.y = 0;
        }
    }
    
    /**
     * Handle collision with another entity
     * @param other The entity we collided with
     */
    public void handleCollision(GameEntity other) {
        if (other == null) return;
        
        // Calculate collision response based on previous positions
        Vector2 prevDist = new Vector2(
            other.position.x - this.previousPosition.x,
            other.position.y - this.previousPosition.y
        );
        
        Rectangle overlap = new Rectangle();
        if (Intersector.intersectRectangles(this.boundingBox, other.boundingBox, overlap)) {
            
            if (Math.abs(prevDist.y) < Math.abs(prevDist.x)) {
                // Horizontal collision
                float direction = Math.signum(other.position.x - this.position.x);
                position.x -= direction * overlap.width;
                velocity.x = 0;
            } else {
                // Vertical collision
                position.y -= Math.signum(prevDist.y) * overlap.height;
                velocity.y = 0;
            }
            
            updateBoundingBox();
        }
    }
    
    /**
     * Check if entity is touching the ground (simplified)
     */
    protected boolean isOnGround() {
        return position.y <= height/2 + 1; // Small tolerance
    }
    
    // Movement methods
    public void addVelocity(float vx, float vy) {
        velocity.add(vx, vy);
    }
    
    public void setVelocity(float vx, float vy) {
        velocity.set(vx, vy);
    }
    
    public void move(float dx, float dy) {
        position.add(dx, dy);
        updateBoundingBox();
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        updateBoundingBox();
    }
    
    // State methods
    public void destroy() {
        destroyed = true;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public boolean overlaps(GameEntity other) {
        return boundingBox.overlaps(other.boundingBox);
    }
    
    // Getters and setters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Vector2 getPreviousPosition() { return previousPosition; }
    public Rectangle getBoundingBox() { return boundingBox; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public TextureRegion getTexture() { return texture; }
    
    public void setTexture(TextureRegion texture) { this.texture = texture; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setSize(float width, float height) { 
        this.width = width; 
        this.height = height; 
        updateBoundingBox();
    }
    
    /**
     * Set gravity enabled/disabled
     */
    public void setGravityEnabled(boolean enabled) {
        this.gravityEnabled = enabled;
    }
    
    /**
     * Multiply velocity by factors
     */
    public void multiplyVelocity(float factorX, float factorY) {
        velocity.x *= factorX;
        velocity.y *= factorY;
    }
    
    public boolean isGravityEnabled() {
        return gravityEnabled;
    }
} 