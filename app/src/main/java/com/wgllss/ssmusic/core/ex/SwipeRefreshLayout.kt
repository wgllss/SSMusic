package com.wgllss.ssmusic.core.ex

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.music.skin.R

fun SwipeRefreshLayout.initColors() {
    setColorSchemeResources(
        R.color.purple_500, android.R.color.holo_red_light,
        android.R.color.holo_orange_light, android.R.color.holo_green_light
    )
}