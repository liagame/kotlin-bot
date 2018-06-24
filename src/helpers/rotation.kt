package helpers

import com.badlogic.gdx.math.Vector2
import lia.AiApiMessages
import lia.Response

fun rotateToAngle(playerState: AiApiMessages.Player, p1: Vector2, p2: Vector2, response: Response): Boolean {

    val playerAngle = Math.toDegrees(playerState.orientation.toDouble()) % 360

    val directionVector = Vector2().sub(p1.x, p1.y).add(p2.x, p2.y)
    val directionAngle = directionVector.angle()

    var angle = (playerAngle - directionAngle) % 360
    angle = when {
        angle > 180f -> angle - 360f
        angle < -180f -> angle + 360f
        else -> angle
    }

    if (kotlin.math.abs(angle) < 5) {
        response.setRotationSpeed(playerState.id, AiApiMessages.Rotation.Enum.NONE)
        return true
    }

    when {
        angle < 0f -> response.setRotationSpeed(playerState.id, AiApiMessages.Rotation.Enum.LEFT)
        else -> response.setRotationSpeed(playerState.id, AiApiMessages.Rotation.Enum.RIGHT)
    }
    response.setThrustSpeed(playerState.id, AiApiMessages.ThrustSpeed.Enum.NONE)

    return false
}