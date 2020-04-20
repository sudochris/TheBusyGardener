package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.FlowerComponent;
import de.elektropapst.ld46.ashley.components.PositionComponent;

import static de.elektropapst.ld46.Statics.settings;
import static de.elektropapst.ld46.assets.enums.Textures.EMOTES_WATER;

public class FlowerRequirementsSystem extends GameSystem {

    private ImmutableArray<Entity> entities;
    private Batch batch;
    private Texture loading;
    public FlowerRequirementsSystem(int priority) {
        super(priority);
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawLine(0, 0, 1, 1);
        loading = new Texture(pixmap);

    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(FlowerComponent.class, PositionComponent.class).get());
    }

    private float deltaSum = 0.0f;
    private float updateCounter = 0.0f;


    private Color oldColor = new Color();
    private Color newColor = new Color(0, 0, 0, 1);

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        deltaSum += deltaTime;
        updateCounter += deltaTime;
        if(updateCounter >= settings.FLOWER_WATER_REQUIREMENT_TICK) {
            updateCounter -= settings.FLOWER_WATER_REQUIREMENT_TICK;
            for(Entity e : entities) {
                FlowerComponent fc = ComponentMappers.flower.get(e);
                if(!fc.needWater && fc.health >= settings.TIME_FOR_WATERING) { // Only fully healed flowers
                    if(MathUtils.randomBoolean(settings.FLOWER_WATER_REQUIREMENT_CHANCE)) {
                        fc.needWater = true;
                    }
                }
            }
        }

        batch.begin();
        for(Entity e : entities) {
            PositionComponent pc = ComponentMappers.position.get(e);
            FlowerComponent fc = ComponentMappers.flower.get(e);

            boolean needWater = fc.needWater;
            if(needWater && fc.health > 0.0) {
                Vector2 pd = fc.pixelDisplacement;
                batch.draw(Statics.assets.getTextureAtlasRegion(EMOTES_WATER),
                        pc.worldPosition.x * settings.TILE_SIZE + pd.x,
                        ((pc.worldPosition.y + 1) * settings.TILE_SIZE) + pd.y
                                + MathUtils.sin(fc.sinOffset+(deltaSum*2))*8);
            }

            if(fc.health < settings.TIME_FOR_WATERING) {
                float healthPercentage = (fc.health / settings.TIME_FOR_WATERING);

                oldColor.set(batch.getColor());
                newColor = newColor.fromHsv(MathUtils.lerp(0.0f, 120.0f, healthPercentage), 1.0f, 1.0f);
                batch.setColor(newColor);
                batch.draw(loading, pc.worldPosition.x * settings.TILE_SIZE,
                        pc.worldPosition.y * settings.TILE_SIZE, (settings.TILE_SIZE * healthPercentage), 8);
                batch.setColor(oldColor);
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
