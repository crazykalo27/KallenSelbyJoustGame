package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joust.JoustGame;
import com.joust.client.assets.AssetsBundle;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        System.out.println("[HTML_LAUNCHER] getConfig() called — setting up configuration.");
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(true); // Fullscreen, responsive
        config.disableAudio = true; // Disable audio for now since we're not using it
        config.useDebugGL = true; // Enable debug mode for better error messages
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        System.out.println("[HTML_LAUNCHER] createApplicationListener() called — returning JoustGame.");
        // Ensure all assets are referenced to force GWT to compile them
        preloadAllAssets();
        return new JoustGame(new GwtLevelLoader()); // Injects platform-specific loader
    }
    
    /**
     * Force GWT to compile all assets by referencing them
     */
    private void preloadAllAssets() {
        System.out.println("[HTML_LAUNCHER] Preloading all assets for GWT compilation...");
        
        // Reference all image resources to ensure they're compiled
        AssetsBundle bundle = AssetsBundle.INSTANCE;
        
        // Hero textures
        createImageReference(bundle.heroLeft());
        createImageReference(bundle.heroRight());
        
        // Enemy textures
        createImageReference(bundle.ghostLeft());
        createImageReference(bundle.ghostRight());
        createImageReference(bundle.blueKoopaLeft());
        createImageReference(bundle.blueKoopaRight());
        createImageReference(bundle.pacmanLeft());
        createImageReference(bundle.pacmanRight());
        
        // Platform textures
        createImageReference(bundle.platformTruss());
        createImageReference(bundle.platformHealth());
        createImageReference(bundle.platformSlime());
        createImageReference(bundle.platformIce());
        createImageReference(bundle.platformLava());
        
        // Egg texture
        createImageReference(bundle.egg());
        
        // Game over textures
        createImageReference(bundle.gameover0());
        createImageReference(bundle.gameover1());
        createImageReference(bundle.gameover2());
        createImageReference(bundle.gameover3());
        
        // Level resources are automatically loaded via GwtLevelLoader
        System.out.println("[HTML_LAUNCHER] Asset preloading complete.");
    }
    
    /**
     * Creates an image reference to ensure GWT compiles the resource
     */
    private void createImageReference(com.google.gwt.resources.client.ImageResource resource) {
        // This creates a reference to the image without displaying it
        Image img = new Image(resource.getSafeUri());
        ImageElement element = ImageElement.as(img.getElement());
        // Just accessing the URL is enough to make GWT compile it
        String url = element.getSrc();
    }
} 