package com.androidspace.fusion.ui.common.camera.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.androidspace.fusion.data.model.camera.ImageData
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

abstract class ImageTarget(val image: ImageData): Target {
    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        bitmap?.let {
            onLoaded(image, it)
        }
    }
    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    abstract fun onLoaded(im: ImageData, b: Bitmap)
}