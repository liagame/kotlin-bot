import lia.*

/**
 * Place to write the logic for your bots.
 */
class MyBot : Callable {

    /** Called only once when the game is initialized. */
    @Synchronized override fun process(mapData: MapData) {
        println(mapData)
        // Write some code to handle mapData.
    }

    /** Repeatedly called 10 times per second from game engine
     *  with game state updates. */
    @Synchronized override fun process(stateUpdate: StateUpdate, api: Api) {
        println(stateUpdate)
        // Write some code to handle stateUpdate every frame.
        // Use api to send responses back to the game engine.
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
