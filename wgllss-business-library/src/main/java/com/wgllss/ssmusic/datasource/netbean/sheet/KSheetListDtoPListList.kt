package com.wgllss.ssmusic.datasource.netbean.sheet

data class KSheetListDtoPListList(
    val has_next: Int,
    val info: MutableList<KSheetListDtoPlistListItem>,
    val total: Int
)
