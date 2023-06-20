package com.wgllss.ssmusic.datasource.netbean.sheet

import com.wgllss.ssmusic.data.MusicItemBean

data class KSheetDetailDto(
    var list: KSheetDetailDtoList? = null,
    var listData: MutableList<MusicItemBean>,
    val info: KSheetDetailDtoInfo
)