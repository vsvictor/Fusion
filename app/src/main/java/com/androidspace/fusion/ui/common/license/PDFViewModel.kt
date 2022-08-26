package com.infotech.mines.ui.common.license

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.androidspace.fusion.base.BaseViewModel

class PDFViewModel(override val app: Application) : BaseViewModel<Object>(app) {
    var back = -1; //R.id.menuFragment
    var last = -1
    var page = -1
    override fun onBackPressed() {
        super.onBackPressed()
        if(page > 0) {
            navController?.navigate(back, Bundle().apply {
                putInt("page", page)
                putInt("back", last)
            })
        }else{
            navController?.navigate(back, Bundle().apply {
                putInt("back", last)
            })
        }
    }
}