package com.wgllss.ssmusic.datasource.netbean

import com.wgllss.ssmusic.data.MusicItemBean

data class SongSheetDetailDto(
    val imgUrl: String,
    val title: String = "",
    val authorNameL: String = "",
    val authorImgUrl: String = "",
    val txtIntroduce: String = "",
    val list: MutableList<MusicItemBean>
)