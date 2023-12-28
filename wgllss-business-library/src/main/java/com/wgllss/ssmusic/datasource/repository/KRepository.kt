package com.wgllss.ssmusic.datasource.repository

import android.content.Context
import android.text.TextUtils
import android.webkit.*
import com.google.gson.Gson
import com.wgllss.core.units.WLog
import com.wgllss.ssmusic.data.*
import com.wgllss.ssmusic.datasource.net.KMusicApi
import com.wgllss.ssmusic.datasource.net.RetrofitUtils
import com.wgllss.ssmusic.datasource.netbean.KMJson
import com.wgllss.ssmusic.datasource.netbean.KMusicHotSongBean
import com.wgllss.ssmusic.datasource.netbean.KMusicInfoBean
import com.wgllss.ssmusic.datasource.netbean.KSingerBean
import com.wgllss.ssmusic.datasource.netbean.mv.KMVDto
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem
import com.wgllss.ssmusic.datasource.netbean.rank.KRankBean
import com.wgllss.ssmusic.datasource.netbean.rank.KRankExBean
import com.wgllss.ssmusic.datasource.netbean.rank.KTopBean
import com.wgllss.ssmusic.datasource.netbean.search.KGSearchBean
import com.wgllss.ssmusic.datasource.netbean.search.KGSearchDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetListDtoPlistListItem
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerItem
import com.wgllss.ssmusic.features_system.music.music_web.ImplWebViewClient
import com.wgllss.ssmusic.features_system.music.music_web.LrcHelp
import com.wgllss.ssmusic.features_system.savestatus.MMKVHelp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import java.lang.Exception
import java.util.concurrent.TimeoutException

class KRepository private constructor(private val context: Context) {
    private val baseUrl = "https://m.kugou.com/"
    private val musiceApiL by lazy { RetrofitUtils.getInstance(context).create(KMusicApi::class.java) }

    companion object {
        @Volatile
        private var instance: KRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: KRepository(context).also { instance = it }
        }
    }

    /**
     * 首页
     */
    suspend fun homeKMusic(): Flow<MutableList<HomeItemBean>> {
        return flow {
            val html = musiceApiL.homeKMusic()
            val document = Jsoup.parse(html, baseUrl)
            val dcS = document.select(".m_cm_i1w_d1_d0")
            //m_cm_i2w_d1_d1
            val list = mutableListOf<HomeItemBean>()
            val listNew = mutableListOf<MusicItemBean>()
            val listHot = mutableListOf<KMusicHotSongBean>()
//            log("size ${dcS.size}")
            dcS.forEach {
                val links = it.select("a[href]")
                val url = links?.first()?.attr("abs:href")!!
                val imgTag = it.select("img[_src]")
                val img = imgTag.first()?.attr("abs:_src")!!
                if (url.contains("/mixsong/")) {
                    //todo 新歌首发
                    val musicName = it.select(".m_cm_i1w_d1_a2_p1").first()?.html()
//                    log("musicName ###### :$musicName")
                    val author = it.select(".m_cm_i1w_d1_a2_p2").first()?.html()
                    if (musicName != null && author != null) {
                        listNew.add(MusicItemBean(author, musicName, url, "", img, dataSourceType = 1))
//                        log("url:$url img:$img musicName:$musicName author:$author")
                    }
                }
                if (url.contains("/plist/list/")) {
                    //todo 热门歌单
                    val listenerCount = it.select(".m_cm_i1w_d1_a1_d1").html()
                    val musicName = it.select(".m_cm_i1w_d1_a2_p3").first()?.html()
//                    log("musicName ###### :$musicName listenerCount $listenerCount")
                    musicName?.let {
                        listHot.add(KMusicHotSongBean(listenerCount, musicName, url, img, 1))
                    }
                }
            }
            list.add(HomeItemBean(0, HomeLableBean("新歌首发")))
            listNew.forEach {
                list.add(HomeItemBean(1, kMusicItemBean = it))
            }
            list.add(HomeItemBean(0, HomeLableBean("热门歌单")))
            listHot?.forEach {
                list.add(HomeItemBean(2, kKMusicHotSongBean = it))
            }
            list.add(HomeItemBean(0, HomeLableBean("热歌榜单")))

            val rankList = mutableListOf<KRankExBean>()
            val dcSHref = document.select(".m_cm_i2w_d1")
            //todo 排行榜等级
            dcSHref?.forEach {
                val links = it.select("a[href]")
                val url = links?.first()?.attr("abs:href")!!
                val imgTag = it.select("img[_src]")
                val img = imgTag.first()?.attr("abs:_src")!!
//                log("rank img $img")
                val topBean = mutableListOf<KTopBean>()
                val kRankBean = KRankExBean(img, url, topBean)
                val rank = it.select(".m_cm_i2w_d1_d1_a1")
                rank?.forEach { ritem ->
                    val no = ritem?.select(".m_cm_i2w_d1_d1_a1_sp1")?.first()?.html() ?: ""
                    val name = ritem?.select(".m_cm_i2w_d1_d1_a1_sp2")?.html() ?: ""
                    val author = ritem?.select(".m_cm_i2w_d1_d1_a1_sp1")?.next()?.next()?.html() ?: ""
//                    log("no:$no name:$name author:$author")
                    val ktopBean = KTopBean(no, name, author)
                    topBean.add(ktopBean)
                }

                rankList.add(kRankBean)
            }
            rankList?.forEach {
                list.add(HomeItemBean(3, kRankExBean = it))
            }
            val hotSingers = document.select(".singer")
            //todo 热门歌手
            val singers = mutableListOf<KSingerBean>()
            hotSingers.forEach {
                val links = it.select("a[href]")
                val url = links?.first()?.attr("abs:href")!!
                val imgTag = it.select("img[_src]")
                val img = imgTag.first()?.attr("abs:_src")!!
//                log("singer img $img url $url")
                val name = it.select("span").html()
                var encodeID = url.substring(0, url.length - 1)
                encodeID = encodeID.substring(encodeID.lastIndexOf("/") + 1, encodeID.length)
//                log("name $name")
                singers.add(KSingerBean(name, url, img, encodeID))
            }
            list.add(HomeItemBean(0, HomeLableBean("热门歌手")))
            singers?.forEach {
                list.add(HomeItemBean(4, kSingerBean = it))
            }
//            list.add(0, HomeItemBean(5, kMenuBean = KMenuBean(1, "经典")))
//            list.add(1, HomeItemBean(5, kMenuBean = KMenuBean(2, "最新")))
//            list.add(2, HomeItemBean(5, kMenuBean = KMenuBean(3, "新歌")))
            list.add(0, HomeItemBean(5))
            emit(list)
            MMKVHelp.saveHomeTab1Data(Gson().toJson(list))
        }.catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
    }

    private fun log(message: String) {
        android.util.Log.e("KuGouRepository", message)
    }

    private fun loadWebViewUrl(url: String, implWeb: ImplWebViewClient, isUserAgentPC: Boolean = false) {
        val userAgent: String = if (isUserAgentPC) "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0" else ""
        WebView(context).apply {
            settings.apply {
                defaultTextEncodingName = "UTF-8"
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                javaScriptEnabled = true
                domStorageEnabled = true
                webViewClient = implWeb
                if (isUserAgentPC)
                    userAgentString = userAgent
            }
            loadUrl(url)
        }
    }

    /**
     * 获取歌词
     */
    suspend fun getMusicInfo(musicItemBean: MusicItemBean, isOnlyGeLRc: Boolean = false): Flow<MusicBean> {
        val implWeb = ImplWebViewClient()
        loadWebViewUrl(musicItemBean.detailUrl, implWeb)
        return flow {
            log("mp3html url:${musicItemBean.detailUrl}")
            var musicFileUrl = ""
            if (!isOnlyGeLRc) {
                val startTime = System.currentTimeMillis()
                while (TextUtils.isEmpty(implWeb.getMusicFileUrl().also {
                        musicFileUrl = it
                    })) {
                    delay(16)
                    if (System.currentTimeMillis() - startTime > 15000) {
                        throw TimeoutException("获取播放链接超时,请重试")
                    }
                }
            }
            var lrcUrl = implWeb.getMusicLrcUrl()
            if (isOnlyGeLRc) {
                val startTime = System.currentTimeMillis()
                while (TextUtils.isEmpty(lrcUrl)) {
                    delay(16)
                    lrcUrl = implWeb.getMusicLrcUrl()
                    if (System.currentTimeMillis() - startTime > 15000) {
                        break
                    }
                }
            }
            log("lrcUrl 00000 :${lrcUrl}")
            var lrcStr = ""
            var sTdMusicUrl = implWeb.getSTdMusicUrl()
            if (!TextUtils.isEmpty(lrcUrl)) {
                val kLrcDto = musiceApiL.getKLrcJson(lrcUrl)
                log("kLrcDto data lrc : ${kLrcDto.data?.lrc}")
                kLrcDto?.takeIf {
                    it.status == 1
                }?.let {
                    lrcStr = it.data?.lrc ?: ""
                    lrcStr = lrcStr.replace("\r", "")
                }
            }
            musicItemBean.run {
                val musicBean = MusicBean(musicName, author, musicFileUrl, album_sizable_cover.ifEmpty { sTdMusicUrl }, dataSourceType, privilege, mvhash).apply {
                    requestRealUrl = detailUrl
                    musicLrcStr = lrcStr
                }
                emit(musicBean)
            }
        }
    }

    /**
     * 新歌
     */
    suspend fun homeKNewList(url: String) =
        flow {
            val html = musiceApiL.homeKNewList(url)
            val document = Jsoup.parse(html, baseUrl)
            val ahrefList = document.select(".panel-songslist")
            val links = ahrefList.first()?.select("a[href]")
            val listNew = mutableListOf<MusicItemBean>()
            links?.forEach {
                val url = it.attr("abs:href")!!
                val musicName = it.select(".song_name").html()
                val author = it.select(".singer_name").html()
                val jsonTag = it.select(".panel-songs-item-download")
                val json = jsonTag.select("em").html()
//                    .replace("<", "")
//                    .replace(">", "")
                try {
                    val kMJson = Gson().fromJson(json, KMJson::class.java)
                    kMJson?.takeIf { k ->
                        k.privilege == 8
                    }?.run {
//                        val img = authors[0].sizable_avatar
                        val img = album_sizable_cover
                            .replace("\\", "")
                            .replace("{size}", "100")
                        listNew.add(MusicItemBean(author, musicName, url, "", img, mvhash = mvhash, dataSourceType = 1, privilege = privilege))
                    }
                } catch (e: Exception) {
                    log("musicName:$musicName ")
                }
            }
            emit(listNew)
        }

    /**
     * 歌单
     */
//    suspend fun homeKSongSheet() = flow {
//        val html = musiceApiL.homeKSongSheet()
//        val document = Jsoup.parse(html, baseUrl)
//        val ahrefList = document.select(".panel-img-list")
//        val links = ahrefList.first()?.select("a[href]")
//        val listHot = mutableListOf<KSheetListDtoPlistListItem>()
//        links?.forEach {
//            val url = it.attr("abs:href")!!
//            val imgTag = it.select("img[_src]")
//            val img = imgTag.first()?.attr("abs:_src")!!
//            val name = it.select(".panel-img-content-first").html()
////            var encodeID = url.substring(0, url.length)
////            val encodeID = url.substring(url.lastIndexOf("/") + 1, url.length)
//            listHot.add(KSheetListDtoPlistListItem(name, url, img))
//        }
//        emit(listHot)
//    }

    suspend fun homeKSongSheetLoadMore(page: Int) = flow {
        val data = musiceApiL.homeKSongSheetLoadMore(page)?.apply {
            plist?.list?.info?.forEach {
                val img = it.imgurl
                    .replace("\\", "")
                    .replace("{size}", "400")
                it.imgurl = img
            }
        }
        emit(data)
    }

    suspend fun kSongSheetDetail1(encodeID: String) = flow {
        val requestUrl = if (encodeID.contains("https://")) "$encodeID?json=true" else "https://m.kugou.com/plist/list/$encodeID/?json=true"
        val dto = musiceApiL.kSongSheetDetail1(requestUrl)
        dto?.run {
            val listSong = mutableListOf<MusicItemBean>()
            list?.list?.info?.forEach {
                it?.run {
                    val index = it.filename.indexOf("-")
                    it.author_name = it.filename.substring(0, index)
                    it.songname = it.filename.substring(index + 1, it.filename.length)
                    it.album_sizable_cover = ""
                    listSong.add(MusicItemBean(it.filename.substring(0, index), it.filename.substring(index + 1, it.filename.length), it.song_url, "", "", it.mvhash, 1, it.privilege))
                }
            }
            list = null
            listData = listSong
            info?.list?.run {
                imgurl = imgurl.replace("{size}", "400")
                user_avatar = user_avatar.replace("{size}", "400")
            }
        }
        emit(dto)
    }

    suspend fun kSongRankDetail2(encodeID: String) = flow {
        val listSong = mutableListOf<MusicItemBean>()
        val data = musiceApiL.kSongSheetDetail2("$encodeID?json").apply {
            info.img_cover = info.img_cover.replace("\\", "")
                .replace("{size}", "400")
            info.imgurl = info.imgurl.replace("\\", "")
                .replace("{size}", "400")

            songs?.list?.forEach {
                val index = it.filename.indexOf("-")
                listSong.add(MusicItemBean(it.filename.substring(0, index), it.filename.substring(index + 1, it.filename.length), it.song_url, "", it.album_sizable_cover.replace("\\", "").replace("{size}", "400"), it.mvhash, 1, it.privilege))
            }
            listData = listSong
            songs = null
        }
        emit(data)
    }

    suspend fun kRankList() = flow {
        val html = musiceApiL.kRankList()
        val document = Jsoup.parse(html, baseUrl)
        val cdocs = document.select(".panel-img-list")
        val links = cdocs.first()?.select("a[href]")
        val list = mutableListOf<KRankBean>()
        links?.forEach {
            val url = it.attr("abs:href")!!
            val imgTag = it.select("img[_src]")
            val img = imgTag.first()?.attr("abs:_src")!!
            list.add(KRankBean(img, url))
        }
        emit(list)
    }

    suspend fun kSingers(path: String) = flow {
        val list = mutableListOf<KSingerItem>()
        musiceApiL.kSingers(path)?.singers?.list?.info?.forEach { info ->
            val titlePosition = list.size
            info?.singer?.forEach {
                val img = it.imgurl
                    .replace("\\", "")
                    .replace("{size}", "400")
                it.title = info.title
                it.singername = it.singername.replace("S.H.E", "SHE")
                it.imgurl = img
                it.titlePosition = titlePosition
                list.add(it)
            }
        }
        emit(list)
    }

    suspend fun kSingerInfo(path: String) = flow {
        val dto = musiceApiL.kSingerInfo(path)
        dto.info.apply {
            imgurl = imgurl.replace("{size}", "400")
        }
        emit(dto)
    }

    suspend fun getMvData(url: String): Flow<KMVDto> {
        log("mv url:${url}")
        val implWeb = ImplWebViewClient()
        loadWebViewUrl(url, implWeb)
        return flow {
            var mvRequestUrl: String
            while (TextUtils.isEmpty(implWeb.getMvRequestUrl().also {
                    mvRequestUrl = it
                })) {
                delay(10)
            }
            val dto = musiceApiL.getMvData(mvRequestUrl).apply {
                val img = mvicon.replace("\\", "")
                    .replace("{size}", "400")
                mvicon = img
            }
            emit(dto)
        }
    }

    suspend fun pingDao() = flow {
        val html = musiceApiL.pingDaoList()
        val document = Jsoup.parse(html, "https://www.kugou.com/")
        val cdocs = document.select(".main")
        val links = cdocs.first()?.select("a[href]")
        val listPinDao = mutableListOf<MusicItemBean>()
        links?.forEach {
            val url = it.attr("abs:href")!!
            val imgTag = it.select("img[src]")
            val imgUrl = imgTag.first()?.attr("abs:src")!!
            val name = it.select("span").html()
//            WLog.e(this@KRepository, "url:${url} name:${name} imgUrl:${imgUrl}")
            listPinDao.add(MusicItemBean("", name, url, "", imgUrl))
        }
        emit(listPinDao)
    }

    suspend fun playPinDaoDetail(htmlUrl: String): Flow<MusicBean> {
        val implWeb = ImplWebViewClient()
        loadWebViewUrl(htmlUrl, implWeb)
        return flow {
            var kPinDaoRequestUrl: String
            val startTime = System.currentTimeMillis()
            while (TextUtils.isEmpty(implWeb.getPinDaoRequestUrl().also {
                    kPinDaoRequestUrl = it
                })) {
                delay(10)
                if (System.currentTimeMillis() - startTime > 15000) {
                    throw TimeoutException("获取播放链接超时,请重试")
                }
            }
            musiceApiL.getPinDaoDetail(kPinDaoRequestUrl)?.data?.run {
                emit(MusicBean(song_name, author_name, play_url, img, 1, privilege, "").apply {
                    requestRealUrl = "https://www.kugou.com/mixsong/${encode_album_audio_id}.html"
                    var lrcStr = lyrics.replace("\r", "")
                    val start = lrcStr.indexOf("[offset:0]")
                    lrcStr = lrcStr.substring(start + 10)//.replace("offset:0]", "00:00.00")
                    musicLrcStr = lrcStr
                })
            }
        }
    }

    suspend fun kmvList(url: String) = flow {
        val html = musiceApiL.kmvList(url)
        val document = Jsoup.parse(html, "https://www.kugou.com/")
        val cdocs = document.select(".clearfix")
        val links = cdocs.first()?.select("a[href]")
        val list = mutableListOf<KMVItem>()
        links?.forEach {
            val url = it.attr("abs:href")!!
            val imgTag = it.select("img[_src]")
            val imgUrl = imgTag.first()?.attr("abs:_src")!!
            var title = it.attr("abs:title")!!
            title = title.replace("https://www.kugou.com/", "")
                .replace("https://www.kugou.com", "")
            list.add(KMVItem(title, imgUrl, url))
        }
        emit(list)
    }

    suspend fun searchKeyWord(keyword: String): Flow<MutableList<MusicItemBean>> {
        val implWeb = ImplWebViewClient()
        val htmlUrl = "https://www.kugou.com/yy/html/search.html#searchType=song&searchKeyWord=$keyword"
        loadWebViewUrl(htmlUrl, implWeb, true)
        return flow {
            var kgSearchUrl: String
            val startTime = System.currentTimeMillis()
            while (TextUtils.isEmpty(implWeb.getSearchUrl().also {
                    kgSearchUrl = it
                })) {
                delay(10)
                if (System.currentTimeMillis() - startTime > 15000) {
                    throw TimeoutException("获取播放链接超时,请重试")
                }
            }
            val jsoup = musiceApiL.searchKeyWord(kgSearchUrl)
            val start = jsoup.indexOf("(")
            val end = jsoup.lastIndexOf(")")
            val json = jsoup.substring(start + 1, end)
            val kGSearchDto = Gson().fromJson(json, KGSearchDto::class.java)
            val list = mutableListOf<MusicItemBean>()//<MusicItemBean>()
            kGSearchDto?.data?.lists?.forEach {
                list.add(MusicItemBean(it.SingerName, it.SongName, "https://www.kugou.com/mixsong/${it.EMixSongID}.html", "", it.Image.replace("\\", "").replace("{size}", "400"), it.MvHash, 1, it.Privilege))
            }
            emit(list)
        }
    }
}