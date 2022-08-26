package com.androidspace.fusion.ui.common.camera

import com.androidspace.fusion.ui.common.camera.data.OnBitmapDestinatation


interface OnSelectSourcePhoto {
    fun onPhoto()
    fun onCamera(dest: OnBitmapDestinatation? = null)
    fun onVideo(dest: OnBitmapDestinatation? = null)
    fun onGallery()
    fun onCancel()
}