package com.scclzkj.base_core.base.app

import android.app.Application

interface ApplicationProxy {
    fun onCreate(application: Application)

    fun onTerminate()
}