package com.androidspace.fusion.data.local

import android.content.SharedPreferences
import com.androidspace.fusion.ui.map.data.MapType

data class SettingsSharedPreferences(val prefs: SharedPreferences): Settings {
    override var firstStart: Boolean
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) {
            prefs.edit().putBoolean(FIRST_START, value).apply()
        }
    override var accessToken: String
        get() = prefs.getString(ACCESS_TOKEN,"")?:""
        set(value) {
            prefs.edit().putString(ACCESS_TOKEN, value).commit()
        }
    override var refreshToken: String
        get() = prefs.getString(REFRESH_TOKEN, "")?:""
        set(value) {
            prefs.edit().putString(REFRESH_TOKEN, value).commit()
        }
    override fun clearTokens() {
        prefs.edit().remove(ACCESS_TOKEN).commit()
        prefs.edit().remove(REFRESH_TOKEN).commit()
    }

    override var startFrom: Int
        get() = prefs.getInt(START_FROM, -1)
        set(value) { prefs.edit().putInt(START_FROM, value).commit() }
    override var basemap: String
        get() = prefs.getString(BASEMAP, "01c27c8d5f6e4570a361fb8e761e0729")?:"01c27c8d5f6e4570a361fb8e761e0729"
        set(value) {
            prefs.edit().putString(BASEMAP, value).commit()
        }

    override var useFingerPrint: Boolean
        get() = prefs.getBoolean(USE_FINGERPRINT, false)
        set(value) {
            prefs.edit().putBoolean(USE_FINGERPRINT, value).commit()
        }
    override var isAgree: Boolean
        get() = prefs.getBoolean(IS_AGREE, false)
        set(value) {
            prefs.edit().putBoolean(IS_AGREE, value).commit()
        }
    override var mapType: MapType
        get() = when(prefs.getInt(MAP_TYPE, 1)){
            1 -> MapType.STANDART
            2 -> MapType.SATTELITE
            else -> MapType.STANDART
        }
        set(value) {
            prefs.edit().putInt(MAP_TYPE, value.type).commit()
        }
}