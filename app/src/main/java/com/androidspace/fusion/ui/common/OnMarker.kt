package com.androidspace.fusion.ui.common

import com.esri.arcgisruntime.mapping.view.Graphic

interface OnMarker {
    fun onAddMaker(marker: Graphic)
}