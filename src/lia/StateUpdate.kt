package lia

import com.google.gson.GsonBuilder


data class StateUpdate(
        var uid: Long,
        var type: MessageType,
        var time: Float,
        var units: Array<Unit>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun parse(msg: String): StateUpdate {
            return gson.fromJson(msg, StateUpdate::class.java)
        }
    }
}

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

data class OpponentInView(
        var id: Int,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientation: Float
)

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