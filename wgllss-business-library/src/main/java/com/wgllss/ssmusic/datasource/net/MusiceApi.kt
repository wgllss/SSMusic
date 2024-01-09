package com.wgllss.ssmusic.datasource.net

import com.wgllss.ssmusic.datasource.netbean.lrc.KLrcDto
import com.wgllss.ssmusic.datasource.netbean.mv.KMVDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * 首页: https://www.hifini.com/
 *    最新： https://www.hifini.com/index.htm
 *    热门： https://www.hifini.com/index-0-2.htm
 *    月榜： https://www.hifini.com/index-0-3.htm
 *    周榜： https://www.hifini.com/index-0-4.htm
 *    日榜： https://www.hifini.com/index-0-5.htm
 *
 * 华语: https://www.hifini.com/forum-1.htm
 * 日韩: https://www.hifini.com/forum-15.htm
 * 欧美: https://www.hifini.com/forum-10.htm
 * remix: https://www.hifini.com/forum-11.htm
 * 纯音乐: https://www.hifini.com/forum-12.htm
 * 异次元: https://www.hifini.com/forum-13.htm
 */
interface MusiceApi {

//    @GET("https://www.hifini.com/index{tab_item}.htm")
//    suspend fun homeMusic(@Path("tab_item") tab_item: String = ""): String


    @GET("https://www.hifini.com/{tab_item}.htm")
    suspend fun homeTabMusic(@Path("tab_item") tab_item: String = ""): String

    /**
     * 通过标题搜索
     */
    @GET("https://www.hifini.com/search-{keyword}-1-{pageNo}.htm")
    suspend fun searchKeyByTitle(@Path("keyword") keyword: String, @Path("pageNo") pageNo: Int): String

    @GET
    suspend fun getPlayUrl(@Url url: String): String

    @GET
    suspend fun getMusicFileUrl(@Url url: String): Response<ResponseBody>

    @GET
    suspend fun getKLrcJson(@Url url: String): KLrcDto

    @GET
    suspend fun getMvData(@Url url: String): KMVDto

    @GET("https://gitee.com/wgllss888/WeexDemo/raw/develop-wg/assets/0000_Activation/{path}.txt")
    suspend fun checkActivation(@Path("path") path: String): String
}