package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ashley.components.FlowerComponent;

import java.awt.*;

public class UISystem extends GameSystem {
    private Batch batch;
    private BitmapFont font;
    private ImmutableArray<Entity> flowers;
    private float deltaSum = 0.0f;

    public UISystem(int priority) {
        super(priority);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        flowers = engine.getEntitiesFor(Family.all(FlowerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        deltaSum += deltaTime;

        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, "Flowers alive: " + Statics.alivePlants, 0, Gdx.graphics.getHeight()-16, Gdx.graphics.getWidth(), Align.center, false);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
