package com.wgllss.ssmusic.data

import com.wgllss.ssmusic.datasource.netbean.KMusicHotSongBean
import com.wgllss.ssmusic.datasource.netbean.KSingerBean
import com.wgllss.ssmusic.datasource.netbean.rank.KRankExBean

data class HomeItemBean(
    val itemType: Int,
    var homeLableBean: HomeLableBean? = null,
    var listNew: MutableList<MusicItemBean>? = null,
    var listHot: MutableList<KMusicHotSongBean>? = null,
    var rankList: MutableList<KRankExBean>? = null,
    var singers: MutableList<KSingerBean>? = null,
)