package com.wgllss.ssmusic.features_system.startup.pluginloader

import android.content.Context
import com.wgllss.dynamic.host.library.DataSourceManagerImplLoader
import com.wgllss.dynamic.host.library.DataSourcePluginMangerImpl
import com.wgllss.music.datasourcelibrary.data.MusicBean
import com.wgllss.music.datasourcelibrary.data.MusicItemBean
import com.wgllss.music.datasourcelibrary.datasource.MusicSourceManager
import com.wgllss.ssmusic.core.units.WLog

class DynamicDataSourcePluginManager constructor(private val context: Context, private val dexPath: String) : MusicSourceManager {

    private lateinit var mDataSourcePluginMangerImpl: DataSourcePluginMangerImpl

    override suspend fun getPlayUrl(htmlUrl: String): MusicBean? {
        updatePluginManagerImpl()
        return mDataSourcePluginMangerImpl.getPlayUrl(htmlUrl)
    }

    override suspend fun searchByTitle(keyword: String): MutableList<MusicItemBean> {
        updatePluginManagerImpl()
        return mDataSourcePluginMangerImpl.searchByTitle(keyword)
    }

    private fun updatePluginManagerImpl() {
        if (!this::mDataSourcePluginMangerImpl.isInitialized) {
            mDataSourcePluginMangerImpl = DataSourceManagerImplLoader(context, dexPath).load()
        }
    }


}