package com.joust.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.joust.JoustGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		// Basic window settings
		config.setForegroundFPS(60);
		config.setTitle("Joust Game - LibGDX");
		config.setWindowedMode(800, 800);
		config.setResizable(false);
		
		// Add safer graphics settings to prevent crashes
		config.useVsync(true);
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 2, 0);
		
		// Try to prevent native crashes
		try {
			new Lwjgl3Application(new JoustGame(), config);
		} catch (Exception e) {
			System.err.println("Failed to start with ANGLE, trying default OpenGL...");
			// Fallback to default configuration
			Lwjgl3ApplicationConfiguration fallbackConfig = new Lwjgl3ApplicationConfiguration();
			fallbackConfig.setForegroundFPS(60);
			fallbackConfig.setTitle("Joust Game - LibGDX (Safe Mode)");
			fallbackConfig.setWindowedMode(800, 800);
			fallbackConfig.setResizable(false);
			fallbackConfig.useVsync(true);
			new Lwjgl3Application(new JoustGame(), fallbackConfig);
		}
	}
} 