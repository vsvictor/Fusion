package com.androidspace.fusion.ui.common.camera.data

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface OnVideoDestinatation {
    fun onDestinatation(thumb: Bitmap, uri: Uri)
}