package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.dynamic.host.library.DynamicPluginHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class LoadPluginInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        GlobalScope.launch {
            flow {
                DynamicPluginHelp().initDynamicPlugin(context)
                emit(0)
            }.catch { it.printStackTrace() }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}