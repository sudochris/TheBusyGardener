package de.elektropapst.ld46.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import de.elektropapst.ld46.Statics;

public class FlowerComponent implements Component, Pool.Poolable {
    public FlowerType flowerType = FlowerType.BLUE;
    public Vector2 pixelDisplacement = new Vector2(MathUtils.random(-14, 14), MathUtils.random(-14, 14));

    public float health = Statics.settings.TIME_FOR_WATERING;   // maxHealth

    public float age = 0;
    public boolean needWater = false;
    public float sinOffset = MathUtils.random(0, MathUtils.PI2);


    public static enum FlowerType {
        EMPTY, RED, ORANGE, BLUE, WHITE
    }

    public FlowerComponent() {
    }

    @Override
    public void reset() {
        flowerType = FlowerType.BLUE;
        pixelDisplacement = new Vector2(MathUtils.random(-14, 14), MathUtils.random(-14, 14));
        health = Statics.settings.TIME_FOR_WATERING;
        age = 0;
        needWater = true;
        sinOffset = MathUtils.random(0, MathUtils.PI2);
    }

}
