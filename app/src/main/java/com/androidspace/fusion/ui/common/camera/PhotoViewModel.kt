package com.androidspace.fusion.ui.common.camera

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.MainThread
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.ui.common.camera.data.OnBitmapDestinatation
import com.androidspace.fusion.ui.common.camera.data.OnFragmentChange
import com.androidspace.fusion.utils.YuvToRgbConverter
import com.androidspace.fusion.utils.rotateBitmap
import com.androidspace.fusion.utils.saveJPG
import com.androidspace.fusion.utils.toByteArray

class PhotoViewModel(override val app: Application) : BaseViewModel<Object>(app) {
    private var converter = YuvToRgbConverter(app.applicationContext)
    private lateinit var picture: Bitmap
    private var dest: OnBitmapDestinatation? = null
    //private var onFragmentChange: OnFragmentChange? = null
    val flash = MutableLiveData<FlashState>()
    val selector = MutableLiveData<Boolean>()
    val takePhoto = MutableLiveData<Boolean>()
    val handler = Handler(Looper.getMainLooper())
    private var isRear = true
    private var flashState = FlashState.AUTO
    var backID = -1

    var args: Bundle? = null

    override fun load(intent: Intent) {
        super.load(intent)
        isRear = true
        selector.postValue(isRear)
        flashState = FlashState.AUTO
        flash.postValue(flashState)
    }

    @MainThread
    override fun onBackPressed() {
        super.onBackPressed()
        if(backID > 0){
            args?.let {
                navController?.navigate(backID, args)
            }?: kotlin.run {
                navController?.navigate(backID)
            }
        }else {
            //navController?.navigate(R.id.navigation_profile)
            navController?.popBackStack()
        }
    }
    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    fun convert(imageProxy: ImageProxy){
        handler.post(Runnable {
            val degree = imageProxy.imageInfo.rotationDegrees
            val bitmap = Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
            bitmap.saveJPG(app, "test", 100)
            imageProxy.apply { imageProxy.image?.let { converter.yuvToRgb(it, bitmap) } }
            picture = bitmap.rotateBitmap(degree)
            dest?.let {
                it.onDestinatation(picture)
                //onBackPressed()
                dest = null
                //onFragmentChange?.onFragmentClose()
                //onBackPressed()
            }?: kotlin.run {
                val arr = picture.toByteArray()
                val bundle = Bundle().apply {
                    putByteArray("picture", arr)
                    putInt("back", backID)
                }
                toCropper(bundle)
            }
        })
    }
    @MainThread
    private fun toCropper(bundle: Bundle){
        //navController?.navigate(R.id.cropFragment, bundle)
    }
    fun takePhoto(view: View){
        takePhoto.postValue(true)
    }

    fun onSelectCamera(view: View){
        isRear = !isRear
        selector.postValue(isRear)
    }
    fun onFlashSwitch(view: View){
        val res = when(flashState){
            FlashState.AUTO -> FlashState.ON
            FlashState.ON -> FlashState.OFF
            FlashState.OFF -> FlashState.AUTO
        }
        flashState = res
        flash.postValue(flashState)
    }
    fun setOnDestinatationListener(listener: OnBitmapDestinatation?){
        this.dest = listener
    }
    fun setOnFragmentCloseListener(listener: OnFragmentChange){
        //this.onFragmentChange = listener
    }
}