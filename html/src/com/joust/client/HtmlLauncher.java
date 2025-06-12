package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gwt.core.client.GWT;
import com.joust.JoustGame;

public class HtmlLauncher extends GwtApplication {    @Override
    public GwtApplicationConfiguration getConfig() {
        try {
            GWT.log("Starting configuration setup...");
            // Create a fullscreen configuration
            GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
            cfg.padHorizontal = 0;
            cfg.padVertical = 0;
            
            // Scale the canvas to fit the screen while maintaining aspect ratio
            cfg.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
            cfg.useDebugGL = true;
            
            GWT.log("Configuration created successfully");
            return cfg;
        } catch (Exception e) {
            GWT.log("Fatal error in configuration: " + e.getMessage(), e);
            throw new GdxRuntimeException("Configuration failed", e);
        }
    }

    @Override
    public ApplicationListener createApplicationListener() {
        try {
            GWT.log("Creating JoustGame instance...");
            JoustGame game = new JoustGame();
            GWT.log("JoustGame instance created successfully");
            return game;
        } catch (Exception e) {
            GWT.log("Fatal error creating game: " + e.getMessage(), e);
            throw new GdxRuntimeException("Game creation failed", e);
        }
    }
    
    @Override
    public void onModuleLoad() {
        try {
            GWT.log("Beginning module load...");
            super.onModuleLoad();
            GWT.log("Module loaded successfully");
        } catch (Exception e) {
            GWT.log("Fatal error loading module: " + e.getMessage(), e);
            throw new GdxRuntimeException("Module load failed", e);
        }
    }
    
    @Override
    public void error(String tag, String message, Throwable exception) {
        String fullMessage = tag + ": " + message;
        GWT.log("Error occurred: " + fullMessage, exception);
        if (exception != null) {
            GWT.log("Stack trace: ", exception);
        }
    }
}