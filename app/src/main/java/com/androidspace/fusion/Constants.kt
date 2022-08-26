package com.androidspace.fusion

import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.security.UserCredential

class Constants {
    companion object{
        const val LIMIT = 360*6
        const val SECOND = 1000
        const val MINUTE = 60* SECOND
        const val HOUR = 60* MINUTE
        const val DAY = 24* HOUR
        const val ANONIMUS = "anonimous@anon.com"
        const val ANONPASS = "anonimous"

        val portal = Portal(BuildConfig.PORTAL_URL, false)
        val basemap = PortalItem(portal, "01c27c8d5f6e4570a361fb8e761e0729")
        val light = PortalItem(portal,"eee73ee6c03a45888c50275c29ab816c")
        val regions = PortalItem(portal, "c17ab2ed296247149e1ded5cc6a6fd4a")
    }
}