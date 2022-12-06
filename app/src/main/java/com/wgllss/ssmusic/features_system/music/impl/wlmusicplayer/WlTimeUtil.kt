package com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer

//object WlTimeUtil {
//
//    fun secdsToDateFormat(secds: Int, totalsecds: Int): String {
//        val hours = (secds / (60 * 60)).toLong()
//        val minutes = (secds % (60 * 60) / 60).toLong()
//        val seconds = (secds % 60).toLong()
//        var sh = if (hours > 0) if (hours < 10) "0$hours" else hours.toString() + "" else "00"
//        var sm = if (minutes > 0) if (minutes < 10) "0$minutes" else minutes.toString() + "" else "00"
//        var ss = if (seconds > 0) if (seconds < 10) "0$seconds" else seconds.toString() + "" else "00"
//        return if (totalsecds >= 3600) "$sh:$sm:$ss" else "$sm:$ss"
//    }
//
//    fun getProgress(currSecs: Int, totalSecs: Int): Int = if (totalSecs > 0) currSecs * 100 / totalSecs else 0
//}