package de.elektropapst.ld46.ai;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

public class FarmConnection extends DefaultConnection<FarmNode> {
    public FarmConnection(FarmNode fromNode, FarmNode toNode) {
        super(fromNode, toNode);
    }
}
