package pathfinding;

import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import lia.MapData;
import lia.ObstacleData;

public class PathFinding {

    private static final int OFFSET_FROM_OBSTACLE = 1;

    public boolean[][] obstacleGrid;
    public PathNode[][] nodeGrid;
    public NodeGraph nodeGraph;
    public IndexedAStarPathFinder<PathNode> pathFinder;
    public ManhattanDistance heuristic;
    public PathSmoother<PathNode, Vector2> pathSmoother;

    public PathFinding(MapData mapData) {
        int w = (int) mapData.getWidth();
        int h = (int) mapData.getHeight();

        obstacleGrid = createGridOfObstacles(w, h, mapData.getObstacles());
        nodeGrid = createGridOfWalkableNodes(w, h, obstacleGrid);
        nodeGraph = createGraphOfWalkableNodes(w, h, nodeGrid);

        pathFinder = new IndexedAStarPathFinder<>(nodeGraph, true);
        heuristic = new ManhattanDistance();
        pathSmoother = new PathSmoother<>(new CustomRaycastCollisionDetector<>(nodeGrid));
    }

    /** Creates a 2D grid which contains true wherever the obstacle is located and false where there is none */
    public boolean[][] createGridOfObstacles(int width, int height, ObstacleData[] obstacles) {
        // Create table where true means that there is obstacle
        boolean[][] obstacleGrid = new boolean[width][height];

        for (ObstacleData o : obstacles) {
            int x1 = (int) o.getX() - OFFSET_FROM_OBSTACLE;
            int x2 = (int) (o.getX() + o.getWidth()) + OFFSET_FROM_OBSTACLE;

            for (int x = x1; x < x2; x++) {
                int y1 = (int) o.getY() - OFFSET_FROM_OBSTACLE;
                int y2 = (int) (o.getY() + o.getHeight()) + OFFSET_FROM_OBSTACLE;

                for (int y = y1; y < y2; y++) {
                    // Set field to contain obstacle
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        obstacleGrid[x][y] = true;
                    }
                }
            }
        }

        return obstacleGrid;
    }

    /** Creates a 2D grid of PathNodes which contain a PathNode where the grid is walkable and null elsewhere.
     * PathNodes also holds connections to each other if they are neighbours */
    public PathNode[][] createGridOfWalkableNodes(int width, int height, boolean[][] obstacleGrid) {
        PathNode[][] nodeGrid = new PathNode[width][height];

        int index = 0;
        for (int x = 0; x < obstacleGrid.length; x++) {
            for (int y = 0; y < obstacleGrid[0].length; y++) {
                if (!obstacleGrid[x][y]) {
                    nodeGrid[x][y] = new PathNode(x, y, index++);
                }
            }
        }

        // Add connection to every neighbour of this node
        for (int x = 0; x < nodeGrid.length; x++) {
            for (int y = 0; y < nodeGrid[0].length; y++) {
                PathNode node = nodeGrid[x][y];
                if (node != null) {
                    addNodeNeighbour(nodeGrid, node, x - 1, y); // Node to left
                    addNodeNeighbour(nodeGrid, node, x + 1, y); // Node to right
                    addNodeNeighbour(nodeGrid, node, x, y - 1); // Node below
                    addNodeNeighbour(nodeGrid, node, x, y + 1); // Node above
                    addNodeNeighbour(nodeGrid, node, x - 1, y + 1); // Node top left
                    addNodeNeighbour(nodeGrid, node, x + 1, y + 1); // Node top right
                    addNodeNeighbour(nodeGrid, node, x - 1, y - 1); // Node bottom left
                    addNodeNeighbour(nodeGrid, node, x + 1, y - 1); // Node bottom right
                }
            }
        }

        return nodeGrid;
    }

    private void addNodeNeighbour(PathNode[][] nodesGrid, PathNode node, int x, int y) {
        // Check bounds
        if (x >= 0 && x < nodesGrid.length && y >= 0 && y < nodesGrid[0].length) {
            node.addNeighbour(nodesGrid[x][y]);
        }
    }

    /** Builds a graph of PathNodes from grid of nodes */
    public NodeGraph createGraphOfWalkableNodes(int width, int height, PathNode[][] nodeGrid) {
        NodeGraph nodeGraph = new NodeGraph(width * height);
        for (int x = 0; x < nodeGrid.length; x++) {
            for (int y = 0; y < nodeGrid[0].length; y++) {
                if (nodeGrid[x][y] != null) {
                    nodeGraph.addNode(nodeGrid[x][y]);
                }
            }
        }
        return nodeGraph;
    }

    public PathFollower createPathFollower() {
        return new PathFollower(nodeGrid, pathFinder, heuristic, pathSmoother);
    }

    public void printObstacleGrid() {
        for (int y = obstacleGrid[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < obstacleGrid.length; x++) {
                if (obstacleGrid[x][y]) System.out.print("#");
                else System.out.print("_");
            }
            System.out.println();
        }
    }
}
