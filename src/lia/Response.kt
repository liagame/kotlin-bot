package lia

import com.google.gson.GsonBuilder


data class Response(
        val uid: Long,
        val type: MessageType,
        val speedEvents: Array<SpeedEvent>,
        val rotationEvents: Array<RotationEvent>,
        val shootEvents: Array<ShootEvent>,
        val navigationStartEvents: Array<NavigationStartEvent>,
        val navigationStopEvents: Array<NavigationStopEvent>,
        val saySomethingEvents: Array<SaySomethingEvent>,
        val spawnUnitEvents: Array<SpawnUnitEvent>

){
    companion object {
        val gson = GsonBuilder().create()!!

        fun toJson(response: Response): String {
            return gson.toJson(response)
        }
    }
}

data class SpeedEvent(
        val index: Int,
        val unitId: Int,
        val speed: Speed
)

data class RotationEvent(
        val index: Int,
        val unitId: Int,
        val rotation: Rotation
)

data class ShootEvent(
        val index: Int,
        val unitId: Int
)

data class NavigationStartEvent(
        val index: Int,
        val unitId: Int,
        val x: Float,
        val y: Float,
        val moveBackwards: Boolean
)

data class NavigationStopEvent(
        val index: Int,
        val unitId: Int
)

data class SaySomethingEvent(
        val index: Int,
        val unitId: Int,
        val text: String
)

data class SpawnUnitEvent(
        val index: Int,
        val type: UnitType
)