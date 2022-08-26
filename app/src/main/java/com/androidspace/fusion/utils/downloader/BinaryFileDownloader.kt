package com.androidspace.fusion.utils.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.util.*


class BinaryFileDownloader(private val client: OkHttpClient, private val writer: BinaryFileWriter) : AutoCloseable {
    @Throws(IOException::class)
    fun download(url: String): Long {
        val request: Request = Request.Builder().url(url).build()
        val response: Response = client.newCall(request).execute()
        val responseBody: ResponseBody = response.body
            ?: throw IllegalStateException("Response doesn't contain a file")
        val length: Double = Objects.requireNonNull(response.header("Content-Length", "1"))!!.toDouble()
        return writer.write(responseBody.byteStream(), length)
    }

    @Throws(Exception::class)
    override fun close() {
        writer.close()
    }
}