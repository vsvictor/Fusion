package com.androidspace.fusion.ui.common.camera.data

interface OnVideoEvents {
    fun onBindCaptureUseCase(cameraIndex: Int, qualityIndex: Int)
    fun onStartRecord()
    fun onStopRecord()
    fun onQualityChanged()
}