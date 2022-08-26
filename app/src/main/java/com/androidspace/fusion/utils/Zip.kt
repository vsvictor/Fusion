package com.androidspace.fusion.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Zip {
    companion object{
        @Throws(Exception::class)
        fun zipFolder(srcFolder: String, destZipFile: String) {
            var zip: ZipOutputStream? = null
            var fileWriter: FileOutputStream? = null
            fileWriter = FileOutputStream(destZipFile)
            zip = ZipOutputStream(fileWriter)
            addFolderToZip("", srcFolder+"/", zip)
            zip.flush()
            zip.close()
        }
        @Throws(java.lang.Exception::class)
        private fun addFolderToZip(path: String, srcFolder: String, zip: ZipOutputStream) {
            val folder = File(srcFolder)
            for (fileName in folder.list()) {
                if (path == "") {
                    addFileToZip(folder.getName(), "$srcFolder/$fileName", zip)
                } else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip)
                }
            }
        }
        @Throws(java.lang.Exception::class)
        private fun addFileToZip(path: String, srcFile: String, zip: ZipOutputStream) {
            val folder = File(srcFile)
            if (folder.isDirectory) {
                addFolderToZip(path, srcFile, zip)
            } else {
                val buf = ByteArray(1024)
                var len: Int
                val `in` = FileInputStream(srcFile)
                zip.putNextEntry(ZipEntry(path + "/" + folder.name))
                while (`in`.read(buf).also { len = it } > 0) {
                    zip.write(buf, 0, len)
                }
            }
        }
    }
}