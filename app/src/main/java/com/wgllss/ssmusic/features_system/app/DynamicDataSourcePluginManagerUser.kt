package com.wgllss.ssmusic.features_system.app

import android.content.Context
import com.wgllss.ssmusic.features_system.startup.pluginloader.DynamicDataSourcePluginManager

class DynamicDataSourcePluginManagerUser private constructor(private val context: Context, private val dexJarPath: String) {

    private val pluginManagerImpl by lazy { DynamicDataSourcePluginManager(context, dexJarPath) }

    companion object {
        private var instance: DynamicDataSourcePluginManagerUser? = null


        fun getInstance(context: Context, dexJarPath: String): DynamicDataSourcePluginManagerUser {
            if (instance == null) {
                synchronized(DynamicDataSourcePluginManagerUser::class) {
                    if (instance == null) {
                        instance = DynamicDataSourcePluginManagerUser(context, dexJarPath)
                    }
                }
            }
            return instance!!
        }

        fun getDataSourcePluginManager() = instance!!.pluginManagerImpl
    }


}