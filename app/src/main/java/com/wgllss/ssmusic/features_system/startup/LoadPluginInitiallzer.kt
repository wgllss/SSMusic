package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.ssmusic.core.units.FileUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadPluginInitiallzer : Initializer<Unit> {

    override fun create(context: Context) {
        GlobalScope.launch {
            DynamicDataSourcePluginManagerUser.getInstance(context, FileUtils.getSDPathByRootFile("classes3_dex.jar"))
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}