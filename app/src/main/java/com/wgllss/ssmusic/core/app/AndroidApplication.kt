package com.wgllss.ssmusic.core.app

import android.app.Application
import com.scclzkj.base_core.base.app.ApplicationProxy
import com.scclzkj.base_core.base.app.CommonApplicationProxy
import com.wgllss.ssmusic.core.units.AppGlobals

open class AndroidApplication : Application() {

    private val proxies = listOf<ApplicationProxy>(CommonApplicationProxy)

    override fun onCreate() {
        super.onCreate()
        proxies.forEach { it.onCreate(this) }
        AppGlobals.sApplication = this
    }

}