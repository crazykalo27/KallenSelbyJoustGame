package com.joust.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.joust.JoustGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Fixed size configuration to match desktop version dimensions
                return new GwtApplicationConfiguration(1040, 1105);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new JoustGame();
        }
} 