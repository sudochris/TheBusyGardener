package de.elektropapst.ld46.ashley;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import de.elektropapst.ld46.ashley.components.*;

public class ComponentMappers {
    public static ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static ComponentMapper<TextureRegionComponent> textureRegion = ComponentMapper.getFor(TextureRegionComponent.class);
    public static ComponentMapper<FlowerComponent> flower = ComponentMapper.getFor(FlowerComponent.class);
    public static ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static ComponentMapper<PlayerInventoryComponent> inventory = ComponentMapper.getFor(PlayerInventoryComponent.class);

}
