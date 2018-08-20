package pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

public class PathNode implements IndexedNode<PathNode> {

    public int x;
    public int y;
    private int index;
    private Array<Connection<PathNode>> connections = new Array<>();

    public PathNode(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Array<Connection<PathNode>> getConnections() {
        return connections;
    }

    public void addNeighbour(PathNode node) {
        if (node != null) {
            connections.add(new DefaultConnection<>(this, node));
        }
    }
}

