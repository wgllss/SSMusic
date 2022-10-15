package com.wgllss.ssmusic.datasource.repository

import com.wgllss.ssmusic.core.units.ChineseUtils
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.net.MusiceApi
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class MusicRepository @Inject constructor(val musiceApiL: Lazy<MusiceApi>) {

    /**
     * 按照标题搜索
     */
    suspend fun searchKeyByTitle(keyword: String): Flow<MutableList<MusicItemBean>> {
        return flow {
            val keywordL = ChineseUtils.urlencode(keyword)
            val html = musiceApiL.get().searchKeyByTitle(keywordL)
            val document = Jsoup.parse(html, "https://www.hifini.com/")
            val dcS = document.select(".break-all")
            val list = mutableListOf<MusicItemBean>()
            dcS?.forEach {
                val links = it.select("a[href]")
                links?.first()?.attr("abs:href")?.run {
                    val content = links.html()//树深时见鹿dear《<em>三国</em><em>杀</em>》[FLAC/MP3-320K]
                    WLog.e(this@MusicRepository, "content:${content}")
                    val startIndex = content.indexOf("《")
                    val endIndex = content.indexOf("》")
                    if (startIndex != -1 && endIndex != -1) {
                        var author: String = content.substring(0, startIndex)
                        author = author.replace("&amp;", "、")
                        var samplingRate = if (content.indexOf("[") != -1) {
                            content.substring(content.indexOf("[") + 1, content.length - 1)
                        } else {
                            content.substring(endIndex + 1, content.length - 1)
                        }
                        var musicName = content.substring(startIndex + 1, endIndex)
                        musicName = musicName.replace("<em>", "")
                            .replace("</em>", "")
                        list.add(MusicItemBean(author, musicName, this, samplingRate))
                    } else {
                        val startIndex = content.indexOf("<")
                        val endIndex = content.lastIndexOf(">")
                        var author: String = content.substring(0, startIndex)
                        author = author.replace("&amp;", "、")
                        var samplingRate = if (content.indexOf("[") != -1) {
                            content.substring(content.indexOf("[") + 1, content.length - 1)
                        } else {
                            content.substring(endIndex + 1, content.length - 1)
                        }
                        var musicName = content.substring(startIndex + 1, endIndex)
                        musicName = musicName.replace("<em>", "")
                            .replace("</em>", "")
                            .replace("em>", "")
                            .replace("</em", "")
                        list.add(MusicItemBean(author, musicName, this, samplingRate))
                    }
                }
            }
            emit(list)
        }
    }

    /**
     * 得到播放地址
     */
    suspend fun getPlayUrl(htmlUrl: String): Flow<MusicBean> {
        return flow {
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
                    WLog.e(this@MusicRepository, "耗时：${System.currentTimeMillis() - startTime} ms")
                    emit(MusicBean(title, author, url, pic))
                    return@forEach
                }
            }
        }
    }
}