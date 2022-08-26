package com.androidspace.fusion.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.view.WindowInsets
import android.view.WindowManager

fun Context.dp(px: Float): Float {
    return px / this.getResources().getDisplayMetrics().density
}
fun Context.px(dp: Float): Float {
    return dp * this.resources.displayMetrics.density
}

val Context.statusBarHeight: Int get(){
    var result = 0
    val resourceId: Int = this.getResources().getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = this.getResources().getDimensionPixelSize(resourceId)
    }
    return result
}

val Context.navigationBarHeight: Int
    @SuppressLint("NewApi")
    get() {
        if(hasNavigationBar){
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return if (Build.VERSION.SDK_INT >= 30) {
                windowManager
                    .currentWindowMetrics
                    .windowInsets
                    .getInsets(WindowInsets.Type.navigationBars())
                    .bottom
            } else {
                val currentDisplay = try {
                    display
                } catch (e: NoSuchMethodError) {
                    windowManager.defaultDisplay
                }
                val appUsableSize = Point()
                val realScreenSize = Point()
                currentDisplay?.apply {
                    getSize(appUsableSize)
                    getRealSize(realScreenSize)
                }

                // navigation bar on the side
                if (appUsableSize.x < realScreenSize.x) {
                    return realScreenSize.x - appUsableSize.x
                }

                // navigation bar at the bottom
                return if (appUsableSize.y < realScreenSize.y) {
                    realScreenSize.y - appUsableSize.y
                } else 0
            }

        }else {
            return 0
        }
    }
val Context.hasNavigationBar: Boolean get() {
    val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    val result = id > 0 && resources.getBoolean(id)
    return result
}
fun Context.isLocationEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is a new method provided in API 28
        val lm: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.isLocationEnabled()
    } else {
        // This was deprecated in API 28
        val mode: Int = Settings.Secure.getInt(
            this.contentResolver, Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        mode != Settings.Secure.LOCATION_MODE_OFF
    }
}
val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels


