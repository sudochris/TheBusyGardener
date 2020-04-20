package de.elektropapst.ld46.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class FarmManhattanDistance implements Heuristic<FarmNode> {
    @Override
    public float estimate(FarmNode node, FarmNode endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
