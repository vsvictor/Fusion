package com.androidspace.fusion.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.androidspace.fusion.domain.BaseUseCase
import com.androidspace.fusion.utils.save
import java.io.File

class UseCaseResizeBitmap(private val context: Context): BaseUseCase<File, UseCaseResizeBitmap.Param>() {

    override suspend fun onLaunch(params: UseCaseResizeBitmap.Param): File? = resize(params.source, params.newWidth, params.newHeight, params.fileName)

    private fun resize(source: Bitmap, width: Int, height: Int, fileName: String): File{
        val resized = Bitmap.createScaledBitmap(source, width, height, false)
        return resized.save(context, fileName)
    }
    private fun scale(source: Bitmap, width: Int, height: Int, fileName: String): Bitmap{
        val coef = (width as Float) / (source.width as Float)
        val newHeight = (source.height * coef).toInt()
        return Bitmap.createScaledBitmap(source, width, newHeight, false)
    }
    class Param(val source: Bitmap, val newWidth: Int, val newHeight: Int, val fileName: String)

}