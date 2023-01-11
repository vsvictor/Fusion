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

        val portal = Portal(BuildConfig.PORTAL_URL, true).apply {
            //credential = UserCredential("viktor.dzhurliak", "Vestern74")
        }
        val basemap = PortalItem(portal, "22cc4cff89ea44d781f6a7d93045f5f8")
        val regions = PortalItem(portal, "5e77557034b244db9d5bea56f4bbcdc4")
        var ukraine = PortalItem(portal, "a01b0b0a504f480aa7cb11a12a1cb38f")
    }
}