package com.androidspace.fusion.base.interfaces

import android.view.View

interface OnKeyboadVisibleChanged {
    fun setKeyboarVisible(isVisible: Boolean)
    fun setKeyboarVisible(view: View, isVisible: Boolean)
    fun onKeyboardVisibleChanged(isShow: Boolean)
    fun isKeyboardVisible():Boolean
}