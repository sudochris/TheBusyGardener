package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;
import de.elektropapst.ld46.ai.*;
import de.elektropapst.ld46.ai.actions.PlayerAction;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.*;
import de.elektropapst.ld46.assets.enums.Textures;

import static de.elektropapst.ld46.Statics.*;
import static de.elektropapst.ld46.ashley.ComponentMappers.flower;

public class InputSystem extends GameSystem {
    private static final String TAG = "INPUTSYSTEM";

    private Batch batch;
    private TextureRegion menuTexture;
    private TextureRegion selectionTexture;
    private final TiledMap map;
    private OrthographicCamera camera;
    private FarmGraph farmGraph;

    public static final int BLOCKED_ID = 100;
    public static final int BUILDABLE_ID = 99;

    private boolean menuOpen = false;
    private Vector2 menuTilePosition = new Vector2();
    private Vector3 tmpVec3 = new Vector3();

    private ImmutableArray<Entity> flowers;
    private ImmutableArray<Entity> playerEntities;

    IndexedAStarPathFinder<FarmNode> pathFinder;
    FarmPath farmPath = new FarmPath();
    FarmManhattanDistance heuristic;

    private Vector2 screen2Tile(int screeX, int screenY) {
        Vector3 unproject = camera.unproject(tmpVec3.set(screeX, screenY, 0));
        return new Vector2(MathUtils.floor(unproject.x), MathUtils.floor(unproject.y));
    }

    private Vector2 screen2Tile(Vector2 screen) {
        return screen2Tile(screen);
    }

    public InputSystem(int priority, final TiledMap map, OrthographicCamera camera, FarmGraph farmGraph) {
        super(priority);
        this.map = map;
        this.camera = camera;
        this.farmGraph = farmGraph;
        batch = new SpriteBatch();
        menuTexture = assets.getTextureAtlasRegion(Textures.MENU);
        selectionTexture = assets.getTextureAtlasRegion(Textures.VALIDSELECTION);

        pathFinder = new IndexedAStarPathFinder<FarmNode>(farmGraph, true);
        heuristic = new FarmManhattanDistance();


        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 clickedTile = screen2Tile(Gdx.input.getX(), Gdx.input.getY());

                if (button == Input.Buttons.LEFT) {

                    if (!menuOpen && isBuildable(clickedTile)) {
                        menuTilePosition.set(clickedTile);
                        menuOpen = true;
                    } else {
                        if(clickedTile.x == menuTilePosition.x && clickedTile.y == menuTilePosition.y)
                            appendMoveAction(menuTilePosition);
                        else if (clickedTile.x == menuTilePosition.x + 1 && clickedTile.y == menuTilePosition.y + 2)
                            appendPlantAction(menuTilePosition, 0);
                        else if (clickedTile.x == menuTilePosition.x + 2 && clickedTile.y == menuTilePosition.y + 2)
                            appendPlantAction(menuTilePosition, 1);
                        else if (clickedTile.x == menuTilePosition.x + 1 && clickedTile.y == menuTilePosition.y + 1)
                            appendPlantAction(menuTilePosition, 2);
                        else if (clickedTile.x == menuTilePosition.x + 2 && clickedTile.y == menuTilePosition.y + 1)
                            appendPlantAction(menuTilePosition, 3);
                        else if (clickedTile.x == menuTilePosition.x + 1 && clickedTile.y == menuTilePosition.y + 0)
                            appendWateringAction(menuTilePosition);
                        else if (clickedTile.x == menuTilePosition.x + 2 && clickedTile.y == menuTilePosition.y + 0)
                            appendDigOutAction(menuTilePosition);

                        menuOpen = false;
                    }
                }
                if (button == Input.Buttons.RIGHT) {
                    menuOpen = false;
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }
        });
    }

    private void appendMoveAction(Vector2 clickedPosition) {
        if(playerEntities.size() > 0) {
            PlayerComponent plc = ComponentMappers.player.get(playerEntities.first());
            Queue<PlayerAction> moveActions = moveToActions(clickedPosition);
            for(PlayerAction moveAction : moveActions) {
                plc.addAction(moveAction);
            }
            plc.replaceLastAction(PlayerAction.createNoOpAction());
        }
    }

    public boolean appendWateringAction(Vector2 clickedPosition) {
        if(playerEntities.size() > 0) {
            PlayerComponent plc = ComponentMappers.player.get(playerEntities.first());
            Queue<PlayerAction> moveActions = moveToActions(clickedPosition);
            for(PlayerAction moveAction : moveActions) {
                plc.addAction(moveAction);
            }
            if(!moveActions.isEmpty()) {
                plc.replaceLastAction(PlayerAction.createWateringAction(clickedPosition, moveActions.last().playerPosLocation));
            } else {
                plc.replaceLastAction(PlayerAction.createNoOpAction());
            }
            return true;
        }
        return false;
    }

    public boolean appendDigOutAction(Vector2 clickedPosition) {
        if(hasFlower(clickedPosition)) {
            if(playerEntities.size() > 0) {
                PlayerComponent plc = ComponentMappers.player.get(playerEntities.first());
                Queue<PlayerAction> moveActions = moveToActions(clickedPosition);
                for (PlayerAction moveAction : moveActions) {
                    plc.addAction(moveAction);
                }
                if (!moveActions.isEmpty())
                    plc.replaceLastAction(PlayerAction.createDigOutAction(clickedPosition, moveActions.last().playerPosLocation));
                else {
                    plc.replaceLastAction(PlayerAction.createNoOpAction());
                }
                return true;
            }
        }
        return false;
    }

    public boolean appendPlantAction(Vector2 clickedPosition, int slot) {
        if(!hasFlower(clickedPosition)) {
            if(playerEntities.size() > 0) {
                PlayerComponent plc = ComponentMappers.player.get(playerEntities.first());
                PlayerInventoryComponent pic = ComponentMappers.inventory.get(playerEntities.first());
                if (pic.get(slot) != FlowerComponent.FlowerType.EMPTY) {

                    Queue<PlayerAction> moveActions = moveToActions(clickedPosition);
                    for(PlayerAction moveAction : moveActions) {
                        plc.addAction(moveAction);
                    }
                    if(!moveActions.isEmpty()) {
                        plc.replaceLastAction(PlayerAction.createPlantAction(clickedPosition, moveActions.last().playerPosLocation, slot));
                        return true;
                    } else {
                        plc.replaceLastAction(PlayerAction.createNoOpAction());
                    }
                }
            }
        }
        return false;
    }

    private Queue<PlayerAction> moveToActions(Vector2 location) {
        Queue<PlayerAction> moveActions = new Queue<PlayerAction>();

        if(playerEntities.size() > 0) {
            Entity player = playerEntities.first();
            PlayerComponent plc = ComponentMappers.player.get(player);
            Vector2 lastPlayerPosition = plc.getLastPlayerPosition();

            farmPath.clear();
            boolean pathFound = pathFinder.searchNodePath(
                    farmGraph.getNode(lastPlayerPosition),
                    farmGraph.getNode(location),
                    heuristic, farmPath);

            if(pathFound) {
                for (int i = 0; i < farmPath.nodes.size; i++) {
                    FarmNode current = farmPath.nodes.get(i);
                    moveActions.addLast(PlayerAction.createMoveAction(current.x, current.y));
//                    plc.addAction();
                }
            } else {
                System.out.println("NO PATH FOUND");
            }
        }

        return moveActions;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        flowers = engine.getEntitiesFor(Family.all(PositionComponent.class, FlowerComponent.class).get());
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    private boolean isBuildable(Vector2 position) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) map.getLayers().get("OCCUPATION")).getCell(
                (int) position.x,
                (int) position.y);
        if(cell == null) {
            return false;
        }
        return cell.getTile().getId() == BUILDABLE_ID;
    }

    public boolean hasFlower(Vector2 position) {
        for (Entity e : flowers) {
            PositionComponent thatPc = ComponentMappers.position.get(e);
            FlowerComponent fc = flower.get(e);
            if (thatPc.worldPosition.epsilonEquals(position, 0.0f)) {
                if(fc.health > 0) // ONLY LIVING FLOWERS!
                    return true;
            }
        }
        return false;
    }

    private TextureRegion textureRegionFromFlowerType(FlowerComponent.FlowerType flowerType) {
        switch (flowerType) {
            case RED:
                return assets.getTextureAtlasRegion(Textures.REDFLOWER);
            case ORANGE:
                return assets.getTextureAtlasRegion(Textures.ORANGEFLOWER);
            case BLUE:
                return assets.getTextureAtlasRegion(Textures.BLUEFLOWER);
            case WHITE:
                return assets.getTextureAtlasRegion(Textures.WHITEFLOWER);
            case EMPTY:
            default:
                return assets.getTextureAtlasRegion(Textures.DEADFLOWER);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        batch.begin();
        if (menuOpen) {
            batch.setColor(1, 1, 1, 0.8f);
            batch.draw(menuTexture, menuTilePosition.x * settings.TILE_SIZE, menuTilePosition.y * settings.TILE_SIZE);
            if(playerEntities.size() > 0) {
                Entity player = playerEntities.first();
                PlayerInventoryComponent pic = ComponentMappers.inventory.get(player);
                FlowerComponent.FlowerType[] flowerInventory = pic.getFlowerInventory();

                if(flowerInventory[0] != FlowerComponent.FlowerType.EMPTY)
                    batch.draw(textureRegionFromFlowerType(flowerInventory[0]),(menuTilePosition.x + 1) * settings.TILE_SIZE, (menuTilePosition.y + 2) * settings.TILE_SIZE);
                if(flowerInventory[1] != FlowerComponent.FlowerType.EMPTY)
                    batch.draw(textureRegionFromFlowerType(flowerInventory[1]),(menuTilePosition.x + 2) * settings.TILE_SIZE, (menuTilePosition.y + 2) * settings.TILE_SIZE);
                if(flowerInventory[2] != FlowerComponent.FlowerType.EMPTY)
                    batch.draw(textureRegionFromFlowerType(flowerInventory[2]),(menuTilePosition.x + 1) * settings.TILE_SIZE, (menuTilePosition.y + 1) * settings.TILE_SIZE);
                if(flowerInventory[3] != FlowerComponent.FlowerType.EMPTY)
                    batch.draw(textureRegionFromFlowerType(flowerInventory[3]),(menuTilePosition.x + 2) * settings.TILE_SIZE, (menuTilePosition.y + 1) * settings.TILE_SIZE);

            }

            batch.draw(assets.getTextureAtlasRegion(Textures.WATERINGCAN),
                    (menuTilePosition.x + 1) * settings.TILE_SIZE, (menuTilePosition.y + 0) * settings.TILE_SIZE);
            batch.draw(assets.getTextureAtlasRegion(Textures.SHOVEL),
                    (menuTilePosition.x + 2) * settings.TILE_SIZE, (menuTilePosition.y + 0) * settings.TILE_SIZE);
        } else {
            Vector2 hoverTile = screen2Tile(Gdx.input.getX(), Gdx.input.getY());
            if(isBuildable(hoverTile)) {
                batch.draw(selectionTexture, hoverTile.x * settings.TILE_SIZE, hoverTile.y * settings.TILE_SIZE);
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
