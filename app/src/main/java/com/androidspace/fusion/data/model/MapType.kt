package com.androidspace.fusion.ui.map.data

enum class MapType(val type: Int) {
    STANDART(1),
    SATTELITE(2);
    companion object {
        private val mapOrder = MapType.values().associateBy(MapType::type)
        infix fun from(order: Int): MapType = mapOrder[order] ?: STANDART
    }
}