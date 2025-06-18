package com.joust.managers;

import java.util.function.Consumer;

public interface LevelLoaderInterface {
    void loadLevel(int levelNumber, Consumer<String> onSuccess, Consumer<Throwable> onFailure);
} 