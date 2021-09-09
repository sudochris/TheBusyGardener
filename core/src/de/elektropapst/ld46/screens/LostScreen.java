package de.elektropapst.ld46.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ashley.components.DirtyComponent;
import de.elektropapst.ld46.ashley.components.PlayerComponent;
import de.elektropapst.ld46.ashley.components.PlayerInventoryComponent;
import de.elektropapst.ld46.ashley.components.PositionComponent;
import de.elektropapst.ld46.ashley.systems.*;
import de.elektropapst.ld46.assets.enums.Textures;

public class LostScreen extends GdxScreen {
    private static final String TAG = "LOSTSCREEN";
    private Batch batch;
    private BitmapFont font;

    private float countDownTime;
    private Color countDownColor = new Color(1, 1, 1, 1);

    public LostScreen() {
        create();
    }

    private void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        countDownTime = 8.0f;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        countDownTime -= delta;
        if(countDownTime <= 0.0) {
            Statics.game.setCurrentScreen(Screens.GAME);
        }

        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();
        font.getData().setScale(4);

        font.setColor(Color.WHITE);
        font.draw(batch, "Thank you for playing!", 0, Gdx.graphics.getHeight()-60, Gdx.graphics.getWidth(), Align.center, false);
        font.draw(batch, "Your score:", 0, Gdx.graphics.getHeight()-180, Gdx.graphics.getWidth(), Align.center, false);
        font.draw(batch, Statics.gameScore + " flowers alive.", 0, Gdx.graphics.getHeight()-240, Gdx.graphics.getWidth(), Align.center, false);
        float countdownPercentage = countDownTime / 8.0f;
        countDownColor = countDownColor.fromHsv(MathUtils.lerp(120.0f, 0.0f, countdownPercentage), 1.0f, 1.0f);
        font.setColor(countDownColor);
        font.draw(batch, "Try again in " + (int)countDownTime + " seconds", 0, Gdx.graphics.getHeight()>>1, Gdx.graphics.getWidth(), Align.center, false);
        batch.end();

    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    @Override
    public void reset() {
        dispose();
        create();
    }
}
