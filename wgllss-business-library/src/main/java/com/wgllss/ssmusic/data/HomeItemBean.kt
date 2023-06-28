package com.wgllss.ssmusic.data

import com.wgllss.ssmusic.datasource.netbean.KMusicHotSongBean
import com.wgllss.ssmusic.datasource.netbean.KSingerBean
import com.wgllss.ssmusic.datasource.netbean.rank.KRankExBean

data class HomeItemBean(
    val itemType: Int,
    var homeLableBean: HomeLableBean? = null,
    var kMusicItemBean: MusicItemBean? = null,
    var kKMusicHotSongBean: KMusicHotSongBean? = null,
    var kRankExBean: KRankExBean? = null,
    var kSingerBean: KSingerBean? = null,
    var kMenuBean: KMenuBean? = null
)