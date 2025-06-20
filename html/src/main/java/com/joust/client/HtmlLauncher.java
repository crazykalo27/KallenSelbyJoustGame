package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.joust.JoustGame;
import com.joust.client.WebLevelLoader;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        // Resizable application, uses available space in browser
        return new GwtApplicationConfiguration(true);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new JoustGame(new WebLevelLoader());
    }
} 