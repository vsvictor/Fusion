package com.androidspace.fusion.data.repository

import com.androidspace.fusion.data.model.TokenResponseData
import com.androidspace.fusion.ui.map.data.MapType


class RepositoryImpl(private val local: LocalDataSource, private val remote: RemoteDataSource): Repository {

    override fun setFirstStart(isFirst: Boolean) = local.setFirstStart(isFirst)
    override fun isFirtsStart(): Boolean = local.isFirstStart()
    override fun getAccessToken(): String = local.getAccessToken()
    override fun setAccessToken(token: String) = local.setAccessToken(token)
    override fun getRefreshToken(): String = local.getRefreshToken()
    override fun setRefreshToken(token: String) = local.setRefreshToken(token)
    override fun setUseFingerPrint(use: Boolean) = local.setUseFingerPrint(use)
    override fun isUseFingerPrint(): Boolean = local.isUseFingerPrint()
    override fun setAgree(agree: Boolean) = local.setAgree(agree)
    override fun isAgree(): Boolean = local.isAgree()
    override fun getMapType(): MapType = local.getMapType()
    override fun setMapType(type: MapType) = local.setMapType(type)
    override fun getStartFrom(): Int = local.getStartFrom()
    override fun setStartFrom(startFrom: Int) = local.setStartFrom(startFrom)
    override fun getBasemap(): String = local.getBasemap()
    override fun setBasemap(basemapID: String) = local.setBasemap(basemapID)

    override fun clearTokens() {local.clearTokens()}

    override suspend fun reftesh(): TokenResponseData? = try{remote.refresh()}catch (ex: Throwable){throw ex}
}