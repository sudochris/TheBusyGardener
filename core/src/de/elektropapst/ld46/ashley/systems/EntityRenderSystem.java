package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.FlowerComponent;
import de.elektropapst.ld46.ashley.components.PlayerComponent;
import de.elektropapst.ld46.ashley.components.PositionComponent;
import de.elektropapst.ld46.ashley.components.TextureRegionComponent;
import de.elektropapst.ld46.assets.enums.Textures;

import static de.elektropapst.ld46.Statics.*;


public class EntityRenderSystem extends GameSystem {
    private ImmutableArray<Entity> entities;
    private Batch batch;

    public EntityRenderSystem(int priority) {
        super(priority);
        batch = new SpriteBatch();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family
                .all(PositionComponent.class)
                .one(TextureRegionComponent.class, PlayerComponent.class)
                .get());
    }

    private Color oldColor = new Color();
    private Color newColor = new Color(0, 0, 0, 1);
    private float deltaSum = 0.0f;
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        deltaSum += deltaTime;
        PositionComponent pc;
        TextureRegionComponent tr;

        batch.begin();

        for(Entity e : entities) {
            pc = ComponentMappers.position.get(e);
            if(ComponentMappers.flower.has(e)) {
                // ITs a flower
                FlowerComponent fc = ComponentMappers.flower.get(e);
                tr = ComponentMappers.textureRegion.get(e);
                Vector2 pd = fc.pixelDisplacement;
                oldColor.set(batch.getColor());
                batch.setColor(1, 1.0f, 1.0f, MathUtils.clamp(fc.age, 0.0f, 1.0f));
                TextureRegion flowerTexture = tr.textureRegion;
                if(fc.health <= 0.0) {
                    flowerTexture = assets.getTextureAtlasRegion(Textures.DEADFLOWER);
                }
                batch.draw(flowerTexture, pc.worldPosition.x * settings.TILE_SIZE + pd.x, pc.worldPosition.y * settings.TILE_SIZE + pd.y);
                batch.setColor(oldColor);

            } else if(ComponentMappers.player.has(e)) {
                PlayerComponent plc = ComponentMappers.player.get(e);
                batch.draw(plc.getAnimation().getKeyFrame(deltaSum, true), pc.worldPosition.x * settings.TILE_SIZE, pc.worldPosition.y * settings.TILE_SIZE);
            } else {
                tr = ComponentMappers.textureRegion.get(e);
                batch.draw(tr.textureRegion, pc.worldPosition.x * settings.TILE_SIZE, pc.worldPosition.y * settings.TILE_SIZE);
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
