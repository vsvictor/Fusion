package com.androidspace.fusion.ui.common.camera.data

import android.graphics.Bitmap
import android.net.Uri

interface OnBitmapDestinatation {
    fun onDestinatation(bitmap: Bitmap)
    fun onDestinatation(bitmap: Bitmap, uri: Uri)
}