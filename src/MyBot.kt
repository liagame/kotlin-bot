import com.adamldavis.pathfinder.PathGrid
import com.adamldavis.pathfinder.SimplePathGrid
import lia.AiApiMessages.*
import lia.Callable
import lia.NetworkingClient
import lia.Response
import helpers.MyPlayer
import helpers.Timer

/**
 * Place to write the logic for your bots.
 */
class MyBot : Callable {

    val PLAYER_RADIUS = 1

    lateinit var grid: PathGrid

    private val players = HashMap<Int, MyPlayer>()
    private var timer = Timer()

    @Synchronized override fun process(mapData: MapData) {

        grid = SimplePathGrid(120, 67)

        for (obstacle in mapData.obstaclesList) {
            // Width
            val xStart = obstacle.x.toInt() - PLAYER_RADIUS
            val xEnd = (obstacle.x + obstacle.width).toInt() + PLAYER_RADIUS
            for (x in xStart until xEnd) {
                // height
                val yStart = obstacle.y.toInt() - PLAYER_RADIUS
                val yEnd = (obstacle.y + obstacle.height).toInt() + PLAYER_RADIUS
                for (y in yStart until yEnd) {
                    if (x < grid.width && y < grid.height && x >= 0 && y >= 0) {
                        grid.setGrid(x, y, true)
                    }
                }
            }
        }
    }

    @Synchronized override fun process(stateUpdate: StateUpdate, response: Response) {
        timer.time += stateUpdate.time

        // Initialize players
        if (players.size == 0) {
            for (player in stateUpdate.playersList) {
                players[player.id] = MyPlayer(grid, players, player.id, timer)
            }
        }

        // Tell bots they are dead
        for ((_, player) in players) {
            var found = false
            for (playerData in stateUpdate.playersList) {
                if (player.id == playerData.id) {
                    found = true
                    break
                }
            }
            if (!found) {
                player.isDead = true
            }
        }

        // Process player
        for (playerData in stateUpdate.playersList) {
            val player = players[playerData.id]!!
            player.update(playerData, stateUpdate.headquarters, response, stateUpdate)
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
