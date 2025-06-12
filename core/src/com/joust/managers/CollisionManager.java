package com.joust.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.joust.entities.*;
import com.joust.screens.GameScreen;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

/**
 * CollisionManager - handles all collision detection and response
 * Centralizes collision logic that was scattered in the Swing version
 */
public class CollisionManager {
    
    private static final float COLLISION_OVERLAP = 0.1f;
    
    public CollisionManager() {
    }
    
    /**
     * Check all collisions between game entities
     */
    public void checkCollisions(Hero hero, ArrayList<Platform> platforms, 
                              ArrayList<Enemy> enemies, ArrayList<Egg> eggs) {
        // Check hero-platform collisions
        for (Platform platform : platforms) {
            if (hero.overlaps(platform)) {
                handleHeroPlatformCollision(hero, platform);
            }
        }
        
        // Check hero-enemy collisions
        for (Enemy enemy : enemies) {
            if (hero.overlaps(enemy)) {
                handleHeroEnemyCollision(hero, enemy);
            }
        }
        
        // Check hero-egg collisions
        for (Egg egg : eggs) {
            if (hero.overlaps(egg) && !egg.isCollected()) {
                handleHeroEggCollision(hero, egg);
            }
        }
        
        // Check enemy-platform collisions
        for (Enemy enemy : enemies) {
            for (Platform platform : platforms) {
                if (enemy.overlaps(platform)) {
                    handleEnemyPlatformCollision(enemy, platform);
                }
            }
        }
        
        // Check egg-platform collisions
        for (Egg egg : eggs) {
            for (Platform platform : platforms) {
                if (egg.overlaps(platform)) {
                    handleEggPlatformCollision(egg, platform);
                }
            }
        }
    }
    
    /**
     * Handle Hero vs Platform collisions
     */
    private void handleHeroPlatformCollision(Hero hero, Platform platform) {
        Rectangle heroBounds = hero.getBoundingBox();
        Rectangle platformBounds = platform.getBoundingBox();
        
        float overlap = Math.min(
            heroBounds.y + heroBounds.height - platformBounds.y,
            platformBounds.y + platformBounds.height - heroBounds.y
        );
        
        if (hero.getVelocity().y < 0) {
            hero.setPosition(hero.getPosition().x, hero.getPosition().y + overlap);
            hero.setVelocity(hero.getVelocity().x, 0);
        } else if (hero.getVelocity().y > 0) {
            hero.setPosition(hero.getPosition().x, hero.getPosition().y - overlap);
            hero.setVelocity(hero.getVelocity().x, 0);
        }
    }
    
    /**
     * Handle Hero vs Enemy collisions (joust mechanics)
     */
    private void handleHeroEnemyCollision(Hero hero, Enemy enemy) {
        if (hero.getY() > enemy.getY()) {
            // Hero is above enemy
            enemy.destroy();
            hero.setVelocity(hero.getVelocity().x, 400); // Bounce
        } else {
            // Enemy is above hero
            hero.destroy();
        }
    }
    
    /**
     * Handle Hero vs Egg collisions
     */
    private void handleHeroEggCollision(Hero hero, Egg egg) {
        egg.collect();
    }
    
    /**
     * Handle Enemy vs Platform collisions - use same logic as hero
     */
    public void handleEnemyPlatformCollision(Enemy enemy, Platform platform) {
        Rectangle enemyBounds = enemy.getBoundingBox();
        Rectangle platformBounds = platform.getBoundingBox();
        
        // Calculate proper overlap for collision resolution
        float overlapX = Math.min(
            enemyBounds.x + enemyBounds.width - platformBounds.x,
            platformBounds.x + platformBounds.width - enemyBounds.x
        );
        float overlapY = Math.min(
            enemyBounds.y + enemyBounds.height - platformBounds.y,
            platformBounds.y + platformBounds.height - enemyBounds.y
        );
        
        // Use similar logic as hero collision (smaller overlap determines collision type)
        if (overlapX < overlapY) {
            // Horizontal collision - reverse direction for koopa enemies
            if (enemy.getType() == Enemy.EnemyType.KOOPA) {
                // Koopa should reverse direction on horizontal collision
                enemy.setDirection(-enemy.getDirection());
            }
            enemy.setVelocity(0, enemy.getVelocity().y);
            float direction = Math.signum(platformBounds.x - enemyBounds.x);
            enemy.addPosition(-direction * overlapX, 0);
        } else {
            // Vertical collision
            enemy.addPosition(0, -Math.signum(enemy.getVelocity().y) * overlapY);
            enemy.setVelocity(enemy.getVelocity().x, 0);
        }
    }
    
    /**
     * Handle Egg vs Platform collisions
     */
    public void handleEggPlatformCollision(Egg egg, Platform platform) {
        Rectangle eggBounds = egg.getBoundingBox();
        Rectangle platformBounds = platform.getBoundingBox();
        
        float overlap = Math.min(
            eggBounds.y + eggBounds.height - platformBounds.y,
            platformBounds.y + platformBounds.height - eggBounds.y
        );
        
        if (egg.getVelocity().y < 0) {
            egg.setPosition(egg.getPosition().x, egg.getPosition().y + overlap);
            egg.setVelocity(egg.getVelocity().x, 0);
        } else if (egg.getVelocity().y > 0) {
            egg.setPosition(egg.getPosition().x, egg.getPosition().y - overlap);
            egg.setVelocity(egg.getVelocity().x, 0);
        }
    }
    
    /**
     * Check if an entity is on solid ground (touching a platform)
     */
    public boolean isOnGround(GameEntity entity, ArrayList<Platform> platforms) {
        // Check if entity is touching any platform from above
        for (Platform platform : platforms) {
            if (entity.getBoundingBox().overlaps(platform.getBoundingBox())) {
                // Check if entity is above the platform
                if (entity.getY() > platform.getY()) {
                    return true;
                }
            }
        }
        
        // Check if entity is at ground level
        return entity.getY() <= entity.getHeight() / 2;
    }
    
    /**
     * Find the nearest platform to an entity (for AI)
     */
    public Platform getNearestPlatform(GameEntity entity, ArrayList<Platform> platforms) {
        Platform nearest = null;
        float minDistance = Float.MAX_VALUE;
        
        for (Platform platform : platforms) {
            float distance = entity.getPosition().dst(platform.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = platform;
            }
        }
        
        return nearest;
    }
    
    /**
     * Check if there's a clear path between two entities (for AI)
     */
    public boolean hasLineOfSight(GameEntity from, GameEntity to, ArrayList<Platform> platforms) {
        // Simple line-of-sight check - could be improved with raycasting
        // For now, just check if there are platforms directly between them
        
        float fromX = from.getX();
        float fromY = from.getY();
        float toX = to.getX();
        float toY = to.getY();
        
        // Check several points along the line
        int steps = 5;
        for (int i = 1; i < steps; i++) {
            float t = (float) i / steps;
            float checkX = fromX + (toX - fromX) * t;
            float checkY = fromY + (toY - fromY) * t;
            
            // Check if this point intersects any platform
            for (Platform platform : platforms) {
                if (platform.getBoundingBox().contains(checkX, checkY)) {
                    return false; // Line of sight blocked
                }
            }
        }
        
        return true; // Clear line of sight
    }
} 