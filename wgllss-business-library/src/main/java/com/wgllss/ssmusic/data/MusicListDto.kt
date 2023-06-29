package com.wgllss.ssmusic.data

data class MusicListDto(
    val maxPage: Int = 1,
    val list: MutableList<MusicItemBean>
)
