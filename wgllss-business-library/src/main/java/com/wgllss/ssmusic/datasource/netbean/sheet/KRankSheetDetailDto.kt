package com.wgllss.ssmusic.datasource.netbean.sheet

import com.wgllss.ssmusic.data.MusicItemBean

data class KRankSheetDetailDto(
    var songs: KRankSheetDetailDtoSongs? = null,
    val info: KRankSheetDetailDtoInfo,
    var listData: MutableList<MusicItemBean>,
)