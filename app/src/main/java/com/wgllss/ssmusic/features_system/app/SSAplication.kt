package com.wgllss.ssmusic.features_system.app

import android.content.Context
import android.os.Debug
import com.wgllss.ssmusic.core.app.AndroidApplication
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.features_third.um.UMHelp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SSAplication : AndroidApplication() {

    override fun attachBaseContext(base: Context?) {
        LogTimer.initTime(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
//        UMHelp.umInit(this@SSAplication)
    }

}