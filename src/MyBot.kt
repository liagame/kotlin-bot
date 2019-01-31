import lia.*

/**
 * Here is where you control your bot. Initial implementation keeps sending units
 * to random locations on the map and makes them shoot when they see opponents.
 */
class MyBot : Bot {

    // Here we store the map as a 2D array of booleans. If map[x][y] equals True that
    // means that at (x,y) there is an obstacle. x=0, y=0 points to bottom left corner.
    lateinit var map: Array<Array<Boolean>>

    // In this method we receive the basic information about the game environment.
    // - GameEnvironment reference: https://docs.liagame.com/api/#gameenvironment
    @Synchronized override fun processGameEnvironment(gameEnvironment: GameEnvironment) {

        // We store the game map so that we can use it later.
        map = gameEnvironment.map
    }

    // This is the main method where you control your bot. 10 times per game second it receives
    // current game state. Use Api object to call actions on your units.
    // - GameState reference: https://docs.liagame.com/api/#gamestate
    // - Api reference:       https://docs.liagame.com/api/#api-object
    @Synchronized override fun processGameState(gameState: GameState, api: Api) {

        // We iterate through all of our units that are still alive.
        for (unit in gameState.units) {

            // If the unit is not going anywhere, we choose a new valid point on the
            // map and send the unit there.
            if (unit.navigationPath.isEmpty()) {

                var x: Int
                var y: Int

                // Generate new x and y until you get a position on the map where there
                // is no obstacle.
                while (true) {
                    x = (Math.random() * map.size).toInt()
                    y = (Math.random() * map[0].size).toInt()
                    // False means that on (x,y) there is no obstacle.
                    if (!map[x][y]) {
                        break
                    }
                }

                // Make the unit go to the chosen x and y. Last parameter is a boolean that
                // if set to true tells the unit to move backwards.
                api.navigationStart(unit.id, x.toFloat(), y.toFloat(), false)
            }

            // If the unit sees an opponent then make it shoot.
            if (unit.opponentsInView.isNotEmpty()) {
                api.shoot(unit.id)

                // Don't forget to make your unit brag. :)
                api.saySomething(unit.id, "I see you!")
            }
        }
    }

    // Connects your bot to Lia game engine, don't change it.
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
