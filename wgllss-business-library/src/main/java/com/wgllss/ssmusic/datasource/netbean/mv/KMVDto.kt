package com.wgllss.ssmusic.datasource.netbean.mv

data class KMVDto(
    val songname: String,
    val singer: String,
    val remark: String,
    val publish_date: String,
    val play_count: String,
    var mvicon: String = "",
    val timelength: String,
    val mvdata: KMVDtoMvData
)
