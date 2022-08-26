package com.androidspace.fusion.ui.profile.data

import com.androidspace.fusion.data.model.RegionData


interface OnDownload {
    fun onDownloadFile(data: RegionData)
}