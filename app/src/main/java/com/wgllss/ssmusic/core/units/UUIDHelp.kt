package com.wgllss.ssmusic.core.units

import java.util.*

object UUIDHelp {

    fun getMusicUUID(title: String, author: String, requestRealUrl: String, pic: String): Long {
        val sb = StringBuilder()
        sb.append(title).append(author).append(requestRealUrl).append(pic)
        val uuLongID = sb.toString().hashCode().toLong()
        return Math.abs(UUID(uuLongID, uuLongID).toString().replace("-", "").hashCode()).toLong()
    }
}