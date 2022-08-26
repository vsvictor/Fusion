package com.androidspace.fusion.utils

import com.esri.arcgisruntime.geometry.Point

fun Point.asString(): String{
    return ""+this.y+":"+this.x
}