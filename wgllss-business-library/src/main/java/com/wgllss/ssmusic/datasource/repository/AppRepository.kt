package com.wgllss.ssmusic.datasource.repository

import android.content.Context
import android.text.TextUtils
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.LiveData
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.data.MVPlayData
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.net.MusiceApi
import com.wgllss.ssmusic.datasource.net.RetrofitUtils
import com.wgllss.ssmusic.datasource.netbean.mv.KMVDto
import com.wgllss.ssmusic.features_system.music.MusicCachePlayUrl
import com.wgllss.ssmusic.features_system.music.music_web.ImplWebViewClient
import com.wgllss.ssmusic.features_system.room.SSDataBase
import com.wgllss.ssmusic.features_system.room.help.RoomDBMigration
import com.wgllss.ssmusic.features_system.room.table.MusicExtraTableBean
import com.wgllss.ssmusic.features_system.room.table.MusicTableBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException

class AppRepository private constructor(private val context: Context) {

    private val musiceApiL by lazy { RetrofitUtils.getInstance(context).create(MusiceApi::class.java) }// Lazy<MusiceApi>
    private val mSSDataBaseL by lazy { SSDataBase.getInstance(context, RoomDBMigration.instance) }
    private val cache by lazy { MusicCachePlayUrl.instance } //Lazy<MusicCachePlayUrl>

    //    private val implWeb = ImplWebViewClient()
    private val webView by lazy {
        WebView(context).apply {
            settings.apply {
                defaultTextEncodingName = "UTF-8"
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                javaScriptEnabled = true
                domStorageEnabled = true
//                webViewClient = implWeb
            }
        }
    }

    companion object {

        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: AppRepository(context).also { instance = it }
        }
    }

    suspend fun getMusicList(): Flow<LiveData<MutableList<MusicTableBean>>> {
        return flow {
            emit(mSSDataBaseL.musicDao().getList())
        }
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(mediaID: String, htmlUrl: String, title: String = "", author: String = "", pic: String = ""): Flow<MusicBean> {
        cache.get(mediaID)?.let {
            return flow {
                WLog.e(this@AppRepository, "拿到缓存: $title")
                val musicBean = MusicBean(title, author, it, pic)
                musicBean.requestRealUrl = htmlUrl
                emit(musicBean)
            }
        }
        return flow {
            WLog.e(this@AppRepository, "当前线程: ${Thread.currentThread().name}")
            val startTime = System.currentTimeMillis()
            val html = musiceApiL.getPlayUrl(htmlUrl)
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
                musiceApiL.getMusicFileUrl(it.url)?.raw()?.request?.url?.run {
                    it.url = this@run.toString().replace("http://", "https://")
                    cache.put(it.id.toString(), it.url)
                }
            }
            emit(it)
        }
    }

    fun containsKey(mediaID: String) = cache.get(mediaID)

    fun putToCache(key: String, url: String) = cache.put(key, url)

    /**
     * 获取歌词
     */
    suspend fun getMusicInfo(mediaID: String, htmlUrl: String, title: String = "", author: String = "", pic: String = "", mvhash: String): Flow<MusicBean> {
        val implWeb = ImplWebViewClient()
        webView.webViewClient = implWeb
        webView.loadUrl(htmlUrl)
        return flow {
            var musicFileUrl: String
            WLog.e(this@AppRepository, "########## 0  $title")
            val startTime = System.currentTimeMillis()
            while (TextUtils.isEmpty(implWeb.getMusicFileUrl().also {
                    musicFileUrl = it
                })) {
                delay(16)
                if (System.currentTimeMillis() - startTime > 15000) {
                    throw TimeoutException("获取播放链接超时")
                }
            }
            WLog.e(this@AppRepository, "########## 1  $title")
            val lrcUrl = implWeb.getMusicLrcUrl()
            WLog.e(this@AppRepository, "lrcUrl 00000 :${lrcUrl}")
            var lrcStr = ""
            var sTdMusicUrl = implWeb.getSTdMusicUrl()
            if (!TextUtils.isEmpty(lrcUrl)) {
                val kLrcDto = musiceApiL.getKLrcJson(lrcUrl)
                WLog.e(this@AppRepository, "kLrcDto data lrc : ${kLrcDto.data?.lrc}")
                kLrcDto?.takeIf {
                    it.status == 1
                }?.let {
                    lrcStr = it.data?.lrc ?: ""
                    lrcStr = lrcStr.replace("\r", "")
                }
            }
            val musicBean = MusicBean(title, author, musicFileUrl, pic.ifEmpty { sTdMusicUrl }, 1, 0, mvhash).apply {
                requestRealUrl = htmlUrl
                musicLrcStr = lrcStr
            }
            cache.put(mediaID, musicFileUrl)
            emit(musicBean)
        }
    }

    suspend fun getMvData(musicBean: MusicBean, mvUrl: String): Flow<MusicBean> {
        val implWeb = ImplWebViewClient()
        webView.webViewClient = implWeb
        webView.loadUrl(mvUrl)
        return flow {
            var mvRequestUrl: String
            val startTime = System.currentTimeMillis()
            while (TextUtils.isEmpty(implWeb.getMvRequestUrl().also {
                    mvRequestUrl = it
                })) {
                delay(16)
                if (System.currentTimeMillis() - startTime > 15000) {
                    throw TimeoutException("获取播放链接超时")
                }
            }
            val mvDto = musiceApiL.getMvData(mvRequestUrl)
            mvDto.run {
                val data = MVPlayData(if (mvdata.rq != null && mvdata.rq.downurl != null) mvdata.rq.downurl else mvdata.le.downurl, musicBean.title)
                musicBean.url = data.url
                musicBean.requestRealUrl = mvUrl
                cache.put(musicBean.id.toString(), data.url)
                emit(musicBean)
            }
        }
    }
}