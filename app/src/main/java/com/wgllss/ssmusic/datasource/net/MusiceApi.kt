package com.wgllss.ssmusic.datasource.net

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface MusiceApi {

    /**
     * 通过标题搜索
     */
    @GET("https://www.hifini.com/search-{keyword}-1.htm")
    suspend fun searchKeyByTitle(@Path("keyword") keyword: String): String

    @GET
    suspend fun getPlayUrl(@Url url: String): String

    @GET
    suspend fun getMusicFileUrl(@Url url: String): Response<ResponseBody>
}