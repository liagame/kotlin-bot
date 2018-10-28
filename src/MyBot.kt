import lia.*

/**
 * This is your bot. Implement it with your logic.
 *
 * In this example bot keeps sending units to random locations on map
 * and is able to shoot when it sees an opponent, it does not aim at
 * him though!
 */
class MyBot : Bot {

    // Here we will store a map that we will receive from game engine at the
    // start of the game.
    // x=0, y=0 presents a bottom left corner of the map, and the value on
    // map is true if there is an obstacle
    lateinit var map: Array<BooleanArray>

    // Called only once when the game is initialized holding data about the map.
    @Synchronized override fun processGameEnvironment(gameEnvironment: GameEnvironment) {
        // We store the map that game uses in our variable.
        // https://docs.liagame.com/api/#mapdata for the data you receive here
        map = gameEnvironment.map
    }

    // Repeatedly called 10 times per second from game engine with
    // game state updates. Here is where all the things happen.
    @Synchronized override fun processGameState(gameState: GameState, api: Api) {

        // Iterate through the data of all of your units
        for (unit in gameState.units) {

            // navigationPath is a field of your unit that shows the path
            // on which the unit is currently using if you have sent it
            // to a location using a api.navigationStart method.
            // If it is empty it means there is no path set. In this case
            // we choose a new destination and send the unit there.
            if (unit.navigationPath.isEmpty()) {

                // Generate a point on map
                val (x, y) = randomValidPointOnMap()

                // Send the unit to chosen x and y
                api.navigationStart(unit.id, x.toFloat(), y.toFloat())
            }

            // If the unit has an opponent in it's viewing area then
            // make it shoot
            if (unit.opponentsInView.isNotEmpty()) {
                api.shoot(unit.id)
            }
        }
    }

    // Finds a random point on the map that is not on the obstacle
    fun randomValidPointOnMap(): Pair<Int, Int> {
        val minDistanceToMapEdge = 2

        // Generate new x and y until you get one that is not placed on an obstacle
        while (true) {
            val x = (Math.random() * (map.size - 2 * minDistanceToMapEdge)).toInt() + minDistanceToMapEdge
            val y = (Math.random() * (map[0].size - 2 * minDistanceToMapEdge)).toInt() + minDistanceToMapEdge
            // If true it means that at (x,y) in map there is an obstacle
            if (!map[x][y]) {
                return Pair(x, y)
            }
        }
    }
}

// This connects your bot to Lia game engine
fun main(args: Array<String>) {
    NetworkingClient.connectNew(args, MyBot())
}
