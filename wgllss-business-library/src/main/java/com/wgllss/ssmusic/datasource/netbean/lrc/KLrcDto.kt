package com.wgllss.ssmusic.datasource.netbean.lrc

data class KLrcDto(
    val status: Int,
    var errcode: Int,
    val error: String,
    val data: KLrcData
)