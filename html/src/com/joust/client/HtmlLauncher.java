package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.joust.JoustGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(800, 600);
        cfg.padHorizontal = 0;
        cfg.padVertical = 0;
        
        // Enable better debugging for web version
        cfg.useDebugGL = true;
        cfg.disableAudio = false;
        
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new JoustGame();
    }
    
    @Override
    public void onModuleLoad() {
        super.onModuleLoad();
        System.out.println("Module loaded, initializing game...");
    }
} 