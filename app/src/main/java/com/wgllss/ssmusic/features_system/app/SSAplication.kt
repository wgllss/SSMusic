package com.wgllss.ssmusic.features_system.app

import android.content.Context
import com.umeng.commonsdk.UMConfigure
import com.wgllss.ssmusic.core.app.AndroidApplication
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.third.um.UMHelp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltAndroidApp
class SSAplication : AndroidApplication() {

    override fun attachBaseContext(base: Context?) {
        LogTimer.initTime(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        UMHelp.umInit(this@SSAplication)
    }

}