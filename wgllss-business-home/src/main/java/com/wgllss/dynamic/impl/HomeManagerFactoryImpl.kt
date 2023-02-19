package com.wgllss.dynamic.impl

import android.content.Context
import com.wgllss.dynamic.sample.SampleHomePluginManagerImpl
import com.wgllss.plugin.library.HomeManagerFactory
import com.wgllss.plugin.library.HomePluginManagerImpl

class HomeManagerFactoryImpl : HomeManagerFactory {
    override fun buildManager(context: Context): HomePluginManagerImpl {
        return SampleHomePluginManagerImpl()
    }
}