package com.androidspace.fusion.data.local

import com.androidspace.fusion.ui.map.data.MapType


const val FIRST_START = "first_start"
const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val USE_FINGERPRINT = "use_fingerpring"
const val IS_AGREE = "agree"
const val MAP_TYPE = "map_type"
const val START_FROM = "start_from"
const val BASEMAP = "basemap"

interface Settings {
    var firstStart: Boolean
    var accessToken: String
    var refreshToken: String
    var useFingerPrint: Boolean
    var isAgree: Boolean
    var mapType: MapType
    fun clearTokens()
    var startFrom: Int
    var basemap: String
}