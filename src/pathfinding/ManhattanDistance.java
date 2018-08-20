package pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class ManhattanDistance implements Heuristic<PathNode> {

    @Override
    public float estimate(PathNode node, PathNode endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }

}