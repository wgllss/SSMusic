package com.wgllss.ssmusic.core.units

import com.wgllss.ssmusic.data.MusicBean
import java.util.*

object UUIDHelp {

    fun getMusicUUID(musicBean: MusicBean): Int {
        val sb = StringBuilder()
        musicBean?.run {
            sb.append(title).append(author).append(url).append(pic).append(toString())
        }
        return Math.abs(UUID(sb.toString().hashCode().toLong(), musicBean.toString().hashCode().toLong()).toString().replace("-", "").hashCode())
    }
}