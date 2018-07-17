package lia

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON


@Serializable
data class MapData(
        protected val uid: Long,
        protected val type: MessageType,
        val width: Float,
        val height: Float,
        val obstacles: Array<Obstacle>,
        val unitLocations: Array<UnitLocation>
) {
    companion object {
        fun parse(msg: String): MapData {
            return JSON.parse(msg)
        }
    }
}

@Serializable
data class Obstacle(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float
)

@Serializable
data class UnitLocation(
        val id: Int,
        val x: Float,
        val y: Float,
        val orientation: Float
)