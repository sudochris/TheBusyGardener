package de.elektropapst.ld46.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;

public abstract class GameSystem extends EntitySystem implements Disposable {
    public GameSystem(int priority) {
        super(priority);
    }

}
