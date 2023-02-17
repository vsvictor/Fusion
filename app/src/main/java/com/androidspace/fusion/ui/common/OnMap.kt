package com.androidspace.fusion.ui.route.data

import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.layers.FeatureLayer

interface OnMap {
    fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer? = null)

}