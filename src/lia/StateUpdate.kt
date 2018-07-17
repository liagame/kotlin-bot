package lia

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON


@Serializable
data class StateUpdate(
        protected var uid: Long,
        protected var type: MessageType,
        var time: Float,
        var units: Array<Unit>
){
    companion object {
        fun parse(msg: String): StateUpdate {
            return JSON.parse(msg)
        }
    }
}

@Serializable
data class Unit(
        var id: Int,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientation: Float,
        var thrustSpeed: ThrustSpeed,
        var rotation: Rotation,
        var canShoot: Boolean,
        var nBullets: Int,
        var opponentsInView: Array<OpponentInView>,
        var opponentBulletsInView: Array<BulletInView>
)

@Serializable
data class OpponentInView(
        var id: Int,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientation: Float
)

@Serializable
data class BulletInView(
        var x: Float,
        var y: Float,
        var orientation: Float,
        var velocity: Float
)

enum class ThrustSpeed {
    NONE, FORWARD, BACKWARD
}

enum class Rotation {
    NONE, LEFT, RIGHT
}