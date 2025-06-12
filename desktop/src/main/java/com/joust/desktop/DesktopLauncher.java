package com.joust.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.joust.JoustGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Set window properties
        config.setTitle("Java Joust");
        config.setWindowedMode(1040, 1105); // 30% larger than original (800x850 * 1.3)
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        // Create and launch the game
        new Lwjgl3Application(new JoustGame(), config);
    }
} 