package com.joust.client.assets;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;

public interface AssetsBundle extends ClientBundle {
    AssetsBundle INSTANCE = com.google.gwt.core.client.GWT.create(AssetsBundle.class);

    // Hero textures
    @Source("images/DigDugLeft.png") ImageResource heroLeft();
    @Source("images/DigDugRight.PNG") ImageResource heroRight();

    // Enemy textures
    @Source("images/WaddleDLeft.PNG") ImageResource ghostLeft();
    @Source("images/WaddleDRight.PNG") ImageResource ghostRight();
    @Source("images/BlueKoopaLeft.PNG") ImageResource blueKoopaLeft();
    @Source("images/BlueKoopaRight.PNG") ImageResource blueKoopaRight();
    @Source("images/PacManLeft.PNG") ImageResource pacmanLeft();
    @Source("images/PacManRight.PNG") ImageResource pacmanRight();

    // Platform textures
    @Source("images/PlatTruss.PNG") ImageResource platformTruss();
    @Source("images/PlatHealth.png") ImageResource platformHealth();
    @Source("images/PlatSlime.png") ImageResource platformSlime();
    @Source("images/PlatIce.PNG") ImageResource platformIce();
    @Source("images/PlatLava.PNG") ImageResource platformLava();

    // Egg texture
    @Source("images/Egg.png") ImageResource egg();

    // Game over textures
    @Source("images/gameover0.png") ImageResource gameover0();
    @Source("images/gameover1.png") ImageResource gameover1();
    @Source("images/gameover2.png") ImageResource gameover2();
    @Source("images/gameover3.png") ImageResource gameover3();

    // Level resources
    @Source("levels/0level.txt") ExternalTextResource level0();
    @Source("levels/1level.txt") ExternalTextResource level1();
    @Source("levels/2level.txt") ExternalTextResource level2();
    @Source("levels/3level.txt") ExternalTextResource level3();
    @Source("levels/4level.txt") ExternalTextResource level4();
    @Source("levels/5level.txt") ExternalTextResource level5();
    @Source("levels/6level.txt") ExternalTextResource level6();
    @Source("levels/7level.txt") ExternalTextResource level7();
    @Source("levels/8level.txt") ExternalTextResource level8();
    @Source("levels/9level.txt") ExternalTextResource level9();
    @Source("levels/10level.txt") ExternalTextResource level10();
    @Source("levels/11level.txt") ExternalTextResource level11();
    @Source("levels/12level.txt") ExternalTextResource level12();
} 