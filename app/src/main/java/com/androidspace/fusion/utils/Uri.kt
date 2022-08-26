package com.androidspace.fusion.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.FileNotFoundException
import java.io.IOException


fun Uri.toFileName(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        context.getContentResolver().query(this, projection, null, null, null) ?: return null
    val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(column_index)
    cursor.close()
    return s
}
fun Uri.toVideoFileName(context: Context): String? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor: Cursor =
        context.getContentResolver().query(this, projection, null, null, null) ?: return null
    val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(column_index)
    cursor.close()
    return s
}

 fun Uri.getRealPath(context: Context): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        cursor = context.getContentResolver().query(this, proj, null, null, null)
        cursor?.let {
            val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(column_index)
        }
    } catch (e: Exception) {
        this.path
    } finally {
        cursor?.close()
    }
}

fun Uri.asRequestBody(contentResolver: ContentResolver,
                      contentType: MediaType? = null,
                      contentLength: Long = -1L)
        : RequestBody {

    val acc = try {
        contentResolver.openAssetFileDescriptor(this, "r")
    } catch (e: FileNotFoundException) {
        null
    }
    val pfd = try {
        contentResolver.openFileDescriptor(this, "r")
    } catch (e: FileNotFoundException) {
        null
    }
    val iss = try {
        contentResolver.openInputStream(this)
    } catch (e: FileNotFoundException) {
        null
    }
    return object : RequestBody() {
        /** If null is given, it is binary for Streams */
        override fun contentType() = contentType

        /** 'chunked' transfer encoding will be used for big files when length not specified */
        override fun contentLength() = contentLength

        /** This may get called twice if HttpLoggingInterceptor is used */
        override fun writeTo(sink: BufferedSink) {
            val assetFileDescriptor = acc
            if (assetFileDescriptor != null) {
                // when InputStream is closed, it auto closes AssetFileDescriptor
                AssetFileDescriptor.AutoCloseInputStream(assetFileDescriptor)
                    .source()
                    .use { source -> sink.writeAll(source) }
            } else {
                val inputStream = iss
                if (inputStream != null) {
                    inputStream
                        .source()
                        .use { source -> sink.writeAll(source) }
                } else {
                    val parcelFileDescriptor = pfd
                    if (parcelFileDescriptor != null) {
                        // when InputStream is closed, it auto closes ParcelFileDescriptor
                        ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor)
                            .source()
                            .use { source -> sink.writeAll(source) }
                    } else {
                        throw IOException()
                    }
                }
            }
        }
    }
}

fun Uri.findFileName(): String?{
    val list = this.pathSegments
    if(list.size > 0) {
        return list.get(list.size - 1)
    }else {
        return null
    }
}