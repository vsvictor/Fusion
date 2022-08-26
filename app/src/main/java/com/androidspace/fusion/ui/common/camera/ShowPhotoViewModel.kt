package com.androidspace.fusion.ui.common.camera

import android.app.Application
import androidx.annotation.MainThread
import com.androidspace.fusion.base.BaseViewModel

class ShowPhotoViewModel(override val app: Application) : BaseViewModel<Object>(app) {

    private val TAG = ShowPhotoViewModel::class.java.simpleName
    var backID = -1
    @MainThread
    override fun onBackPressed() {
        super.onBackPressed()
        if(backID > 0){
            navController?.navigate(backID)
        }else{
            navController?.popBackStack()
        }
    }
}