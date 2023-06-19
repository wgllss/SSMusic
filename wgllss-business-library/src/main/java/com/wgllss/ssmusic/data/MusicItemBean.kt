package com.wgllss.ssmusic.data

data class MusicItemBean(
    val author: String,//演唱人
    val musicName: String,//歌曲名称
    val detailUrl: String,//详细地址获取播放地址
    var samplingRate: String,//采样率
    val album_sizable_cover: String = "",
    val mvhash: String = "",
    val dataSourceType: Int = 0, //0 : hifi 1 :kg
    val privilege: Int = 0
)
