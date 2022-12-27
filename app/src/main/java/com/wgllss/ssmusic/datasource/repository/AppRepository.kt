package com.wgllss.ssmusic.datasource.repository

import androidx.lifecycle.LiveData
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.music.datasourcelibrary.data.MusicBean
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepository @Inject constructor(private val mSSDataBaseL: Lazy<SSDataBase>) {

    suspend fun getMusicList(): Flow<LiveData<MutableList<MusicTabeBean>>> {
        return flow {
            emit(mSSDataBaseL.get().musicDao().getList())
        }
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(mediaID: String, htmlUrl: String, title: String = "", author: String = "", pic: String = "") = flow {
        DynamicDataSourcePluginManagerUser.getDataSourcePluginManager()?.run {
            val url = getCacheMediaID(mediaID)
            if (url == null) {
                getPlayUrl(htmlUrl)?.let {
                    emit(it)
                }
            } else {
                WLog.e(this@AppRepository, "拿到缓存: $title")
                val musicBean = MusicBean(title, author, url, pic)
                musicBean.requestRealUrl = htmlUrl
                emit(musicBean)
            }
        }
    }
}