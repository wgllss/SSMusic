package com.wgllss.ssmusic.features_system.app

import android.content.Context
import com.wgllss.ssmusic.core.app.AndroidApplication
import com.wgllss.ssmusic.core.units.LogTimer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SSAplication : AndroidApplication() {

    override fun attachBaseContext(base: Context?) {
        LogTimer.initTime(this)
        super.attachBaseContext(base)
    }

}