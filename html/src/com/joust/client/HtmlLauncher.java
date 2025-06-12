package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.joust.JoustGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(1040, 780); // 30% larger (800x600 * 1.3)
        cfg.padHorizontal = 0;
        cfg.padVertical = 0;
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener () {
        return new JoustGame();
    }
} 