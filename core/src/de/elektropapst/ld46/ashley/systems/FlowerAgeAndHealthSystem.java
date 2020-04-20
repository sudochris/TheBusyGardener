package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.FlowerComponent;

import static de.elektropapst.ld46.Statics.*;

public class FlowerAgeAndHealthSystem extends GameSystem {

    private ImmutableArray<Entity> entities;
    public FlowerAgeAndHealthSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(FlowerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity e : entities) {
            FlowerComponent fc = ComponentMappers.flower.get(e);
            if(fc.health > 0.0) { // Only living flowers
                fc.age += deltaTime;
                if (fc.needWater) {
                    fc.health = MathUtils.clamp(fc.health - deltaTime, 0.0f, settings.TIME_FOR_WATERING);
                } else if (fc.health < settings.TIME_FOR_WATERING) {
                    fc.health = MathUtils.clamp(fc.health + deltaTime, 0.0f, settings.TIME_FOR_WATERING);
                }
            }

        }
    }

    @Override
    public void dispose() {

    }
}
