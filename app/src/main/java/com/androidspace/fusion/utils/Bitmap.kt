package com.androidspace.fusion.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


private const val IMAGE_DIR = "images"

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees.toFloat())
    val scaledBitmap = Bitmap.createScaledBitmap(this, this.width, this.height, true)
    return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
}
fun Bitmap.save(context: Context, imageName: String, quality: Int = 100): File {
    val contextWrapper = ContextWrapper(context)
    val directory = contextWrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
    val path = File(directory, "$imageName.png")

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(path)
        this.compress(Bitmap.CompressFormat.PNG, quality, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fileOutputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return path
}
fun Bitmap.saveJPG(context: Context, imageName: String, quality: Int = 100): File {
    val contextWrapper = ContextWrapper(context)
    val directory = contextWrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
    val path = File(directory, "$imageName.jpg")

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(path)
        this.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fileOutputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return path
}
fun Bitmap.asDrawable(context: Context): Drawable{
    return BitmapDrawable(context.resources, this)
}

fun loadBitmap(context: Context, imageName: String, isAbsolutePath: Boolean): Bitmap? {
    return try {
/*        val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        var path: File? = null
        if(isAbsolutePath){
           path = File("$imageName.jpg")
        }else{
            path = File(directory, "$imageName.jpg")
        }*/
        val res = BitmapFactory.decodeFile(imageName)
        return res
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}
fun Bitmap.toByteArray():ByteArray{
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.PNG, 100, this)
        return toByteArray()
    }
}
fun ByteArray.toBitmap():Bitmap{
    return BitmapFactory.decodeByteArray(this, 0, size)
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap? {
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    vectorDrawable.draw(canvas)
    return bitmap
}

private fun getBitmap(vectorDrawable: VectorDrawableCompat): Bitmap? {
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    vectorDrawable.draw(canvas)
    return bitmap
}

fun bitmapFromReource(context: Context?, @DrawableRes drawableResId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context!!, drawableResId)
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else if (drawable is VectorDrawableCompat) {
        getBitmap(drawable)
    } else if (drawable is VectorDrawable) {
        getBitmap(drawable)
    } else {
        throw IllegalArgumentException("Unsupported drawable type")
    }
}

fun bitmapFromDrawable(context: Context?, drawable: Drawable): Bitmap? {
    //val drawable = ContextCompat.getDrawable(context!!, drawableResId)
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else if (drawable is VectorDrawableCompat) {
        getBitmap(drawable)
    } else if (drawable is VectorDrawable) {
        getBitmap(drawable)
    } else {
        throw IllegalArgumentException("Unsupported drawable type")
    }
}

fun Bitmap.radius(context: Context, radius: Float): Bitmap?{
    this.let {
        val circularBitmapDrawable: RoundedBitmapDrawable =
            RoundedBitmapDrawableFactory.create(context.getResources(), it)
        circularBitmapDrawable.cornerRadius = radius
        return circularBitmapDrawable.bitmap()
    }
}

fun Bitmap.md5(): String? {
    val srcBytes = this.toByteArray()
    try {
        // Create MD5 Hash
        val digest: MessageDigest = MessageDigest.getInstance("MD5")
        digest.update(srcBytes)
        val messageDigest: ByteArray = digest.digest()

        // Create Hex String
        val hexString = StringBuffer()
        for (i in messageDigest.indices) {
            val hb = Integer.toHexString(0xFF and messageDigest[i].toInt())
            if (hb.length == 1) {
                hexString.append('0')
            }
            hexString.append(hb)
        }
        return hexString.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

fun takeScreenshot(v: View): Bitmap? {
    v.clearFocus()
    v.setPressed(false)
    val willNotCache: Boolean = v.willNotCacheDrawing()
    v.setWillNotCacheDrawing(false)
    val color: Int = v.getDrawingCacheBackgroundColor()
    v.setDrawingCacheBackgroundColor(0)
    if (color != 0) {
        v.destroyDrawingCache()
    }
    v.buildDrawingCache()
    val cacheBitmap: Bitmap = v.getDrawingCache() ?: return null
    val bitmap = Bitmap.createBitmap(cacheBitmap)
    v.destroyDrawingCache()
    v.setWillNotCacheDrawing(willNotCache)
    v.setDrawingCacheBackgroundColor(color)
    return bitmap
}