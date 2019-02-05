package lia

import com.google.gson.GsonBuilder


data class GameState(
        var uid: Long,
        var type: MessageType,
        var time: Float,
        var numberOfOpponentUnits: Int,
        var resources: Int,
        var canSaySomething: Boolean,
        var units: Array<UnitData>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun parse(msg: String): GameState {
            return gson.fromJson(msg, GameState::class.java)
        }
    }
}

data class UnitData(
        var id: Int,
        var type: UnitType,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientationAngle: Float,
        var speed: Speed,
        var rotation: Rotation,
        var canShoot: Boolean,
        var nBullets: Int,
        var opponentsInView: Array<OpponentInView>,
        var opponentBulletsInView: Array<BulletInView>,
        var resourcesInView: Array<ResourceInView>,
        var navigationPath: Array<Point>
)

data class Point(
        var x: Float,
        var y: Float
)

data class OpponentInView(
        var id: Int,
        var type: UnitType,
        var health: Int,
        var x: Float,
        var y: Float,
        var orientationAngle: Float,
        var speed: Speed,
        var rotation: Rotation
)

data class BulletInView(
        var x: Float,
        var y: Float,
        var orientation: Float,
        var velocity: Float
)

data class ResourceInView(
        var x: Float,
        var y: Float
)

enum class Speed {
    NONE, FORWARD, BACKWARD
}

enum class Rotation {
    NONE, LEFT, RIGHT, SLOW_LEFT, SLOW_RIGHT
}

enum class UnitType {
    WARRIOR, WORKER
}