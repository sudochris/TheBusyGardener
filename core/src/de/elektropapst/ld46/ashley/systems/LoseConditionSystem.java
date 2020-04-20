package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.FlowerComponent;
import de.elektropapst.ld46.screens.Screens;

public class LoseConditionSystem extends GameSystem {

    private ImmutableArray<Entity> flowers;
    private float timeSinceStart = 0.0f;

    public LoseConditionSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        flowers = engine.getEntitiesFor(Family.all(FlowerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceStart += deltaTime;

        int deadPlants = 0;
        for(Entity e : flowers) {
            FlowerComponent fc = ComponentMappers.flower.get(e);
            if(fc.health <= 0) {
                deadPlants++;
            }
        }

        Statics.deadPlants = deadPlants;
        Statics.alivePlants = flowers.size() - deadPlants;

        if(deadPlants >= Statics.settings.MAX_DEAD_PLANTS) {
            Statics.gameScore = flowers.size() - deadPlants;
            Statics.game.setCurrentScreen(Screens.LOST);
        }

    }

    @Override
    public void dispose() {

    }
}
