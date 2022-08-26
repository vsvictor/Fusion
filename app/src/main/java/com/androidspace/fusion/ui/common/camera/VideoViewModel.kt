package com.androidspace.fusion.ui.common.camera

import android.app.Application
import android.os.Handler
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.ui.common.camera.data.OnVideoDestinatation
import com.androidspace.fusion.ui.common.camera.data.OnVideoEvents
import com.androidspace.fusion.ui.common.camera.data.OnVideoUI
import com.androidspace.fusion.ui.common.camera.data.VideoState

class VideoViewModel(override val app: Application): BaseViewModel<Object>(app) {
    private var _audioEnabled = true
    private var dest: OnVideoDestinatation? = null
    var audioEnabled get() = _audioEnabled; set(value) {
        _audioEnabled = value
    }
    private var _cameraIndex = 0
    var cameraIndex get() = _cameraIndex; set(value) {
        _cameraIndex = value
    }
    private var _qualityIndex = DEFAULT_QUALITY_IDX
    var qualityIndex get() = _qualityIndex; set(value) {
        _qualityIndex = value
    }
    private var _state: VideoState = VideoState.IDLE
    var state get() = _state; set(value) {
        _state = value
        when(_state){
            VideoState.RECORD -> {
                onVideoUI?.onVideoStop()
            }
            VideoState.IDLE -> {
                onVideoUI?.onVideoStart()
            }
        }
    }
    val cameraCapabilities = mutableListOf<CameraCapability>()

    var onVideoEvents: OnVideoEvents? = null
    var onVideoUI: OnVideoUI? = null

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            startPreviewVideo()
        }, 1000)
    }
    fun onStartVideo(view: View){
        if(state != VideoState.RECORD){
            onVideoEvents?.onStartRecord()
        }else{
            onVideoEvents?.onStopRecord()
        }
    }
    fun onSelectCamera(view: View){
        selectCamera()
    }
    fun onQuality(view: View){
        onVideoEvents?.onQualityChanged()
    }
    private fun startPreviewVideo(){
        cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
        qualityIndex = DEFAULT_QUALITY_IDX
        onVideoEvents?.onBindCaptureUseCase(cameraIndex, qualityIndex)
    }
    private fun selectCamera(){
        cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
        // camera device change is in effect instantly:
        //   - reset quality selection
        //   - restart preview
        qualityIndex = DEFAULT_QUALITY_IDX
        onVideoEvents?.onBindCaptureUseCase(cameraIndex, qualityIndex)
    }
    fun onRebind(){
        onVideoEvents?.onBindCaptureUseCase(cameraIndex, qualityIndex)
    }
    data class CameraCapability(val camSelector: CameraSelector, val qualities:List<Quality>)
    companion object {
        const val DEFAULT_QUALITY_IDX = 0
    }
    fun setOnDestinatationListener(listener: OnVideoDestinatation?){
        this.dest = listener
    }
}