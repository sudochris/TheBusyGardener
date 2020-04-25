package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ai.FarmGraph;
import de.elektropapst.ld46.ai.actions.PlayerAction;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.FlowerComponent;
import de.elektropapst.ld46.ashley.components.PlayerComponent;
import de.elektropapst.ld46.ashley.components.PositionComponent;
import de.elektropapst.ld46.ashley.components.TextureRegionComponent;
import de.elektropapst.ld46.assets.enums.Textures;

import static de.elektropapst.ld46.Statics.*;

public class FlowerSpawnSystem extends GameSystem{

    private static final int BUILDABLE_ID = 99;

    private final TiledMapTileLayer occupationLayer;
    private FarmGraph farmGraph;
    private ImmutableArray<Entity> players;

    private float flowerSpawnIntervalCounter = 0.0f;
    private float flowerSpawnIntervalIncCounter = 0.0f;
    private float currentFlowerSpawnInterval = Statics.settings.INITIAL_FLOWER_SPAWN_INTERVAL;

    public FlowerSpawnSystem(int priority, TiledMapTileLayer occupationLayer, FarmGraph farmGraph) {
        super(priority);
        this.occupationLayer = occupationLayer;
        this.farmGraph = farmGraph;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        flowerSpawnIntervalCounter += deltaTime;
        flowerSpawnIntervalIncCounter += deltaTime;

        if(flowerSpawnIntervalIncCounter >= Statics.settings.FLOWER_SPAWN_INTERVAL_INC_INTERVAL) {
            flowerSpawnIntervalIncCounter -= Statics.settings.FLOWER_SPAWN_INTERVAL_INC_INTERVAL;
            currentFlowerSpawnInterval = MathUtils.clamp(
                    currentFlowerSpawnInterval + settings.FLOWER_SPAWN_INTERVAL_INC_STEP,
                    settings.INITIAL_FLOWER_SPAWN_INTERVAL, settings.FINAL_FLOWER_SPAWN_INTERVAL);
        }

        if(flowerSpawnIntervalCounter >= currentFlowerSpawnInterval) {
            flowerSpawnIntervalCounter -= currentFlowerSpawnInterval;


            boolean spawnPossible = true;
            int x = 0;
            int y = 0;
            do {
                x = MathUtils.random(0, occupationLayer.getWidth() - 1);
                y = MathUtils.random(0, occupationLayer.getHeight() - 1);
                spawnPossible = true;
                if (players.size() > 0) {
                    PlayerComponent playerComponent = ComponentMappers.player.get(players.first());
                    PositionComponent playerPosition = ComponentMappers.position.get(players.first());
                    for (PlayerAction ac : playerComponent.getActionQueue()) {
                        if ((ac.actionLocation.x == x && ac.actionLocation.y == y) || (ac.playerPosLocation.x == x && ac.playerPosLocation.y == y)) {
                            spawnPossible = false;
                        }
                    }
                    if(playerPosition.worldPosition.x == x && playerPosition.worldPosition.y == y) {
                        spawnPossible = false;
                    }
                }
            } while(!spawnPossible);


            FlowerComponent.FlowerType flowerType = FlowerComponent.FlowerType.EMPTY;
            while(flowerType == FlowerComponent.FlowerType.EMPTY)
                flowerType = FlowerComponent.FlowerType.values()[MathUtils.random(0, FlowerComponent.FlowerType.values().length-1)];

            createFlower(new Vector2(x, y), flowerType);
        }
    }

    private boolean isBuildable(Vector2 position) {
        TiledMapTileLayer.Cell cell = occupationLayer.getCell((int) position.x, (int) position.y);
        return cell.getTile().getId() == BUILDABLE_ID;
    }

    public boolean hasFlower(Vector2 position) {
        for (Entity e : ashley.getEntitiesFor(Family.all(FlowerComponent.class).get())) {
            PositionComponent thatPc = ComponentMappers.position.get(e);
            if (thatPc.worldPosition.epsilonEquals(position, 0.0f)) {
                return true;
            }
        }
        return false;
    }
    private void createFlower(Vector2 position, FlowerComponent.FlowerType flowerType) {
        if (isBuildable(position)) {
            addEntityAt(position, flowerType);
        }
    }

    private void addEntityAt(Vector2 position, FlowerComponent.FlowerType flowerType) {
        if (!hasFlower(position)) {
            PositionComponent pc = ashley.createComponent(PositionComponent.class);
            TextureRegionComponent tc = ashley.createComponent(TextureRegionComponent.class);
            FlowerComponent fc = ashley.createComponent(FlowerComponent.class);
            fc.flowerType = flowerType;

            pc.worldPosition = position;
            switch (flowerType) {
                case EMPTY:
                    tc.textureRegion = assets.getTextureAtlasRegion(Textures.VALIDSELECTION);
                case RED:
                    tc.textureRegion = assets.getTextureAtlasRegion(Textures.REDFLOWER);
                    break;
                case ORANGE:
                    tc.textureRegion = assets.getTextureAtlasRegion(Textures.ORANGEFLOWER);
                    break;
                case BLUE:
                    tc.textureRegion = assets.getTextureAtlasRegion(Textures.BLUEFLOWER);
                    break;
                case WHITE:
                    tc.textureRegion = assets.getTextureAtlasRegion(Textures.WHITEFLOWER);
                    break;
            }
            farmGraph.removeAllConnectionsFromNode((int)pc.worldPosition.x, (int) pc.worldPosition.y);
            ashley.addEntity(ashley.createEntity().add(pc).add(tc).add(fc));
        }
    }

    @Override
    public void dispose() {

    }
}
