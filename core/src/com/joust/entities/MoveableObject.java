package com.joust.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class MoveableObject extends GameObject {
    protected Vector2 velocity;
    protected Vector2 acceleration;
    protected float maxSpeed;
    protected boolean gravityEnabled;
    
    public MoveableObject(Vector2 position, float width, float height) {
        super(position, width, height);
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
        this.maxSpeed = 200f;
        this.gravityEnabled = true;
    }
    
    @Override
    public void update(float deltaTime) {
        // Apply acceleration
        velocity.add(acceleration.x * deltaTime, acceleration.y * deltaTime);
        
        // Apply gravity if enabled
        if (gravityEnabled) {
            velocity.y -= 800f * deltaTime; // Gravity constant
        }
        
        // Limit speed
        if (velocity.len() > maxSpeed) {
            velocity.nor().scl(maxSpeed);
        }
        
        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        // Reset acceleration
        acceleration.setZero();
    }
    
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }
    
    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setAcceleration(float x, float y) {
        acceleration.set(x, y);
    }
    
    public void setAcceleration(Vector2 acceleration) {
        this.acceleration.set(acceleration);
    }
    
    public Vector2 getAcceleration() {
        return acceleration;
    }
    
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    
    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    public void setGravityEnabled(boolean enabled) {
        this.gravityEnabled = enabled;
    }
    
    public boolean isGravityEnabled() {
        return gravityEnabled;
    }
} 