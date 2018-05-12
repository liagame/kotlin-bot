import lia.AiApiMessages.*
import lia.Callable
import lia.NetworkingClient
import lia.Response
import java.util.Random

/**
 * Place to write the logic for your bots.
 */
class MyBot : Callable {

    private var counter: Long = 0
    private val random = Random(15)

    /** Called only once when the game is initialized.  */
    @Synchronized override fun process(mapData: MapData) {
        System.out.println(mapData)
    }

    /** Repeatedly called from game engine with game status updates.   */
    @Synchronized override fun process(stateUpdate: StateUpdate, response: Response) {
        /*
        * This is a sample code that moves players randomly and shoots when the
        * opponent is in sight.
        **/

        val players = stateUpdate.getPlayersList()

        for (player in players) {
            val id = player.getId()

            // Rotation and thrust speed
            if (counter % 10 == 0L || counter % 11 == 0L) {

                // Handle rotation
                var rand = random.nextFloat()
                if (rand < 0.4)
                    response.setRotationSpeed(id, Rotation.Enum.LEFT)
                else if (rand < 0.8)
                    response.setRotationSpeed(id, Rotation.Enum.RIGHT)
                else
                    response.setRotationSpeed(id, Rotation.Enum.NONE)

                // Handle thrust speed
                rand = random.nextFloat()
                if (rand < 0.9) {
                    response.setThrustSpeed(id, ThrustSpeed.Enum.FULL_SPEED)
                } else {
                    response.setThrustSpeed(id, ThrustSpeed.Enum.NONE)
                }
            }
            counter++

            // Shoot
            if (player.getWeaponLoaded() && player.getOpponentsInViewCount() > 0)
                response.shoot(id)
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            NetworkingClient.connectNew(args, MyBot())
            //NetworkingClient.connectNew(args, new MyBot());
        }
    }
}
