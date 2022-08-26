package com.androidspace.fusion.ui.profile.data

interface OnOffline {
    fun onOfflineShow(dirname: String? = null)
    fun onCancel()
    fun onSuccess()
    fun onFail(message: String)
    fun onProgress(progress: Int)
}