package pathfinding;

import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import lia.Api;
import lia.Rotation;
import lia.ThrustSpeed;
import lia.UnitData;

public class PathFollower {
    // How close the unit needs to be to the destination so that
    // we determine that it arrived there
    private final float ALLOWED_DESTINATION_OFFSET = 1.5f;
    // Tells when the angle towards the destination is small
    // enough so that the unit can move slowly or move forward
    private final float ALLOWED_ANGLE_OFFSET_FAST_SPEED = 15f;
    private final float ALLOWED_ANGLE_OFFSET_SLOW_SPEED = 3f;

    private CustomGraphPath path = new CustomGraphPath();
    private int nextPointIndex = 0;

    private PathNode[][] nodeGrid;
    private IndexedAStarPathFinder<PathNode> pathFinder;
    private ManhattanDistance heuristic;
    private PathSmoother<PathNode, Vector2> pathSmoother;

    public PathFollower(PathNode[][] nodeGrid, IndexedAStarPathFinder<PathNode> pathFinder,
                        ManhattanDistance heuristic, PathSmoother<PathNode, Vector2> pathSmoother) {
        this.nodeGrid = nodeGrid;
        this.pathFinder = pathFinder;
        this.heuristic = heuristic;
        this.pathSmoother = pathSmoother;
    }

    public boolean isFollowingThePath() {
        return nextPointIndex < path.nodes.size;
    }

    /**
     * Find path from (x1, y1) to (x2, y2) on specified grid.
     * */
    public boolean findPath(int x1, int y1, int x2, int y2) {
        path.clear();
        nextPointIndex = 0;

        PathNode startNode = nodeGrid[x1][y1];
        PathNode endNode = nodeGrid[x2][y2];

        if (startNode == null || endNode == null) {
            System.out.println("unit was pushed to close to the obstacle and can't find way out");
            return false;
        }

        boolean found = pathFinder.searchNodePath(startNode, endNode, heuristic, path);

        if (found) optimizePathPoints();

        return found;
    }

    private void optimizePathPoints() {
        pathSmoother.smoothPath(path);
    }

    /**
     * Follows the previously chosen path and writes
     * the needed step to api.
     * @return true if the unit is at the last point.
     * */
    public boolean follow(UnitData unitData, Api api) {
        // If there is still a point in our path that we need to visit
        // then move towards it
        if (nextPointIndex < path.nodes.size) {
            PathNode nextNode = path.get(nextPointIndex);

            boolean arrived = moveToDestination(unitData, nextNode, api);

            // If we have arrived to the nextPoint, choose new nextPoint
            if (arrived) {
                nextPointIndex++;
            }

            return nextPointIndex == path.nodes.size;
        }

        return true;
    }

    /** Moves to destination.
     * @return  true if the unit is at destination else false */
    public boolean moveToDestination(UnitData unitData, PathNode nextNode, Api api) {
        // If we are already at the destination then stop the unit
        // (Method defined in point 1.)
        if (atDestination(unitData, nextNode.x, nextNode.y)) {
            api.setThrustSpeed(unitData.getId(), ThrustSpeed.NONE);
            return true; // We have arrived
        }

        // Calculate the angle (Method defined in point 2.)
        float angle = angleBetweenUnitAndPoint(unitData, nextNode.x, nextNode.y);

        // Find if the angle is small enough so we can move forward
        boolean moveForward = Math.abs(angle) < ALLOWED_ANGLE_OFFSET_SLOW_SPEED;

        if (moveForward) {
            // Stop rotating and move forward
            if (unitData.getRotation() != Rotation.NONE) {
                api.setRotationSpeed(unitData.getId(), Rotation.NONE);
            }
            if (unitData.getThrustSpeed() != ThrustSpeed.FORWARD) {
                api.setThrustSpeed(unitData.getId(), ThrustSpeed.FORWARD);
            }
        }
        else {
            // Stop moving forward
            if (unitData.getThrustSpeed() != ThrustSpeed.NONE) {
                api.setThrustSpeed(unitData.getId(), ThrustSpeed.NONE);
            }

            // Rotate closer to the wanted angle
            boolean slowSpeed = Math.abs(angle) < ALLOWED_ANGLE_OFFSET_FAST_SPEED;
            if (slowSpeed) {
                if (angle < 0) {
                    if (unitData.getRotation() != Rotation.SLOW_RIGHT) {
                        api.setRotationSpeed(unitData.getId(), Rotation.SLOW_RIGHT);
                    }
                } else {
                    if (unitData.getRotation() != Rotation.SLOW_LEFT) {
                        api.setRotationSpeed(unitData.getId(), Rotation.SLOW_LEFT);
                    }
                }
            }
            else {
                if (angle < 0) {
                    if (unitData.getRotation() != Rotation.RIGHT) {
                        api.setRotationSpeed(unitData.getId(), Rotation.RIGHT);
                    }
                } else {
                    if (unitData.getRotation() != Rotation.LEFT) {
                        api.setRotationSpeed(unitData.getId(), Rotation.LEFT);
                    }
                }
            }
        }
        return false; // We haven't arrived yet
    }

    public boolean atDestination(UnitData unitData, int destX, int destY) {
        return Math.abs(destX - unitData.getX()) < ALLOWED_DESTINATION_OFFSET &&
                Math.abs(destY - unitData.getY()) < ALLOWED_DESTINATION_OFFSET;
    }

    /** Calculate the angle between the unit orientation and the point x, y */
    public float angleBetweenUnitAndPoint(UnitData unitData, int x, int y) {
        // Create a vector from the unit to the destination by subtracting
        // base unit location vector from base destination vector
        Vector2 unitToDest = new Vector2(x, y);
        unitToDest.sub(unitData.getX(), unitData.getY());

        // Get the angle between unitToDest and unit orientation vectors
        // (Vector2 has a function to calculate angle, link to implementation below)
        float angle = unitToDest.angle() - unitData.getOrientation();

        // Angles can always be represented with a positive or negative value that
        // is smaller than 180°. Here this optimization helps us so that later we
        // know better to which direction we should rotate.
        if (angle > 180) angle -= 360;
        else if (angle < -180) angle += 360;

        return angle;
    }

    public void printPath() {
        for (int y = nodeGrid[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < nodeGrid.length; x++) {
                // If [x][y] is in path then print O
                boolean inPath = false;
                for (PathNode node : path.nodes) {
                    if (node.x == x && node.y == y) {
                        inPath = true;
                        break;
                    }
                }
                if (inPath) {
                    System.out.print("O");
                }
                else {
                    if (nodeGrid[x][y] == null) System.out.print("#");
                    else System.out.print("_");
                }

            }
            System.out.println();
        }
    }
}
