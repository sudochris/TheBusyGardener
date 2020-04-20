package de.elektropapst.ld46.ashley.components;

import com.badlogic.ashley.core.Component;

public class PlayerInventoryComponent implements Component {
    private int inventorySize = 4;

    private FlowerComponent.FlowerType[] flowerInventory = new FlowerComponent.FlowerType[inventorySize];

    public PlayerInventoryComponent() {
        for (int i = 0; i < inventorySize; i++) {
            flowerInventory[i] = FlowerComponent.FlowerType.EMPTY;
        }
    }

    public int insert(FlowerComponent.FlowerType flowerType) {
        if(hasSpace()) {
            for (int i = 0; i < inventorySize; i++) {
                if(flowerInventory[i] == FlowerComponent.FlowerType.EMPTY) {
                    flowerInventory[i] = flowerType;
                    return i;
                }
            }
        }
        return -1;
    }

    public FlowerComponent.FlowerType get(int idx) {
        return  flowerInventory[idx];
    }

    public FlowerComponent.FlowerType[] getFlowerInventory() {
        return flowerInventory;
    }

    public boolean hasSpace() {
        for (int i = 0; i < inventorySize; i++) {
            if(flowerInventory[i] == FlowerComponent.FlowerType.EMPTY) {
                return true;
            }
        }
        return false;
    }

    public FlowerComponent.FlowerType pull(int slot) {
        FlowerComponent.FlowerType flowerType = get(slot);
        flowerInventory[slot] = FlowerComponent.FlowerType.EMPTY;
        return flowerType;
    }
}
