package com.wgllss.ssmusic.data

import com.wgllss.ssmusic.core.units.UUIDHelp


data class MusicBean(
    val title: String,
    val author: String,
    var url: String,
    val pic: String,
    val dataSourceType: Int = 0, //0 : hifi 1 :kg
    val privilege: Int = 0,
    val mvhash: String = ""
) {
    var requestRealUrl: String = ""
    inline val id: Long
        get() = UUIDHelp.getMusicUUID(title, author)
}
//{
//    "title":"起风了",
//    "author":"潇潇潇潇如",
//    "url":"https://m.hifini.com/music/潇潇潇潇如-起风了.m4a",
//    "pic":"https://y.gtimg.cn/music/photo_new/T002R300x300M000004VdMu943Cmuw.jpg"
//}