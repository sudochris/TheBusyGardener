package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import de.elektropapst.ld46.ai.FarmGraph;
import de.elektropapst.ld46.ai.actions.PlayerAction;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.*;
import de.elektropapst.ld46.assets.enums.Sounds;
import de.elektropapst.ld46.assets.enums.Textures;

import static de.elektropapst.ld46.Statics.*;
import static de.elektropapst.ld46.Statics.ashley;
import static de.elektropapst.ld46.Statics.assets;
import static de.elektropapst.ld46.ashley.components.PlayerComponent.*;

public class PlayerActionSystem extends GameSystem {

    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> flowerEntities;
    private FarmGraph farmGraph;

    public PlayerActionSystem(int priority, FarmGraph farmGraph) {
        super(priority);
        this.farmGraph = farmGraph;
    }


    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        flowerEntities = engine.getEntitiesFor(Family.all(FlowerComponent.class).get());
    }

    float deltasum = 0.0f;
    boolean walkingSoundPlaying = false;
    private long stepSoundId;
    private PlayerAction previousAction = PlayerAction.createNoOpAction();
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(playerEntities.size() != 1) return;

        Entity player = playerEntities.first();
        PlayerComponent plc = ComponentMappers.player.get(player);
        PlayerInventoryComponent pic = ComponentMappers.inventory.get(player);
        PositionComponent pc = ComponentMappers.position.get(player);

        deltasum += deltaTime;
        if(deltasum >= settings.PLAYER_MOVE_STEP) {
            deltasum -= settings.PLAYER_MOVE_STEP;
            // DO MOVE
            PlayerAction action = plc.getNextAction();
            if(action.type == PlayerAction.Type.MOVE) {
                if(!walkingSoundPlaying) {
                    stepSoundId = sound.loopSound(Sounds.GRASS_STEPS);
                    walkingSoundPlaying = true;
                }

                if(action.actionLocation.x > pc.worldPosition.x) {
                    plc.direction = Direction.RIGHT;
                } else if(action.actionLocation.x < pc.worldPosition.x){
                    plc.direction = Direction.LEFT;
                } else {
                    if(action.actionLocation.y > pc.worldPosition.y) {
                        plc.direction = Direction.UP;
                    } else if(action.actionLocation.y < pc.worldPosition.y) {
                        plc.direction = Direction.DOWN;
                    } else {
                        plc.direction = Direction.DOWN;
                    }
                }
                pc.worldPosition.set(action.actionLocation);
            }
            if(action.type == PlayerAction.Type.WATERING) {
                waterTile(action.actionLocation);
                sound.playSound(Sounds.WATERING);
            }
            if(action.type == PlayerAction.Type.DIGOUT) {
                Entity flower = getFlowerEntity(action.actionLocation);
                if(flower != null) {
                    FlowerComponent flowerComponent = ComponentMappers.flower.get(flower);
                    if(pic.hasSpace()) {
                        pic.insert(flowerComponent.flowerType);
                        flower.add(ashley.createComponent(DirtyComponent.class));

                        farmGraph.addFourConnectivity((int) action.actionLocation.x, (int) action.actionLocation.y);
                        sound.playSound(Sounds.SHOVEL);
                    }
                }
            }
            if(action.type == PlayerAction.Type.PLANT) {
                if (playerEntities.size() > 0) {
                    PositionComponent playerPosition = ComponentMappers.position.get(playerEntities.first());
                    if(playerPosition.worldPosition.x != action.actionLocation.x || playerPosition.worldPosition.y != action.actionLocation.y) {
                        createFlowerAt(action.actionLocation, pic.pull(action.slot));
                        farmGraph.removeAllConnectionsFromNode((int) action.actionLocation.x, (int) action.actionLocation.y);
                        sound.playSound(Sounds.PLANT);
                    }
                }
            }
            if(action.type == PlayerAction.Type.NOOP) {
                sound.stopSound(Sounds.GRASS_STEPS, stepSoundId);
                walkingSoundPlaying = false;
            }
        }
    }

    public Entity getFlowerEntity(Vector2 position) {
        for (Entity e : flowerEntities) {
            PositionComponent thatPc = ComponentMappers.position.get(e);
            if (thatPc.worldPosition.epsilonEquals(position, 0.0f)) {
                return e;
            }
        }
        return null; // THIS IS BAD, BUT I HAVE NO TIME :(
    }

    private boolean waterTile(Vector2 position) {
        for (Entity e : flowerEntities) {
            PositionComponent thatPc = ComponentMappers.position.get(e);
            if (thatPc.worldPosition.epsilonEquals(position, 0.0f)) {
                FlowerComponent fc = ComponentMappers.flower.get(e);
                fc.needWater = false;
                return true;
            }
        }
        return false;
    }

    //DUPLICATE
    @Deprecated
    public boolean hasFlower(Vector2 position) {
        for (Entity e : ashley.getEntitiesFor(Family.all(FlowerComponent.class).get())) {
            PositionComponent thatPc = ComponentMappers.position.get(e);
            if (thatPc.worldPosition.epsilonEquals(position, 0.0f)) {
                return true;
            }
        }
        return false;
    }

    // DUPLICATE
    @Deprecated
    private void createFlowerAt(Vector2 position, FlowerComponent.FlowerType flowerType) {
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

            ashley.addEntity(ashley.createEntity().add(pc).add(tc).add(fc));
        }
    }

    @Override
    public void dispose() {

    }
}
