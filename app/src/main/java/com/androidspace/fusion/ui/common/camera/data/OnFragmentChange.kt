package com.androidspace.fusion.ui.common.camera.data

import androidx.fragment.app.Fragment

interface OnFragmentChange {
    fun onFragmentOpen(fr: Fragment? = null)
    fun onFragmentClose(fr: Fragment? = null)
}