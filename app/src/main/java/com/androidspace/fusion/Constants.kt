package com.androidspace.fusion

import com.arcgismaps.portal.Portal
import com.arcgismaps.portal.PortalItem
import com.arcgismaps.portal.PortalPrivilegeRealm

class Constants {
    companion object{
        const val LIMIT = 360*6
        const val SECOND = 1000
        const val MINUTE = 60* SECOND
        const val HOUR = 60* MINUTE
        const val DAY = 24* HOUR
        const val tolerance = 10.0

        val portal = Portal(BuildConfig.PORTAL_URL)

    }
}