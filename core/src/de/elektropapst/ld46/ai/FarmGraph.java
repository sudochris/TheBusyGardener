package de.elektropapst.ld46.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FarmGraph implements IndexedGraph<FarmNode> {
    public static int sizeX = 0;
    public static int sizeY = 0;
    private TiledMapTileLayer occupation;
    public static final int BLOCKED_ID = 100;

    private Array<FarmNode> nodes;

    public FarmGraph(TiledMapTileLayer occupation) {
        sizeX = occupation.getWidth();
        sizeY = occupation.getHeight();
        this.occupation = occupation;
        nodes = new Array<FarmNode>(sizeX * sizeY);
        generateGraph(occupation);
    }

    private boolean isTileBlocked(int x, int y) {
        TiledMapTile tile = occupation.getCell(x, y).getTile();
        if(tile != null) {
            return tile.getId() == BLOCKED_ID;
        }
        return false;
    }

    private void generateGraph(TiledMapTileLayer layer) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    nodes.add(new FarmNode(x, y));
                }
            }
        }

        for (int x = 0; x < sizeX; x++) {
            int idx = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                addFourConnectivity(x, y);
            }
        }
    }

    public void addFourConnectivity(int x, int y) {
        FarmNode node = getNode(x, y);
        if (x > 0) addConnection(node, -1, 0);
        if (y > 0) addConnection(node, 0, -1);
        if (x < sizeX - 1) addConnection(node, 1, 0);
        if (y < sizeY - 1) addConnection(node, 0, 1);
    }
    public void removeAllConnectionsFromNode(int x, int y) {
        getNode(x, y).getConnections().clear();
    }

    private void addConnection(FarmNode node, int xOffset, int yOffset) {
        int targetX = node.x + xOffset;
        int targetY = node.y + yOffset;
        if(!isTileBlocked(targetX, targetY)) {
            FarmNode target = getNode(targetX, targetY);
            node.getConnections().add(new FarmConnection(node, target));
        }
    }

    public FarmNode getNode(int x, int y) {
        return nodes.get(x * sizeY + y);
    }
    public FarmNode getNode(Vector2 location) {
        return nodes.get((int) (location.x * sizeY + location.y));
    }

    @Override
    public int getIndex(FarmNode node) {
        return nodes.indexOf(node, true);
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<FarmNode>> getConnections(FarmNode fromNode) {
        return fromNode.getConnections();
    }
}
