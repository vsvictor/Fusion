package com.androidspace.fusion.domain

import android.graphics.Bitmap

class UseCaseScaleBitmap: BaseUseCase<Bitmap, UseCaseScaleBitmap.Param>() {

    override suspend fun onLaunch(params: UseCaseScaleBitmap.Param): Bitmap? = scale(params.source, params.coef, params.rotate)

    private fun scale(source: Bitmap, coef: Float, rotate: Int): Bitmap?{
        val newWidth = (source.width * coef).toInt()
        val newHeight = (source.height * coef).toInt()
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, false)
    }
    class Param(val source: Bitmap, val coef: Float, val rotate: Int = 0)

}