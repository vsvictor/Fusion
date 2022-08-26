package com.androidspace.fusion.utils

import android.R.attr.textColor
import android.R.attr.textSize
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val TEXT_DIR = "text"

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
fun String.isValidPassword(easy: Boolean = true):Boolean{
    val exp = if(easy) "^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*)[^\\s]{8,}\$" else "^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=]).*)[^\\s]{8,}\$"
    val pattern = Pattern.compile(exp)
    return pattern.matcher(this).matches()
}
fun String.isPhone(): Boolean {
    val res = this.replace("(","").replace(")","").replace(" ","")
    val str = "\\+?3?8?(0[5-9][0-9]\\d{7})\$".toRegex()
    val r = res.matches(str)
    return r
}
fun String.asPhone(): String{
    return this.substring(0,3)+" ("+this.substring(3,6)+") "+this.substring(6,9)+" "+this.substring(9,11)+" "+this.substring(11,13)
}
fun String.asDate(format: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") : Date{
    SimpleDateFormat(format).let {
        return it.parse(this)
    }
}

fun String.asBitmap(): Bitmap{
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setTextSize(textSize.toFloat())
    paint.setColor(textColor)
    paint.setTextAlign(Paint.Align.LEFT)
    val baseline: Float = -paint.ascent() // ascent() is negative

    val image = Bitmap.createBitmap(
        (paint.measureText(this).toInt()),
        (baseline + paint.descent()).toInt(), Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(image)
    canvas.drawText(this, 0f, baseline, paint)
    return image
}
fun String.asText(context: Context, fileName: String):File{
    val contextWrapper = ContextWrapper(context)
    val directory = contextWrapper.getDir(TEXT_DIR, Context.MODE_PRIVATE)
    val path = File(directory, "$fileName")

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(path)
        //val oWriter = OutputStreamWriter(fileOutputStream)
        //oWriter.write(this.toByteArray())
        fileOutputStream?.write(this.toByteArray())
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
fun String.isMediaFile():Boolean{
    val PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|mp4|3gp))$)"
    val pattern = Pattern.compile(PATTERN)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}
fun String.isVideoFile():Boolean{
    val PATTERN = "([^\\s]+(\\.(?i)(mp4|3gp))$)"
    val pattern = Pattern.compile(PATTERN)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

