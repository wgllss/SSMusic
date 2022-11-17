package com.wgllss.ssmusic.core.app

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object CommonApplicationProxy : ApplicationProxy, ViewModelStoreOwner {
    lateinit var application: Application

    private var viewModelStore = ViewModelStore()

    override fun onCreate(application: Application) {
        CommonApplicationProxy.application = application
    }

    override fun onTerminate() = viewModelStore.clear()


    override fun getViewModelStore() = viewModelStore

}