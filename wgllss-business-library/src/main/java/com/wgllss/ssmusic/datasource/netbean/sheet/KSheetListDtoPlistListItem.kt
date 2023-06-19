package com.wgllss.ssmusic.datasource.netbean.sheet

data class KSheetListDtoPlistListItem(
    val specialname: String,
    val encode_id: String,
    var imgurl: String,
    val play_count_text: String = "",
    val viewType: Int = 0
)
