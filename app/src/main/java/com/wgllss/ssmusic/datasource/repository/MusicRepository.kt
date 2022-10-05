package com.wgllss.ssmusic.datasource.repository

import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.net.MusiceApi
import com.wgllss.ssmusic.core.units.ChineseUtils
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
                    val startIndex = content.indexOf("《")
                    val endIndex = content.indexOf("》")
                    var author: String = content.substring(0, startIndex)
                    var samplingRate: String = content.substring(endIndex + 1)
                    var musicName = content.substring(startIndex + 1, endIndex)
                    musicName = musicName.replace("<em>", "")
                        .replace("</em>", "")
                    list.add(MusicItemBean(author, musicName, this, samplingRate))
                }
            }
            emit(list)
        }
    }
}