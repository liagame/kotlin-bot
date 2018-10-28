package lia

import com.google.gson.GsonBuilder

data class GameEnvironment(
        val uid: Long,
        val type: MessageType,
        val map: Array<Array<Boolean>>,
        val unitLocations: Array<UnitLocation>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun parse(msg: String): GameEnvironment {
            return gson.fromJson(msg, GameEnvironment::class.java)
        }
    }
}

data class UnitLocation(
        val id: Int,
        val x: Float,
        val y: Float,
        val orientation: Float
)