package com.wgllss.ssmusic.datasource.netbean.search

data class KGSearchBean(
    val MvHash: String,
    var Image: String,
    val SingerName: String,
    val SongName: String,
    val FileName: String,
    val EAlbumID: String,//专辑链接ID
    val EMixSongID: String,//歌曲网页ID
    val Privilege: Int
)
