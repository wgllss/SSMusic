package com.wgllss.ssmusic.datasource.repository

import androidx.lifecycle.LiveData
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.UUIDHelp
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.livedatabus.MusicBeanEvent
import com.wgllss.ssmusic.datasource.net.MusiceApi
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import javax.inject.Inject

class AppRepository @Inject constructor(private val musiceApiL: Lazy<MusiceApi>, private val mSSDataBaseL: Lazy<SSDataBase>) {

    suspend fun getMusicList(): Flow<LiveData<MutableList<MusicTabeBean>>> {
        return flow {
            emit(mSSDataBaseL.get().musicDao().getList())
        }
    }

//    suspend fun addToPlayList(it: MusicBeanEvent): Flow<Long> {
//        return flow {
//            it.run {
//                val uuID = UUIDHelp.getMusicUUID(this)
//                MMKVHelp.setPlayID(uuID)
//                //uuID： 1519754784   uuid： 0
//                //uuID： 1529454536   uuid： 1519754784
//                logE("uuID： $uuID   uuid： $uuid")
//                if (uuid == 0L) {
//                    val count = mSSDataBaseL.get().musicDao().queryByUUID(uuID)
//                    if (count > 0) {
//                        logE("已经在播放列表里面")
//                    } else {
//                        val bean = MusicTabeBean(uuID, title, author, requestRealUrl, pic, System.currentTimeMillis())
//                        mSSDataBaseL.get().musicDao().insertMusicBean(bean)
//                    }
//                    emit(uuID)
//                }
//            }
//        }.catch { it.printStackTrace() }.flowOn(Dispatchers.IO)
//    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(htmlUrl: String) = flow {
        val startTime = System.currentTimeMillis()
        val html = musiceApiL.get().getPlayUrl(htmlUrl)
        val baseUrl = "https://www.hifini.com/"
        val document = Jsoup.parse(html, baseUrl)
        val element = document.select("script")
        element?.forEach {
            if (it.html().contains("var ap4 = new APlayer")) {
                val str = it.html().toString()
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                var startIndex = str.indexOf("[")
                val endIndex = str.lastIndexOf("]")
                var subStr = str.substring(startIndex + 1, endIndex).trim()
                var startIndex2 = subStr.indexOf("{")
                val endIndex2 = subStr.lastIndexOf("}")
                var subStr2 = subStr.substring(startIndex2 + 1, endIndex2).trim()

                val arr = subStr2.split(", ")
                var title = ""
                var author = ""
                var url = ""
                var pic = ""
                arr.forEach { m ->
                    m.run {
                        if (contains("title")) {
                            title = substring(indexOf("'") + 1, length - 1)
                        }
                        if (contains("author")) {
                            author = substring(indexOf("'") + 1, length - 1)
                        }
                        if (contains("url")) {
                            url = substring(indexOf("'") + 1, length - 1)
                            if (!url.contains("http")) {
                                url = baseUrl + url
                            }
                        }
                        if (contains("pic")) {
                            pic = substring(indexOf("'") + 1, length)
                        }
                    }
                }
                WLog.e(this@AppRepository, "耗时：${System.currentTimeMillis() - startTime} ms")
                emit(MusicBean(title, author, url, pic))
                return@forEach
            }
        }
    }.transform {
        it.takeIf {
            it.url.isNotEmpty()
        }?.let {
            it.requestRealUrl = it.url
            musiceApiL.get().getMusicFileUrl(it.url)?.raw()?.request?.url?.run {
                it.url = this@run.toString().replace("http://", "https://")
            }
        }
        emit(it)
    }
}