package com.joust.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.joust.managers.LevelLoaderInterface;

import java.util.function.Consumer;

public class DesktopLevelLoader implements LevelLoaderInterface {
    @Override
    public void loadLevel(int levelNumber, Consumer<String> onSuccess, Consumer<Throwable> onFailure) {
        try {
            FileHandle file = Gdx.files.internal("levels/" + levelNumber + "level.txt");
            if (file.exists()) {
                String content = file.readString();
                onSuccess.accept(content);
            } else {
                onFailure.accept(new RuntimeException("Level file not found: " + levelNumber));
            }
        } catch (Exception e) {
            onFailure.accept(e);
        }
    }
} 