package com.wgllss.ssmusic.datasource.netbean

data class KMusicHotSongBean(
    var listenerCount: String,
    var musicName: String,//歌曲名称
    var detailUrl: String,//详细地址获取播放地址
    var imgUrl: String,//头像
    var type: Int
)
