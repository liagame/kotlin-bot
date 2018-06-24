package helpers

import com.adamldavis.pathfinder.PathGrid
import com.badlogic.gdx.math.Vector2
import lia.*
import lia.HeadquartersStatus.*
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

    fun update(playerState: Player, hq: Headquarters, api: Api, stateUpdate: StateUpdate) {
        updatePriorities(playerState, hq, stateUpdate)

        when (state) {
            MyPlayer.State.NONE -> none(playerState, api)
            MyPlayer.State.GO_TO_HQ -> goToHq(playerState, hq, api)
            MyPlayer.State.KILL_OPPONENT -> killOpponent(playerState, hq, api)
            MyPlayer.State.LOST -> lost(playerState, api)
        }

        lastState = playerState
    }

    private fun none(playerState: Player, api: Api) {
        if (turningLeft) {
            api.setRotationSpeed(playerState.id, Rotation.LEFT)
        } else {
            api.setRotationSpeed(playerState.id, Rotation.RIGHT)
        }
    }


    private fun updatePriorities(playerState: Player, hq: Headquarters, stateUpdate: StateUpdate) {
        if (isDead) {
            state = State.NONE
            isDead = false
        }

        if (playerState.opponentsInView.size > 0) {
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
                    if (playerState.opponentsInView.size == 0) {
                        state = State.NONE
                    }
                }
                MyPlayer.State.LOST -> TODO()
            }
        }
    }

    private fun lost(playerState: Player, api: Api) {
        api.setThrustSpeed(playerState.id, ThrustSpeed.FORWARD)
        api.setRotationSpeed(playerState.id, Rotation.RIGHT)
        state = State.NONE
    }

    private fun goToHq(playerState: Player, hq: Headquarters, api: Api) {
        if (hqNavigator.follow(playerState, api)) {
            api.setThrustSpeed(playerState.id, ThrustSpeed.NONE)
            state = State.NONE
        }
    }

    private fun killOpponent(playerState: Player, hq: Headquarters, api: Api) {
        val p1 = Vector2(playerState.x, playerState.y)
        val p2 = Vector2(playerState.opponentsInView[0].x, playerState.opponentsInView[0].y)

        rotateToAngle(playerState, p1, p2, api)

        if (p1.dst(p2) < 4) {
            api.setThrustSpeed(playerState.id, ThrustSpeed.NONE)
        } else if (hq.status != DEFEND) {
            api.setThrustSpeed(playerState.id, ThrustSpeed.FORWARD)
        }

        if (playerState.weaponLoaded) {
            api.shoot(id)
        }
    }

}

fun Vector2.clone(): Vector2 {
    return Vector2(x, y)
}