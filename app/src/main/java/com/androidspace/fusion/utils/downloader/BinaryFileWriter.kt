package com.androidspace.fusion.utils.downloader

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BinaryFileWriter(outputStream: OutputStream, progressCallback: ProgressCallback) :
    AutoCloseable {
    private val outputStream: OutputStream
    private val progressCallback: ProgressCallback
    @Throws(IOException::class)
    fun write(inputStream: InputStream?, length: Double): Long {
        BufferedInputStream(inputStream).use { input ->
            val dataBuffer =
                ByteArray(CHUNK_SIZE)
            var readBytes: Int
            var totalBytes: Long = 0
            while (input.read(dataBuffer).also { readBytes = it } != -1) {
                totalBytes += readBytes.toLong()
                outputStream.write(dataBuffer, 0, readBytes)
                progressCallback.onProgress(totalBytes / length * 100.0)
            }
            return totalBytes
        }
    }

    @Throws(IOException::class)
    override fun close() {
        outputStream.close()
    }

    companion object {
        private const val CHUNK_SIZE = 1024
    }

    init {
        this.outputStream = outputStream
        this.progressCallback = progressCallback
    }
}