package com.joust.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.joust.managers.GameAssetManager;

public abstract class GameObject {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float width;
    protected float height;
    protected Rectangle bounds;
    protected TextureRegion texture;
    protected boolean destroyed;
    
    public GameObject(Vector2 position, float width, float height) {
        this.position = position;
        this.velocity = new Vector2();
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(position.x, position.y, width, height);
        this.destroyed = false;
    }
    
    public abstract void update(float deltaTime);
    
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        position.set(x, y);
        updateBounds();
    }
    
    public void addPosition(float x, float y) {
        position.add(x, y);
        updateBounds();
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }
    
    public void addVelocity(float x, float y) {
        velocity.add(x, y);
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setWidth(float width) {
        this.width = width;
        updateBounds();
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setHeight(float height) {
        this.height = height;
        updateBounds();
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }
    
    public TextureRegion getTexture() {
        return texture;
    }
    
    public Rectangle getBoundingBox() {
        return bounds;
    }
    
    protected void updateBounds() {
        bounds.set(position.x, position.y, width, height);
    }
    
    public boolean overlaps(GameObject other) {
        return bounds.overlaps(other.bounds);
    }
    
    public void destroy() {
        destroyed = true;
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
} 