package helpers

import com.adamldavis.pathfinder.PathGrid
import com.badlogic.gdx.math.Vector2
import lia.AiApiMessages.*
import lia.AiApiMessages.HQStatus.Enum.*
import lia.Response
import java.util.*

class MyPlayer(val grid: PathGrid,
               val players: HashMap<Int, MyPlayer>,
               val id: Int,
               val timer: Timer) {

    enum class State { NONE, GO_TO_HQ, KILL_OPPONENT, LOST }

    var state = State.NONE
    private lateinit var lastState: Player
    private lateinit var hqNavigator: PointNavigator
    private val turningLeft = players.size % 2 == 0

    var isDead = false

    fun update(playerState: Player, hq: Headquarters, response: Response, stateUpdate: StateUpdate) {
        updatePriorities(playerState, hq, stateUpdate)

        when (state) {
            MyPlayer.State.NONE -> none(playerState, response)
            MyPlayer.State.GO_TO_HQ -> goToHq(playerState, hq, response)
            MyPlayer.State.KILL_OPPONENT -> killOpponent(playerState, hq, response)
            MyPlayer.State.LOST -> lost(playerState, response)
        }

        lastState = playerState
    }

    private fun none(playerState: Player, response: Response) {
        if (turningLeft) {
            response.setRotationSpeed(playerState.id, Rotation.Enum.LEFT)
        } else {
            response.setRotationSpeed(playerState.id, Rotation.Enum.RIGHT)
        }
    }


    private fun updatePriorities(playerState: Player, hq: Headquarters, stateUpdate: StateUpdate) {
        if (isDead) {
            state = State.NONE
            isDead = false
        }

        if (playerState.opponentsInViewCount > 0) {
            state = State.KILL_OPPONENT
        }
        else {
            when (state) {
                MyPlayer.State.NONE -> {
                    if (hq.status == CAPTURE || hq.status == DESTROY) {
                        val v1 = Vector2(playerState.x, playerState.y)
                        val v2 = Vector2(hq.location.x, hq.location.y)

                        state = State.GO_TO_HQ
                        hqNavigator = PointNavigator(grid, v1, v2)

                        if (v1.dst(v2) > 15) {
                            val found = hqNavigator.setup2()
                            if (!found) {
                                state = State.LOST
                            }
                        } else {
                            val found = hqNavigator.setup()
                            if (!found) {
                                state = State.LOST
                            }
                        }
                    }
                }
                MyPlayer.State.GO_TO_HQ -> {
                    if (hq.status == INACTIVE) {
                        state = State.NONE
                    }
                }
                MyPlayer.State.KILL_OPPONENT -> {
                    if (playerState.opponentsInViewCount == 0) {
                        state = State.NONE
                    }
                }
            }
        }
    }

    private fun lost(playerState: Player, response: Response) {
        response.setThrustSpeed(playerState.id, ThrustSpeed.Enum.FORWARD)
        response.setRotationSpeed(playerState.id, Rotation.Enum.RIGHT)
        state = State.NONE
    }

    private fun goToHq(playerState: Player, hq: Headquarters, response: Response) {
        if (hqNavigator.follow(playerState, response)) {
            response.setThrustSpeed(playerState.id, ThrustSpeed.Enum.NONE)
            state = State.NONE
        }
    }

    private fun killOpponent(playerState: Player, hq: Headquarters, response: Response) {
        val p1 = Vector2(playerState.x, playerState.y)
        val p2 =  Vector2(playerState.opponentsInViewList[0].x, playerState.opponentsInViewList[0].y)

        rotateToAngle(playerState, p1, p2, response)

        if (p1.dst(p2) < 4) {
            response.setThrustSpeed(playerState.id, ThrustSpeed.Enum.NONE)
        } else if (hq.status != DEFEND) {
            response.setThrustSpeed(playerState.id, ThrustSpeed.Enum.FORWARD)
        }

        if (playerState.weaponLoaded) {
            response.shoot(id)
        }
    }

}

fun Vector2.clone(): Vector2 {
    return Vector2(x, y)
}