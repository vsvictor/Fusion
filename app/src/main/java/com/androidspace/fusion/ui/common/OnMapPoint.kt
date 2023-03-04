package com.androidspace.fusion.ui.common

import com.arcgismaps.geometry.Point


interface OnMapPoint {
    fun onMapPoint(point: Point)
}