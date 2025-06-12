package com.joust.managers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

/**
 * InputManager - handles keyboard input for the game
 * Replaces the old KeyListener implementation with LibGDX's input system
 */
public class InputManager implements InputProcessor {
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean restartPressed;
    
    public InputManager() {
        this.leftPressed = false;
        this.rightPressed = false;
        this.upPressed = false;
        this.restartPressed = false;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.LEFT:
                leftPressed = true;
                return true;
            case Keys.RIGHT:
                rightPressed = true;
                return true;
            case Keys.UP:
                upPressed = true;
                return true;
            case Keys.N:
                restartPressed = true;
                return true;
        }
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.LEFT:
                leftPressed = false;
                return true;
            case Keys.RIGHT:
                rightPressed = false;
                return true;
            case Keys.UP:
                upPressed = false;
                return true;
            case Keys.N:
                restartPressed = false;
                return true;
        }
        return false;
    }
    
    public boolean isLeftPressed() {
        return leftPressed;
    }
    
    public boolean isRightPressed() {
        return rightPressed;
    }
    
    public boolean isUpPressed() {
        return upPressed;
    }
    
    public boolean isRestartPressed() {
        return restartPressed;
    }
    
    // Required InputProcessor methods
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
} 