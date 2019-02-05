import lia.*

/**
 * Initial implementation keeps picking random locations on the map
 * and sending units there. Worker units collect resources if they
 * see them while warrior units shoot if they see opponents.
 */
class MyBot : Bot {

    // This method is called 10 times per game second and holds current
    // game state. Use Api object to call actions on your units.
    // - GameState reference: https://docs.liagame.com/api/#gamestate
    // - Api reference:       https://docs.liagame.com/api/#api-object
    @Synchronized override fun update(state: GameState, api: Api) {

        // If you have enough resources to spawn a new warrior unit then spawn it.
        if (state.resources >= Constants.WARRIOR_PRICE) {
            api.spawnUnit(UnitType.WARRIOR)
        }

        // We iterate through all of our units that are still alive.
        for (unit in state.units) {

            // If the unit is not going anywhere, we send it
            // to a random valid location on the map.
            if (unit.navigationPath.isEmpty()) {

                // Generate new x and y until you get a position on the map
                // where there is no obstacle.
                while (true) {
                    val x = (Math.random() * Constants.MAP.size).toInt()
                    val y = (Math.random() * Constants.MAP[0].size).toInt()

                    // If map[x][y] equals false it means that at (x,y) there is no obstacle.
                    if (!Constants.MAP[x][y]) {
                        // Send the unit to (x, y)
                        api.navigationStart(unit.id, x.toFloat(), y.toFloat())
                        break
                    }
                }
            }

            // If the unit is a worker and it sees at least one resource
            // then make it go to the first resource to collect it.
            if (unit.type == UnitType.WORKER && unit.resourcesInView.isNotEmpty()) {
                val resource = unit.resourcesInView[0]
                api.navigationStart(unit.id, resource.x, resource.y)
            }

            // If the unit is a warrior and it sees an opponent then make it shoot.
            if (unit.type == UnitType.WARRIOR && unit.opponentsInView.isNotEmpty()) {
                api.shoot(unit.id)
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
