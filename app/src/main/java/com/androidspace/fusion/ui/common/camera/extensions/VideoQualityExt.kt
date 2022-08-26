package com.androidspace.fusion.ui.common.camera.extensions

import androidx.camera.core.AspectRatio
import androidx.camera.video.Quality

fun Quality.getAspectRatio(quality: Quality): Int {
    return when {
        arrayOf(Quality.UHD, Quality.FHD, Quality.HD)
            .contains(quality)   -> AspectRatio.RATIO_16_9
        (quality ==  Quality.SD) -> AspectRatio.RATIO_4_3
        else -> throw UnsupportedOperationException()
    }
}

fun Quality.getAspectRatioString(quality: Quality, portraitMode:Boolean) :String {
    val hdQualities = arrayOf(Quality.UHD, Quality.FHD, Quality.HD)
    val ratio =
        when {
            hdQualities.contains(quality) -> Pair(16, 9)
            quality == Quality.SD         -> Pair(4, 3)
            else -> throw UnsupportedOperationException()
        }

    return if (portraitMode) "V,${ratio.second}:${ratio.first}"
           else "H,${ratio.first}:${ratio.second}"
}

fun Quality.getNameString() :String {
    return when (this) {
        Quality.UHD -> "QUALITY_UHD(2160p)"
        Quality.FHD -> "QUALITY_FHD(1080p)"
        Quality.HD -> "QUALITY_HD(720p)"
        Quality.SD -> "QUALITY_SD(480p)"
        else -> throw IllegalArgumentException("Quality $this is NOT supported")
    }
}
fun Quality.getQualityObject(name:String) : Quality {
    return when (name) {
        Quality.UHD.getNameString() -> Quality.UHD
        Quality.FHD.getNameString() -> Quality.FHD
        Quality.HD.getNameString()  -> Quality.HD
        Quality.SD.getNameString()  -> Quality.SD
        else -> throw IllegalArgumentException("Quality string $name is NOT supported")
    }
}
