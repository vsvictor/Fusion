package com.androidspace.fusion.ui.common.camera.data

import androidx.fragment.app.Fragment

interface OnFragment {
    fun onFragmentOpen(fr: Fragment)
    fun onFragmentClose(fr: Fragment)
}