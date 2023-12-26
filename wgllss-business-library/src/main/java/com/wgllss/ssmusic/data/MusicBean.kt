package com.wgllss.ssmusic.data

import com.wgllss.ssmusic.core.units.UUIDHelp


data class MusicBean(
    val title: String,
    val author: String,
    var url: String,
    var pic: String,
    val dataSourceType: Int = 0, //0 : hifi 1 :kg
    val privilege: Int = 0,
    val mvhash: String = ""
) {
    var requestRealUrl: String = ""
    var musicLrcStr: String = ""
    inline val id: Long
        get() = UUIDHelp.getMusicUUID(title, author)
}