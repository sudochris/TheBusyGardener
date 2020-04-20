package de.elektropapst.ld46.ai.actions;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class PlayerAction {

    public enum Type {
        NOOP, MOVE, WATERING, DIGOUT, PLANT
    }

    public Vector2 playerPosLocation = new Vector2();
    public Vector2 actionLocation = new Vector2();
    public Type type = Type.NOOP;
    public int slot = -1;

    private PlayerAction(Type type, Vector2 actionLocation, Vector2 playerPosLocation) {
        this.type = type;
        this.actionLocation.set(actionLocation);
        this.playerPosLocation.set(playerPosLocation);
    }

    private PlayerAction(Type type, int actionX, int actionY, int playerX, int playerY) {
        this.type = type;
        this.actionLocation.set(actionX, actionY);
        this.playerPosLocation.set(playerX, playerY);
    }

    public static PlayerAction createNoOpAction() {
        return new PlayerAction(Type.NOOP, new Vector2(0, 0), new Vector2(0, 0));
    }

    public static PlayerAction createMoveAction(int x, int y) {
        return new PlayerAction(Type.MOVE, x, y, x, y);
    }

    public static PlayerAction createMoveAction(Vector2 actionLocation, Vector2 playerPosLocation) {
        return new PlayerAction(Type.MOVE, actionLocation, playerPosLocation);
    }

    public static PlayerAction createWateringAction(Vector2 actionLocation, Vector2 playerPosLocation) {
        return new PlayerAction(Type.WATERING, actionLocation, playerPosLocation);
    }

    public static PlayerAction createDigOutAction(Vector2 actionLocation, Vector2 playerPosLocation) {
        return new PlayerAction(Type.DIGOUT, actionLocation, playerPosLocation);
    }

    public static PlayerAction createPlantAction(Vector2 actionLocation, Vector2 playerPosLocation, int inventorySlot) {
        PlayerAction playerAction = new PlayerAction(Type.PLANT, actionLocation, playerPosLocation);
        playerAction.slot = inventorySlot; // bad design. but still no time...
        return playerAction;
    }

}

