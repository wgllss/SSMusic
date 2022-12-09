package com.wgllss.ssmusic.features_system.globle

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wgllss.ssmusic.R

object Constants {
    const val MEDIA_ID_ROOT = "-1"

    const val MEDIA_TITLE_KEY = "MEDIA_TITLE_KEY"
    const val MEDIA_ID_KEY = "MEDIA_ID_KEY"
    const val MEDIA_AUTHOR_KEY = "MEDIA_AUTHOR_KEY"
    const val MEDIA_ARTNETWORK_URL_KEY = "MEDIA_ARTNETWORK_URL_KEY"
    const val MEDIA_URL_KEY = "MEDIA_URL_KEY"

    const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px
    val glideOptions = RequestOptions().fallback(R.drawable.loading_logo).diskCacheStrategy(DiskCacheStrategy.DATA)

    //单曲循环
    const val MODE_PLAY_REPEAT_SONG = 2

    //随机模式
    const val MODE_PLAY_SHUFFLE_ALL = 1

    //顺序播放重复队列
    const val MODE_PLAY_REPEAT_QUEUE = 0
}