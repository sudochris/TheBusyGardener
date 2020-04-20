package de.elektropapst.ld46.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import de.elektropapst.ld46.ai.actions.PlayerAction;
import de.elektropapst.ld46.ashley.systems.InputSystem;

public class PlayerComponent implements Component {
    private Queue<PlayerAction> playerActionQueue = new Queue<PlayerAction>();
    private PlayerAction noOpAction = PlayerAction.createNoOpAction();
    private Vector2 lastPlayerPosition = new Vector2(0, 3);
    private Vector2 lastActionLocation = new Vector2(0, 3);
    public Animation<TextureRegion> rightAnimation;
    public Animation<TextureRegion> leftAnimation;
    public Animation<TextureRegion> downAnimation;
    public Animation<TextureRegion> upAnimation;
    public Direction direction = Direction.DOWN;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Animation<TextureRegion> getAnimation() {
        switch(direction) {
            case UP: return upAnimation;
            case DOWN: return downAnimation;
            case LEFT: return leftAnimation;
            case RIGHT: return rightAnimation;
        }
        return downAnimation;
    }

    public Vector2 getLastPlayerPosition() {
        return lastPlayerPosition;
    }

    public void addAction(PlayerAction action) {
        playerActionQueue.addLast(action);
        lastActionLocation.set(action.actionLocation);
        lastPlayerPosition.set(action.playerPosLocation);
    }

    public PlayerAction getNextAction() {
        if(playerActionQueue.size == 0) {
            return noOpAction;
        }
        return playerActionQueue.removeFirst();
    }

    public void replaceLastAction(PlayerAction playerAction) {
        if(playerActionQueue.size > 1) {
            playerActionQueue.removeLast();
            PlayerAction prev = playerActionQueue.get(playerActionQueue.size - 1);
            addAction(playerAction);
            lastActionLocation.set(prev.actionLocation);
            lastPlayerPosition.set(prev.playerPosLocation);
        }
    }

    public Queue<PlayerAction> getActionQueue() {
        return playerActionQueue;
    }
}
