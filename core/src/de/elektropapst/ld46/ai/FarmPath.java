package de.elektropapst.ld46.ai;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;

public class FarmPath extends DefaultGraphPath<FarmNode> implements SmoothableGraphPath<FarmNode, Vector2> {

    private Vector2 tmpPosition = new Vector2();

    @Override
    public Vector2 getNodePosition(int index) {
        FarmNode node = nodes.get(index);
        return tmpPosition.set(node.x, node.y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        nodes.set(index1, nodes.get(index2));
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }
}
