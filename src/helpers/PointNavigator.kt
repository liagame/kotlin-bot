package helpers

import com.adamldavis.pathfinder.AntPathFinder
import com.adamldavis.pathfinder.PathGrid
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import lia.*
import java.util.concurrent.LinkedBlockingQueue

class PointNavigator(val grid: PathGrid, val p1: Vector2, var p2: Vector2) {

    private var finder = AntPathFinder(80)
    private var path = LinkedBlockingQueue<Vector2>()

    fun setup(): Boolean {
        var x = p1.x
        var y = p1.y

        val moves = finder.findPath(grid, x.toInt(), y.toInt(),
                p2.x.toInt(), p2.y.toInt())

        if (moves != null) {
            val pathList = ArrayList<Vector2>()

            var count = 0

            for (move in moves) {
                when (move) {
                    0 -> y -= 1f
                    1 -> x += 1f
                    2 -> y += 1f
                    3 -> x -= 1f
                }
                if (count % 2 == 0)
                    pathList.add(Vector2(x, y))
                count++
            }
            if (count % 2 == 0) pathList.add(Vector2(x, y))

            // Filter path
            path.add(pathList[0])

            for (i in 0 until pathList.size - 2) {
                val first = pathList[i].clone()
                val second = pathList[i + 1].clone()
                val third = pathList[i + 2].clone()

                val a1 = second.clone().sub(first).angle()
                val a2 = third.sub(second).angle()

                if (a1 != a2) {
                    path.add(second)
                }
            }
            path.add(pathList[pathList.size - 1])
        } else {
            println("Should not happen!")
            return false
        }
        return true
    }

    fun setup2(): Boolean {
        var x = p1.x
        var y = p1.y


        var p3 = Vector2()
        var counter = 200
        while (true) {
            counter --
            if (counter == 0) break
            val x3 = MathUtils.random(10, 110)
            val y3 = MathUtils.random(10, 57)
            p3.set(x3.toFloat(), y3.toFloat())

            if (p3.dst(p2) * 1.3f < p3.dst(x, y) && p3.dst(p2) > 5) {
                if (p1.dst(p2) > p1.dst(p3) && grid.getGrid(x3, y3) == false) {
                    break
                }
            }
        }

        if (counter > 0)
            p2 = p3

        val moves = finder.findPath(grid, x.toInt(), y.toInt(),
                p2.x.toInt(), p2.y.toInt())

        if (moves != null) {
            val pathList = ArrayList<Vector2>()

            var count = 0

            for (move in moves) {
                when (move) {
                    0 -> y -= 1f
                    1 -> x += 1f
                    2 -> y += 1f
                    3 -> x -= 1f
                }
                if (count % 2 == 0)
                    pathList.add(Vector2(x, y))
                count++
            }
            if (count % 2 == 0) pathList.add(Vector2(x, y))

            // Filter path
            path.add(pathList[0])

            for (i in 0 until pathList.size - 2) {
                val first = pathList[i].clone()
                val second = pathList[i + 1].clone()
                val third = pathList[i + 2].clone()

                val a1 = second.clone().sub(first).angle()
                val a2 = third.sub(second).angle()

                if (a1 != a2) {
                    path.add(second)
                }
            }
            path.add(pathList[pathList.size - 1])
        } else {
            println("Should not happen!")
            return false
        }
        return true
    }

    fun getLastPoint() = path.last()


    fun follow(playerState: Player, api: Api): Boolean {
        val x = playerState.x
        val y = playerState.y
        val id = playerState.id

        val nextPoint = path.peek() ?: return true

        val rotated = rotateToAngle(playerState, Vector2(x, y), nextPoint, api)
        if (rotated) {
            if (kotlin.math.abs(nextPoint.x - x) < 2f &&
                    kotlin.math.abs(nextPoint.y - y) < 2f) {
                path.poll()
            }
            // move forward
            api.setThrustSpeed(id, ThrustSpeed.FORWARD)
        }

        return false
    }

}