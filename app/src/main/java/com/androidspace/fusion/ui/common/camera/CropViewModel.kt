package com.androidspace.fusion.ui.common.camera

import android.app.Application
import android.graphics.Bitmap
import android.view.View
import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.domain.usecase.RefreshUseCase
import com.androidspace.fusion.domain.usecase.UseCaseResizeBitmap
import com.androidspace.fusion.domain.usecase.UseCaseTokens
import com.androidspace.fusion.utils.SingleLiveEvent

class CropViewModel(override val app: Application,
                    private val useCaseTokens: UseCaseTokens,
                    private val refreshUseCase: RefreshUseCase,
                    private val useCaseResizeBitmap: UseCaseResizeBitmap) : BaseViewModel<Object>(app) {
    private val TAG = CropViewModel::class.java.simpleName
    val cropper = SingleLiveEvent<Boolean>()
    private var _cropped: Bitmap? = null
    var cropped: Bitmap? get() = _cropped; set(value) {
        _cropped = value
        _cropped?.let {
            useCaseResizeBitmap.execute(viewModelScope, UseCaseResizeBitmap.Param(it,200,200,"avatar"),
                    success = {
                              //uploadAvatar(it.absolutePath)
                    },
                    fail = {
                        val err = ErrorData(-1, ErrorBody(app.applicationContext.getString(R.string.bitmap_resize_error)))
                        error.postValue(err)
                    })
        }
    }
    var backID = -1
/*    @MainThread
    private fun toProfile(){
        Log.d(TAG, "To profile")
        navController?.navigate(R.id.aboutFragment)
    }*/
/*    private fun uploadAvatar(fileName: String){
        uploadAvatarUseCase.execute(viewModelScope, UploadAvatarUseCase.Param(fileName),
            success = {
                //toProfile()
        },
        fail = {
            error.postValue(it)
        })
    }*/
    @MainThread
    override fun onBackPressed() {
        super.onBackPressed()
        if(backID > 0){
            navController?.navigate(backID)
        }else{
            //navController?.navigate(R.id.cameraFragment)
        }
    }
    fun onCrop(view: View){
        cropper.postValue(true)
    }
}