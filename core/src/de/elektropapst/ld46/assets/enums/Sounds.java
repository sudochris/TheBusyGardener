package de.elektropapst.ld46.assets.enums;

public enum Sounds {
    GRASS_STEPS("sounds/steps.wav"),
    PLANT("sounds/plant.wav"),
    SHOVEL("sounds/shovel.wav"),
    WATERING("sounds/watering.wav");

    public String name;

    Sounds(String name) {
        this.name = name;
    }
}
