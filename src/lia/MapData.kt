package lia

import com.google.gson.GsonBuilder

data class MapData(
        val uid: Long,
        val type: MessageType,
        val map: Array<BooleanArray>,
        val unitLocations: Array<UnitLocation>
){
    companion object {
        val gson = GsonBuilder().create()!!

        fun parse(msg: String): MapData {
            return gson.fromJson(msg, MapData::class.java)
        }
    }
}

data class UnitLocation(
        val id: Int,
        val x: Float,
        val y: Float,
        val orientation: Float
)