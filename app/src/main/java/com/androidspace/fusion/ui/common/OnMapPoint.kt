package com.androidspace.fusion.ui.common

import com.esri.arcgisruntime.geometry.Point

interface OnMapPoint {
    fun onMapPoint(point: Point)
}