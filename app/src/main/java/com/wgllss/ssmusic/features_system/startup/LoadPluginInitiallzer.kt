package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.ssmusic.core.units.FileUtils
import com.wgllss.ssmusic.features_system.app.DynamicDataSourcePluginManagerUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadPluginInitiallzer : Initializer<Unit> {

    override fun create(context: Context) {
        GlobalScope.launch {
            DynamicDataSourcePluginManagerUser.getInstance(context, FileUtils.getSDPathByRootFile("classes2_dex.jar"))
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}