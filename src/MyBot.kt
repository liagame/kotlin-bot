import lia.*

/**
 * This is your bot that will play the game for you.
 *
 * Implementation below keeps sending units to random locations on the map
 * and makes them shoot when they see opponents.
 */
class MyBot : Bot {

    // Here we will store the map that the game will be played on. Map has values set
    // to True where there is an obstacle. You can access (x,y) coordinate by calling
    // map[x][y] and x=0, y=0 is placed at the bottom left corner.
    lateinit var map: Array<Array<Boolean>>

    // This method is called only once at the beginning of the game and holds the basic
    // information about the game environment.
    // - GameEnvironment reference: TODO link
    @Synchronized override fun processGameEnvironment(gameEnvironment: GameEnvironment) {

        // Here we store the map that is used in current game. We will use it later.
        map = gameEnvironment.map
    }

    // This method is the main part of your bot. It is called 10 times per game seconds and
    // it holds game's current state and the Api object which you use to control your units.
    // - GameState reference: TODO link
    // - Api reference:       TODO link
    @Synchronized override fun processGameState(gameState: GameState, api: Api) {

        // First we iterate through all of our units that are still alive
        for (unit in gameState.units) {

            // With api.navigationStart(...) method you can send your units to go to a
            // specified point on the map all by themselves. If they are currently going
            // somewhere then their path is stored in navigationPath field. If the field
            // is empty the unit is not going anywhere automatically. Here, if the unit
            // is not going anywhere we choose a new location and send it there.
            if (unit.navigationPath.isEmpty()) {

                // Generate a point on the map where there is no obstacle.
                val (x, y) = randomValidPointOnMap()

                // Make the unit go to the chosen x and y.
                api.navigationStart(unit.id, x.toFloat(), y.toFloat())
            }

            // If the unit sees an opponent then make it shoot.
            if (unit.opponentsInView.isNotEmpty()) {
                api.shoot(unit.id)
            }
        }
    }

    // Finds a random point on the map where there is no obstacle.
    fun randomValidPointOnMap(): Pair<Int, Int> {
        // Generate new x and y until you get a position that is not placed on an obstacle.
        while (true) {
            val x = (Math.random()* map.size).toInt()
            val y = (Math.random() * map[0].size).toInt()
            // False means that on (x,y) there is no obstacle.
            if (!map[x][y]) {
                return Pair(x, y)
            }
        }
    }
}

// This connects your bot to Lia game engine, don't change it
fun main(args: Array<String>) {
    NetworkingClient.connectNew(args, MyBot())
}
