package com.wgllss.ssmusic.datasource.net

import retrofit2.http.GET
import retrofit2.http.Path

interface MusiceApi {

    /**
     * 通过标题搜索
     */
    @GET("https://www.hifini.com/search-{keyword}-1.htm")
    suspend fun searchKeyByTitle(@Path("keyword") keyword: String): String
}