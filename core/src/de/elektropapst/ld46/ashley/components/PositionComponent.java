package de.elektropapst.ld46.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
    public Vector2 worldPosition = new Vector2(0, 0);

    public PositionComponent() {
    }
}
