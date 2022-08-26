package com.androidspace.fusion.utils

import android.annotation.SuppressLint
import com.androidspace.fusion.Constants
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.nextDate():Date{
    return Date((this.time+ Constants.DAY))
}
fun Date.prevDate():Date{
    return Date((this.time-Constants.DAY))
}

fun Date.asString(format: String = "yyyy-MM-dd HH:mm:ss"): String{
    var toUpperCase = false
    var vFormat = ""
    if(format.contains("+")){
        toUpperCase = true
        vFormat = format.replace("+","")
    }else{
        vFormat = format
    }
    val dateFormat = SimpleDateFormat(vFormat)
    var res = dateFormat.format(this)
    if(toUpperCase){
        var firstSymbol = res.substring(0,1)
        var otherSymbols = res.subSequence(1,res.length)
        res = firstSymbol.toUpperCase()+otherSymbols
    }
    return res
}
fun Date.hashCode(): Int{
    val result = 31 *  this.hashCode()
    return result
}
const val MINUTES_PER_HOUR = 60
const val SECONDS_PER_MINUTE = 60
const val SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR
const val SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR
@SuppressLint("NewApi")
fun getTime(dob: LocalDateTime, now: LocalDateTime): LongArray? {
    val today: LocalDateTime = LocalDateTime.of(
        now.getYear(),
        now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond()
    )
    val duration: Duration = Duration.between(today, now)
    val seconds: Long = duration.getSeconds()
    val hours: Long = seconds / SECONDS_PER_HOUR
    val minutes: Long = seconds % SECONDS_PER_HOUR / SECONDS_PER_MINUTE
    val secs: Long = seconds % SECONDS_PER_MINUTE
    return longArrayOf(hours, minutes, secs)
}

@SuppressLint("NewApi")
fun getTime(dob: Date, now: Date): LongArray? {
    val diffInMs: Long = dob.getTime() - now.getTime()
    val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
    val seconds: Long = diffInSec

    val hours: Long = seconds / SECONDS_PER_HOUR
    val minutes: Long = seconds % SECONDS_PER_HOUR / SECONDS_PER_MINUTE
    val secs: Long = seconds % SECONDS_PER_MINUTE
    return longArrayOf(hours, minutes, secs)
}
@SuppressLint("NewApi")
fun getFullTime(dob: Date, now: Date): LongArray? {
    val diffInMs: Long = dob.getTime() - now.getTime()
    val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
    var seconds: Long = diffInSec
    val days = seconds / SECONDS_PER_DAY
    seconds = seconds - (days* SECONDS_PER_DAY)
    val hours: Long = seconds / SECONDS_PER_HOUR
    val minutes: Long = seconds % SECONDS_PER_HOUR / SECONDS_PER_MINUTE
    val secs: Long = seconds % SECONDS_PER_MINUTE
    return longArrayOf(days, hours, minutes, secs)
}