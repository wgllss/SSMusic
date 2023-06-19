package com.wgllss.ssmusic.datasource.netbean.mv

data class KMVBean(
    val downurl: String,
    val backupdownurl: MutableList<String>,
    val hash: String,
    val bitrate: String,
    val filesize: String
)
