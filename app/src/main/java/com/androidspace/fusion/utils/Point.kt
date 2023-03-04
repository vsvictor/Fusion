package com.androidspace.fusion.utils

import com.arcgismaps.geometry.Point


fun Point.asString(): String{
    return ""+this.y+":"+this.x
}