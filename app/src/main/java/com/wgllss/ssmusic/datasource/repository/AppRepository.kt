package com.wgllss.ssmusic.datasource.repository

import androidx.lifecycle.LiveData
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.datasource.net.MusiceApi
import com.wgllss.ssmusic.features_system.music.MusicCachePlayUrl
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import org.jsoup.Jsoup
import javax.inject.Inject

class AppRepository @Inject constructor(private val musiceApiL: Lazy<MusiceApi>, private val mSSDataBaseL: Lazy<SSDataBase>, private val cache: Lazy<MusicCachePlayUrl>) {

    suspend fun getMusicList(): Flow<LiveData<MutableList<MusicTabeBean>>> {
        return flow {
            emit(mSSDataBaseL.get().musicDao().getList())
        }
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(mediaID: String, htmlUrl: String, title: String = "", author: String = "", pic: String = "", isQueryCache: Boolean = false): Flow<MusicBean> {
        cache.get()?.get(mediaID)?.let {
            return flow {
                logE("拿到缓存: $title")
                if (!isQueryCache) {
                    val musicBean = MusicBean(title, author, it, pic)
                    musicBean.requestRealUrl = htmlUrl
                    emit(musicBean)
                }
            }
        }
        return flow {
            logE("当前线程: ${Thread.currentThread().name}")
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
                it.requestRealUrl = htmlUrl
                musiceApiL.get().getMusicFileUrl(it.url)?.raw()?.request?.url?.run {
                    it.url = this@run.toString().replace("http://", "https://")
                    cache.get()?.put(it.id.toString(), it.url)
                }
            }
            if (!isQueryCache) {
                emit(it)
            }
        }
    }
}