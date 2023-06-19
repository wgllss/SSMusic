package com.wgllss.ssmusic.datasource.netbean.rank

class KRankExBean(
    imgUrl: String,
    linkUrl: String,
    val topBean: MutableList<KTopBean>
) : KRankBean(imgUrl, linkUrl)