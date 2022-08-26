package com.androidspace.fusion.ui.common

import com.esri.arcgisruntime.geometry.Point

interface OnMapLongClick {
    fun onMapLongClick(point: Point)
}