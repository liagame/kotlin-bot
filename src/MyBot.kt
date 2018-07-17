import lia.*

/**
 * Place to write the logic for your bots.
 */
class MyBot : Callable {


    @Synchronized override fun process(mapData: MapData) {
        println(mapData)
    }
    
    @Synchronized override fun process(stateUpdate: StateUpdate, api: Api) {
        println(stateUpdate)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
        }
    }
}
