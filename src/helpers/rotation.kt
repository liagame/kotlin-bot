package helpers

import com.badlogic.gdx.math.Vector2
import lia.Api
import lia.Player
import lia.Rotation
import lia.ThrustSpeed

fun rotateToAngle(playerState: Player, p1: Vector2, p2: Vector2, api: Api): Boolean {

    val playerAngle = Math.toDegrees(playerState.orientation.toDouble()) % 360

    val directionVector = Vector2().sub(p1.x, p1.y).add(p2.x, p2.y)
    val directionAngle = directionVector.angle()

    var angle = (playerAngle - directionAngle) % 360
    angle = when {
        angle > 180f -> angle - 360f
        angle < -180f -> angle + 360f
        else -> angle
    }

    if (kotlin.math.abs(angle) < 15) {
        api.setRotationSpeed(playerState.id, Rotation.NONE)
        return true
    }

    when {
        angle < 0f -> api.setRotationSpeed(playerState.id, Rotation.LEFT)
        else -> api.setRotationSpeed(playerState.id, Rotation.RIGHT)
    }
    api.setThrustSpeed(playerState.id, ThrustSpeed.NONE)

    return false
}