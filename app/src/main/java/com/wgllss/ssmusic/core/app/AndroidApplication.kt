package com.wgllss.ssmusic.core.app

import android.app.Application
import android.content.Context
import com.wgllss.ssmusic.core.units.AppGlobals

open class AndroidApplication : Application() {

    private val proxies = listOf<ApplicationProxy>(CommonApplicationProxy)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        AppGlobals.sApplication = this
    }

    override fun onCreate() {
        super.onCreate()
        proxies.forEach { it.onCreate(this) }
    }
}