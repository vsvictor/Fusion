package com.androidspace.fusion.utils

    fun Long.toTime(): String{
        var res = ""
        val secs = (this.toFloat() / 1000f).toInt()
        val min = (secs.toFloat() / 60f).toInt()
        val rSec = (secs - (min*60)).toInt()
        val rHours = (min.toFloat() / 60f).toInt()
        val rMin = (min - (rHours*60)).toInt()
        if(rHours > 0) res += (String.format("%02d", rHours)+":")
        res += (String.format("%02d", rMin)+":")
        res += (String.format("%02d", rSec))
        return res
    }