package com.wgllss.ssmusic.core.units

import java.util.*
import kotlin.math.abs

object UUIDHelp {

    fun getMusicLRCUUID(title: String, author: String): Long {
        val sb = StringBuilder()
        sb.append(title.trim()).append(author.trim())
        val uuLongID = sb.toString().hashCode().toLong()
        return abs(UUID(uuLongID, uuLongID).toString().replace("-", "").hashCode()).toLong()
    }

    fun getMusicUUID(title: String, author: String, dataSourceType: Int): Long {
        val sb = StringBuilder()
        sb.append(title.trim()).append(author.trim()).append(dataSourceType)
        val uuLongID = sb.toString().hashCode().toLong()
        return abs(UUID(uuLongID, uuLongID).toString().replace("-", "").hashCode()).toLong()
    }
}