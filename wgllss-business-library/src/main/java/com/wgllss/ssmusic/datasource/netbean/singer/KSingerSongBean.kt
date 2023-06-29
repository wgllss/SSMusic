package com.wgllss.ssmusic.datasource.netbean.singer

data class KSingerSongBean(
    val audio_name: String,
    val song_url: String,
    val auhorName: String,
    val mvhash: String,
    var viewType: Int = 0
//    val privilege_download: KSingerPrivilege,
//    val album_info: KAlbumBean
)