package lia

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON

@Serializable
data class Response(
        protected val uid: Long,
        protected val type: MessageType,
        val thrustSpeedEvents: Array<ThrustSpeedEvent>,
        val rotationEvents: Array<RotationEvent>,
        val shootEvents: Array<ShootEvent>
){
    companion object {
        fun toJson(response: Response): String {
            return JSON.stringify(response)
        }
    }
}

@Serializable
data class ThrustSpeedEvent(
        val type: EventType,
        val unitId: Int,
        val speed: ThrustSpeed
)

@Serializable
data class RotationEvent(
        val type: EventType,
        val unitId: Int,
        val rotation: Rotation
)

@Serializable
data class ShootEvent(
        val type: EventType,
        val unitId: Int
)

enum class EventType {
    SET_THRUST_SPEED, SET_ROTATION, SHOOT
}