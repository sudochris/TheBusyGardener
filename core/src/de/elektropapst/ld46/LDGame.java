package de.elektropapst.ld46;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;
import de.elektropapst.ld46.screens.*;

public class LDGame extends Game {
    private static final String TAG = "LDGAME";

    private static ObjectMap<Screens, GdxScreen> screens;
    private boolean screensInitialized = false;

    public LDGame() {

        if (Statics.game == null) {
            Statics.game = this;
        } else {
            Gdx.app.error(TAG, "Error. Creating another GameInstance...");
        }
    }

    @Override
    public void create() {
        screens = new ObjectMap<Screens, GdxScreen>();
        screens.put(Screens.LOADING, new LoadingScreen());
        setCurrentScreen(Screens.LOADING);
    }

    private void initScreens() {
        if (!screensInitialized) {
            screens.put(Screens.GAME, new GameScreen());
            screens.put(Screens.LOST, new LostScreen());
            screensInitialized = true;
        }
    }

    public void setCurrentScreen(Screens screen) {
        if (screen != Screens.LOADING) {
            initScreens();
        }
        screens.get(screen).reset();
        this.setScreen(screens.get(screen));
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Screen screen : screens.values()) {
            screen.dispose();
        }
    }
}
