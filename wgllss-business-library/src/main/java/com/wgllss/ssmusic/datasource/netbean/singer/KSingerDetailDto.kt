package com.wgllss.ssmusic.datasource.netbean.singer

import com.wgllss.ssmusic.datasource.netbean.singer.KSingerInfo
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerSongs

data class KSingerDetailDto(
    var info: KSingerInfo,
    val songs: KSingerSongs
)
