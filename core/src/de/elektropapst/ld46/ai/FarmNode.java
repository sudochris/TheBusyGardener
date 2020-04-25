package de.elektropapst.ld46.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class FarmNode {

    public int x;
    public int y;
    private Array<Connection<FarmNode>> connections;

    public FarmNode(int x, int y) {
        this.x = x;
        this.y = y;
        connections = new Array<Connection<FarmNode>>(8);
    }

    public int getIndex() {
        return x * FarmGraph.sizeY + y;
    }

    public Array<Connection<FarmNode>> getConnections() {
        return connections;
    }
}
