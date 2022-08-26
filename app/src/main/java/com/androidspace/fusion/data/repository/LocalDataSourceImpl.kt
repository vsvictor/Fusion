package com.androidspace.fusion.data.repository

import android.content.SharedPreferences
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import com.androidspace.fusion.ui.map.data.MapType

class LocalDataSourceImpl(val store: SharedPreferences): LocalDataSource {
    override fun setFirstStart(isFirst: Boolean){ SettingsSharedPreferences(store).firstStart = isFirst}
    override fun isFirstStart():Boolean = SettingsSharedPreferences(store).firstStart
    override fun getAccessToken(): String = SettingsSharedPreferences(store).accessToken
    override fun setAccessToken(token: String) { SettingsSharedPreferences(store).accessToken = token}
    override fun getRefreshToken(): String = SettingsSharedPreferences(store).refreshToken
    override fun setRefreshToken(token: String){SettingsSharedPreferences(store).refreshToken = token}
    override fun clearTokens() {SettingsSharedPreferences(store).clearTokens()}
    override fun setUseFingerPrint(use: Boolean) { SettingsSharedPreferences(store).useFingerPrint = use}
    override fun isUseFingerPrint(): Boolean = SettingsSharedPreferences(store).useFingerPrint
    override fun setAgree(agree: Boolean) { SettingsSharedPreferences(store).isAgree = agree}
    override fun isAgree(): Boolean = SettingsSharedPreferences(store).isAgree
    override fun getMapType(): MapType = SettingsSharedPreferences(store).mapType
    override fun setMapType(type: MapType){SettingsSharedPreferences(store).mapType = type}
    override fun getStartFrom(): Int = SettingsSharedPreferences(store).startFrom
    override fun setStartFrom(startFrom: Int) { SettingsSharedPreferences(store).startFrom = startFrom }
    override fun getBasemap(): String = SettingsSharedPreferences(store).basemap
    override fun setBasemap(basemapID: String) {SettingsSharedPreferences(store).basemap = basemapID}
}