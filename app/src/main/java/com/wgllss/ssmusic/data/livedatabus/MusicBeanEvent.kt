package com.wgllss.ssmusic.data.livedatabus

import com.jeremyliao.liveeventbus.core.LiveEvent

data class MusicBeanEvent(
    var title: String = "",
    var author: String = "",
    var url: String = "",
    var pic: String = "",
    var musicType: Int = 0,
    var playFrom: Int = 0,
    var uuid: Long = 0
) : LiveEvent {
}