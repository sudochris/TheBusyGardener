package de.elektropapst.ld46.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import de.elektropapst.ld46.Statics;
import de.elektropapst.ld46.ai.FarmGraph;
import de.elektropapst.ld46.ashley.components.*;
import de.elektropapst.ld46.ashley.systems.*;
import de.elektropapst.ld46.assets.enums.Musics;
import de.elektropapst.ld46.assets.enums.Textures;

public class GameScreen extends GdxScreen {
    private static final String TAG = "GAMESCREEN";
    private OrthographicCamera camera;
    private ImmutableArray<Entity> dirtyEntities;
    private Music music;
    private FarmGraph farmGraph;


    public GameScreen() {
        create();
    }

    private void create() {
        TiledMap map = Statics.assets.getTiledMap();
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        TiledMapTileLayer occupationLayer = (TiledMapTileLayer) map.getLayers().get("OCCUPATION");

        farmGraph = new FarmGraph((TiledMapTileLayer)map.getLayers().get("OCCUPATION"));


        camera = new OrthographicCamera(width, height);
        camera.translate(width >> 1, height >> 1);
        Statics.ashley.addSystem(new RenderSystem(1));
        Statics.ashley.addSystem(new MapRenderSystem(2, map, camera));
        Statics.ashley.addSystem(new EntityRenderSystem(3));

        Statics.ashley.addSystem(new PlayerActionSystem(4, farmGraph));
        Statics.ashley.addSystem(new FlowerRequirementsSystem(5));
        Statics.ashley.addSystem(new FlowerSpawnSystem(6, occupationLayer, farmGraph));
        Statics.ashley.addSystem(new FlowerAgeAndHealthSystem(7));
        Statics.ashley.addSystem(new LoseConditionSystem(8));
        Statics.ashley.addSystem(new UISystem(9));
        Statics.ashley.addSystem(new InputSystem(10, map, camera, farmGraph));

        Entity player = Statics.ashley.createEntity();

        PositionComponent pc = Statics.ashley.createComponent(PositionComponent.class);
        pc.worldPosition.set(0, 3);

        TextureRegion[][] tmp = Statics.assets.getTextureAtlasRegion(Textures.FARMER).split(64, 64);
        PlayerComponent plc = Statics.ashley.createComponent(PlayerComponent.class);
        PlayerInventoryComponent pic = Statics.ashley.createComponent(PlayerInventoryComponent.class);

        float animationDuration = 0.25f;
        plc.upAnimation     = new Animation<TextureRegion>(animationDuration, tmp[4][0], tmp[4][1]);
        plc.downAnimation   = new Animation<TextureRegion>(animationDuration, tmp[3][0], tmp[3][1]);
        plc.leftAnimation   = new Animation<TextureRegion>(animationDuration, tmp[0][4], tmp[0][3]);
        plc.rightAnimation  = new Animation<TextureRegion>(animationDuration, tmp[0][0], tmp[2][2]);

        // PLAYER
        Statics.ashley.addEntity(player.add(plc).add(pc).add(pic));

        dirtyEntities = Statics.ashley.getEntitiesFor(Family.all(DirtyComponent.class).get());

        music = Statics.assets.getMusic(Musics.GAME_MUSIC);//.play();
        music.setVolume(Statics.settings.MUSIC_VOLUME);
        music.setPosition(0);
        music.setLooping(true);
        music.play();

    }


    @Override
    public void render(float delta) {
        super.render(delta);
        Statics.ashley.update(delta);
        if (dirtyEntities.size() > 0) {
            for (Entity entity : dirtyEntities) {
                Statics.ashley.removeEntity(entity);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Statics.ashley.removeAllEntities();
        Statics.ashley.clearPools();
        for (EntitySystem system : Statics.ashley.getSystems()) {
            Statics.ashley.removeSystem(system);
        }
        music.stop();
    }

    @Override
    public void reset() {
        dispose();
        create();
    }
}
