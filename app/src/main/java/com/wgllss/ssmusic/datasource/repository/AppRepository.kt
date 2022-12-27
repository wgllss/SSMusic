package com.wgllss.ssmusic.datasource.repository

import androidx.lifecycle.LiveData
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.music.datasourcelibrary.data.MusicBean
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.features_system.music.MusicCachePlayUrl
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepository @Inject constructor(private val mSSDataBaseL: Lazy<SSDataBase>, private val cache: Lazy<MusicCachePlayUrl>) {

    suspend fun getMusicList(): Flow<LiveData<MutableList<MusicTabeBean>>> {
        return flow {
            emit(mSSDataBaseL.get().musicDao().getList())
        }
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(mediaID: String, htmlUrl: String, title: String = "", author: String = "", pic: String = ""): Flow<MusicBean> {
        cache.get()?.get(mediaID)?.let {
            return flow {
                WLog.e(this@AppRepository, "拿到缓存: $title")
                val musicBean = MusicBean(title, author, it, pic)
                musicBean.requestRealUrl = htmlUrl
                emit(musicBean)
            }
        }
        return flow {
            DynamicDataSourcePluginManagerUser.getDataSourcePluginManager().getPlayUrl(htmlUrl)?.let {
                emit(it)
            }
        }
    }
}