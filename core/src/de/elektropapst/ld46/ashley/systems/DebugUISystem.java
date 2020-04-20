package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import de.elektropapst.ld46.ai.actions.PlayerAction;
import de.elektropapst.ld46.ashley.ComponentMappers;
import de.elektropapst.ld46.ashley.components.PlayerComponent;

public class DebugUISystem extends GameSystem {
    private SpriteBatch batch = new SpriteBatch();
    private BitmapFont font;
    private GLProfiler profiler;
    private OrthographicCamera camera;
    private ImmutableArray<Entity> players;

    public DebugUISystem(int priority, OrthographicCamera camera) {
        super(priority);
        this.camera = camera;
        font = new BitmapFont();
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        batch.begin();
        font.setColor(Color.BLACK);

        if(players.size() > 0) {
            PlayerComponent player = ComponentMappers.player.get(players.first());

            int yOffset = 16;
            for(PlayerAction action : player.getActionQueue()) {
                font.draw(batch, "ActionType: " + action.type, 0, yOffset);
                yOffset += 16;
            }

        }

        Vector3 pos = getMousePosInGameWorld();
        if(camera != null)debugLine(260, "mouse x=" + (int) (pos.x) );
        if(camera != null)debugLine(240, "mouse y=" + (int) (pos.y) );
        if(camera != null)debugLine(220, "cam x=" + camera.position.x);
        if(camera != null)debugLine(200, "cam y=" + camera.position.y);
        debugLine(180, "entities=" + getEngine().getEntities().size());
        if(camera != null)debugLine(160, "zoom=" + camera.zoom);
        debugLine(100, "DC=" + profiler.getCalls());
        debugLine(80, "TXB=" + profiler.getTextureBindings());
        debugLine(60, "FPS=" + Gdx.graphics.getFramesPerSecond());
        batch.end();

        profiler.reset();

    }

    private Vector3 mouse = new Vector3();
    public Vector3 getMousePosInGameWorld() {
        return camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    public void debugLine(float pos, String text){
        font.draw(batch, text, 1070, pos, 200, Align.right, false);
    }

    @Override
    public void dispose() {
        font.dispose();
        profiler.disable();
    }

}
