package com.joust.client;

import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.joust.managers.LevelLoaderInterface;
import com.joust.client.assets.AssetsBundle;

import java.util.function.Consumer;

public class GwtLevelLoader implements LevelLoaderInterface {

    @Override
    public void loadLevel(int levelNumber, Consumer<String> onSuccess, Consumer<Throwable> onFailure) {
        ExternalTextResource levelRes = getLevelResource(levelNumber);
        
        if (levelRes == null) {
            onFailure.accept(new IllegalArgumentException("Invalid level number: " + levelNumber));
            return;
        }

        try {
            levelRes.getText(new ResourceCallback<TextResource>() {
                @Override
                public void onSuccess(TextResource resource) {
                    onSuccess.accept(resource.getText());
                }

                @Override
                public void onError(ResourceException e) {
                    onFailure.accept(e);
                }
            });
        } catch (ResourceException e) {
            onFailure.accept(e);
        }
    }

    private ExternalTextResource getLevelResource(int levelNumber) {
        switch (levelNumber) {
            case 0: return AssetsBundle.INSTANCE.level0();
            case 1: return AssetsBundle.INSTANCE.level1();
            case 2: return AssetsBundle.INSTANCE.level2();
            case 3: return AssetsBundle.INSTANCE.level3();
            case 4: return AssetsBundle.INSTANCE.level4();
            case 5: return AssetsBundle.INSTANCE.level5();
            case 6: return AssetsBundle.INSTANCE.level6();
            case 7: return AssetsBundle.INSTANCE.level7();
            case 8: return AssetsBundle.INSTANCE.level8();
            case 9: return AssetsBundle.INSTANCE.level9();
            case 10: return AssetsBundle.INSTANCE.level10();
            case 11: return AssetsBundle.INSTANCE.level11();
            case 12: return AssetsBundle.INSTANCE.level12();
            default: return null;
        }
    }
}
