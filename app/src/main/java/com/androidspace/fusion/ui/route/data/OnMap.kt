package com.androidspace.fusion.ui.route.data

import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap

interface OnMap {
    fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer? = null)
}