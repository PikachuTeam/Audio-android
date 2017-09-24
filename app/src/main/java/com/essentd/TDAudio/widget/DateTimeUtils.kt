package com.essentd.TDAudio.widget

/**
 * Created by dongc on 9/2/2017.
 */
object DateTimeUtils {
    fun toMediaPlayerTime(duration: Int): String {
        val totalSeconds = duration / 1000
        val minute = totalSeconds / 60
        val second = totalSeconds - minute * 60
        val minuteStr = if (minute >= 10) "" + minute else "0" + minute
        val secondStr = if (second >= 10) "" + second else "0" + second
        return minuteStr + ":" + secondStr
    }
}