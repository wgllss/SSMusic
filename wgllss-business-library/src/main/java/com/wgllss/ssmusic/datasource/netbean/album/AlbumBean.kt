package com.wgllss.ssmusic.datasource.netbean.album

data class AlbumBean(
    val albumName: String, //专辑名称
    val albumCreateTime: String, //发行时间
    val detailUrl: String, // 跳转到专辑详情url
    val albumImage: String,//专辑图片
    val company: String, //唱片公司
    val viewType: Int = 0
)
