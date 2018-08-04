package lia

import com.google.gson.GsonBuilder


data class Response(
        val uid: Long,
        val type: MessageType,
        val thrustSpeedEvents: Array<ThrustSpeedEvent>,
        val rotationEvents: Array<RotationEvent>,
        val shootEvents: Array<ShootEvent>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun toJson(response: Response): String {
            return gson.toJson(response)
        }
    }
}

data class ThrustSpeedEvent(
        val type: EventType,
        val unitId: Int,
        val speed: ThrustSpeed
)

data class RotationEvent(
        val type: EventType,
        val unitId: Int,
        val rotation: Rotation
)

data class ShootEvent(
        val type: EventType,
        val unitId: Int
)

enum class EventType {
    SET_THRUST_SPEED, SET_ROTATION, SHOOT
}