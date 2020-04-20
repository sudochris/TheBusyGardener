package de.elektropapst.ld46.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import de.elektropapst.ld46.assets.enums.Musics;
import de.elektropapst.ld46.assets.enums.ShaderPrograms;
import de.elektropapst.ld46.assets.enums.Sounds;
import de.elektropapst.ld46.assets.enums.Textures;

public class Assets implements Disposable {
    private static final String TAG = "ASSETS";

    private final AssetManager manager;

    public Assets() {
        manager = new AssetManager();
        loadTextures();
        loadMusics();
        loadSounds();
        loadFonts();
        loadTileMaps();
    }

    private void loadTileMaps() {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load("map/newmap.tmx", TiledMap.class);
    }

    public boolean load() {
        return manager.update();
    }

    public float progress() {
        return manager.getProgress();
    }

    private void loadTextures() {
        manager.load("sprites/packed.atlas", TextureAtlas.class);
//        for(Textures texture : Textures.values()) {
//            manager.load(texture.fileName, Texture.class);
//        }
    }

    private void loadFonts() {}

    private void loadSounds() {
        for(Sounds sound : Sounds.values()) {
            manager.load(sound.name, Sound.class);
        }
    }

    private void loadMusics() {
        for(Musics music : Musics.values()) {
            manager.load(music.name, Music.class);
        }
    }

    public Sound getSound(Sounds sound) {
        return manager.get(sound.name, Sound.class);
    }

    public Music getMusic(Musics music) {
        return manager.get(music.name, Music.class);
    }

    public TextureRegion getTextureAtlasRegion(Textures texture) {
        return manager.get("sprites/packed.atlas", TextureAtlas.class).findRegion(texture.fileName);
    }

    public TiledMap getTiledMap() {
        return manager.get("map/newmap.tmx", TiledMap.class);
    }

    public ShaderProgram getShader(ShaderPrograms shaderProgram) {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal(shaderProgram.vertexShaderFile),
                Gdx.files.internal(shaderProgram.fragmentShaderFile)
        );

        if (shader.isCompiled()) {
            Gdx.app.debug(TAG, shaderProgram.name() + " compiled.");
        } else {
            Gdx.app.error(TAG, shader.getLog());
        }

        return shader;
    }

        @Override
    public void dispose() {
        manager.dispose();
    }
}
