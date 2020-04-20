package de.elektropapst.ld46.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.elektropapst.ld46.Statics;

public class LoadingScreen extends GdxScreen {
    private static final String TAG = "LOADINGSCREEN";
    private ShapeRenderer shapeRenderer;

    public LoadingScreen() {
        init();
        Statics.initializeStatics();
    }

    private void init() {
        shapeRenderer = new ShapeRenderer();
    }

    private void update(float delta) {
        if (Statics.assets.load()) {
            Statics.game.setCurrentScreen(Screens.GAME);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        float progress = Statics.assets.progress();
        float val = 191 * progress;
        Gdx.gl.glClearColor(val / 255f, val / 255f, val / 255f, 1f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.25f, 0.25f, 0.25f, 1);
        float width = 300;
        float height = 20;
        float x = (Gdx.graphics.getWidth() - width) / 2;
        float y = (Gdx.graphics.getHeight() - height) / 2;
        shapeRenderer.rect(x, y, width * progress, height);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void reset() {
    }
}