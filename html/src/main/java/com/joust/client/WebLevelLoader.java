package com.joust.client;

import com.badlogic.gdx.Gdx;
import com.joust.managers.LevelLoaderInterface;
import java.util.function.Consumer;

public class WebLevelLoader implements LevelLoaderInterface {
    @Override
    public void loadLevel(int levelNumber, Consumer<String> onSuccess, Consumer<Throwable> onFailure) {
        try {
            String levelContent = Gdx.files.internal("levels/level" + levelNumber + ".txt").readString();
            onSuccess.accept(levelContent);
        } catch (Exception e) {
            Gdx.app.error("WebLevelLoader", "Error loading level " + levelNumber + ": " + e.getMessage());
            onFailure.accept(e);
        }
    }
} 