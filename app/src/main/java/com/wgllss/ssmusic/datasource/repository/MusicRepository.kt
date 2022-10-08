package com.wgllss.ssmusic.datasource.repository

import com.google.gson.Gson
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.net.MusiceApi
import com.wgllss.ssmusic.core.units.ChineseUtils
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.data.MusicBean
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

class MusicRepository @Inject constructor(val musiceApiL: Lazy<MusiceApi>) {

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

    suspend fun getPlayUrl(htmlUrl: String): Flow<MusicBean> {
        return flow {
            val startTime = System.currentTimeMillis()
            val html = musiceApiL.get().getPlayUrl(htmlUrl)
            val baseUrl = "https://www.hifini.com/"
            val document = Jsoup.parse(html, baseUrl)
            val element = document.select("script")
            element?.forEach {
                if (it.html().contains("var ap4 = new APlayer")) {
                    val str = it.html()
                    var startIndex = str.indexOf("[")
                    val endIndex = str.lastIndexOf("]")
                    var subStr = str.substring(startIndex + 1, endIndex - 2).trim()
                    subStr = subStr.replace("{", "{\"")
                        .replace(": '", "\":\"")
                        .replace(":'", "\":\"")
                        .replace("',", "\",\"")
                        .replace("'", "\"")
                        .replace("},", "}")
                        .replace("\n", "")
                        .replace("\r", "")
                        .replace("\t", "")
                        .replace(" ", "")
                    WLog.e(this@MusicRepository, "${System.currentTimeMillis() - startTime}ms  ___: ${subStr}")
                    val musicBean = Gson().fromJson(subStr, MusicBean::class.java)
                    musicBean.run {
                        if (!url.contains("http")) {
                            url = baseUrl + url
                        }
                    }
                    emit(musicBean)
                    return@forEach
                }
            }
        }
    }
}