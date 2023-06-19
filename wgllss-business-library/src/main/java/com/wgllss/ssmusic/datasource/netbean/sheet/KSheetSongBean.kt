package com.wgllss.ssmusic.datasource.netbean.sheet

data class KSheetSongBean(
    var songname: String,
    var album_sizable_cover: String,
    val mvhash: String,
    val song_url: String,
    val privilege: Int,
    val filename: String,
    var author_name: String
)