package com.wgllss.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityDestination(
    val pageUrl: String,
    val needLogin: Boolean,
    val asStarter: Boolean,
    val iconId: Int
)
