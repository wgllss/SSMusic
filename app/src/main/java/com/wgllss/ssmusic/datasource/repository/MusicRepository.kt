package com.wgllss.ssmusic.datasource.repository

import com.wgllss.music.datasourcelibrary.data.MusicBean
import com.wgllss.music.datasourcelibrary.data.MusicItemBean
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.features_system.app.DynamicDataSourcePluginManagerUser
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import javax.inject.Inject

class MusicRepository @Inject constructor(private val mSSDataBaseL: Lazy<SSDataBase>) {

    /**
     * 按照标题搜索
     */
    suspend fun searchKeyByTitle(keyword: String): Flow<MutableList<MusicItemBean>> = flow {
        emit(DynamicDataSourcePluginManagerUser.getDataSourcePluginManager().searchByTitle(keyword))
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(htmlUrl: String): Flow<MusicBean> = flow {
        DynamicDataSourcePluginManagerUser.getDataSourcePluginManager().getPlayUrl(htmlUrl)?.let {
            emit(it)
        }
    }

    suspend fun addToPlayList(it: MusicBean): Flow<Long> {
        return flow {
            it.run {
                WLog.e(this@MusicRepository, "addToPlayList id: ${it.id} requestRealUrl $requestRealUrl")
                val count = mSSDataBaseL.get().musicDao().queryByUUID(it.id)
                if (count > 0) {
                    WLog.e(this@MusicRepository, "已经在播放列表里面")
                } else {
                    val bean = MusicTabeBean(it.id, title, author, requestRealUrl, pic, System.currentTimeMillis())
                    mSSDataBaseL.get().musicDao().insertMusicBean(bean)
                }
                emit(it.id)
            }
        }.catch { it.printStackTrace() }.flowOn(Dispatchers.IO)
    }

    suspend fun deledeFromId(id: Long) = flow {
        WLog.e(this, "deledeFromId id: $id")
        mSSDataBaseL.get().musicDao().deleteFromID(id)
        emit(0)
    }
}