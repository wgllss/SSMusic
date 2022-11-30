package com.wgllss.ssmusic.core.units

import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import java.util.*

object UUIDHelp {

    fun getMusicUUID(musicBeanEvent: MusicBeanEvent): Long {
        val sb = StringBuilder()
        musicBeanEvent?.run {
            sb.append(title).append(author).append(requestRealUrl).append(pic).append(toString())
        }
        return Math.abs(UUID(sb.toString().hashCode().toLong(), sb.toString().hashCode().toLong()).toString().replace("-", "").hashCode()).toLong()
    }

    fun getMusicUUID(title: String, author: String, requestRealUrl: String, pic: String): Long {
        val sb = StringBuilder()
        sb.append(title).append(author).append(requestRealUrl).append(pic).append(toString())
        return Math.abs(UUID(sb.toString().hashCode().toLong(), sb.toString().hashCode().toLong()).toString().replace("-", "").hashCode()).toLong()
    }
}