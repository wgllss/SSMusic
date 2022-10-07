package com.wgllss.annotations

data class Destination(
    val isFragment: Boolean,
    val asStarter: Boolean,
    val needLogin: Boolean,
    val pageUrl: String,
    val className: String,
    val id: Int,
    val label: String,
    val iconId: Int
)
