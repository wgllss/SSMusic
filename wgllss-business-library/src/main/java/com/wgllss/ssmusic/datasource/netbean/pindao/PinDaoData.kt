package com.wgllss.ssmusic.datasource.netbean.pindao

data class PinDaoData(
    val lyrics: String,
    val audio_name: String,
    val song_name: String,
    val album_name: String,
    val author_name: String,
    val privilege: Int,
    val play_url: String,
    val play_backup_url: String,
    val encode_album_id: String,
    val encode_album_audio_id: String,
    val img: String
)
