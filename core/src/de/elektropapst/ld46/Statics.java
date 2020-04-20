package de.elektropapst.ld46;

import com.badlogic.ashley.core.PooledEngine;
import de.elektropapst.ld46.assets.Assets;
import de.elektropapst.ld46.assets.SoundManager;

public class Statics {

    public static PooledEngine ashley;
    public static Assets assets;
    public static LDGame game;
    public static SoundManager sound;

    public static GameSettings settings;

    public static int gameScore = 0;
    public static int deadPlants;
    public static int alivePlants;

    public static void initializeStatics() {
        ashley = new PooledEngine();
        assets = new Assets();
        settings = new GameSettings();
        sound = new SoundManager();
    }

    public static void disposeStatics() {
        assets.dispose();
    }

}
