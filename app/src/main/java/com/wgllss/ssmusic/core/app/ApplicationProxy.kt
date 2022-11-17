package com.wgllss.ssmusic.core.app

import android.app.Application

interface ApplicationProxy {
    fun onCreate(application: Application)

    fun onTerminate()
}