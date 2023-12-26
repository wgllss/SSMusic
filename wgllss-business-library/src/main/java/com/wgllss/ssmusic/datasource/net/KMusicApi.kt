package com.wgllss.ssmusic.datasource.net

import com.wgllss.ssmusic.datasource.netbean.lrc.KLrcDto
import com.wgllss.ssmusic.datasource.netbean.mv.KMVDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KRankSheetDetailDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetDetailDto
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetListDto
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerDetailDto
import com.wgllss.ssmusic.datasource.netbean.singer.KSingersDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface KMusicApi {

    //    @GET("https://m.kugou.com/?json=true")  298k
    @GET("https://m.kugou.com/")  //25.17k
    suspend fun homeKMusic(): String

    //https://m.kugou.com/newsong/index?json=true  //196k  else 332k
    @GET
    suspend fun homeKNewList(@Url url: String): String

    @GET("https://www.kugou.com/mvweb/html/index_{path}.html") //只有html 无json
    suspend fun kmvList(@Path("path") path: String): String

    @GET("https://www.kugou.com/fmweb/html/index.html") //只有html 无json
    suspend fun pingDaoList(): String

    @GET("https://m.kugou.com/plist/index/")   //36.38k
    //https://m.kugou.com/plist/index?page=1&json=true   // 137.65k
    //https://m.kugou.com/plist/index?page=4&json=true
//    @GET("https://m.kugou.com/plist/index?json=true")
    suspend fun homeKSongSheet(): String

    @GET("https://m.kugou.com/plist/index?json=true")
    suspend fun homeKSongSheetLoadMore(@Query("page") page: Int): KSheetListDto

    //https://m.kugou.com/plist/list/2bos1e6/?json=true 13.9k
    //https://m.kugou.com/plist/list/2bos1e6/  46.7k

    @GET //("https://m.kugou.com/plist/list/{path}/")
    suspend fun kSongSheetDetail1(@Url url: String): KSheetDetailDto
    // @GET //("https://m.kugou.com/plist/list/{path}/")
//    suspend fun kSongSheetDetail(@Url url: String): String

    @GET //("https://m.kugou.com/rank/info/{path}/?json")
    suspend fun kSongSheetDetail2(@Url url: String): KRankSheetDetailDto


//    @GET
//    suspend fun kSongSheetDetail2(@Url url: String): KSheetDetailDto

    //http://m.kugou.com/rank/list&json=true  //79.6k
    @GET("https://m.kugou.com/rank/list/")  // 20.8k
    suspend fun kRankList(): String

    @GET("https://m.kugou.com/singer/list/{path}?json=true")
    suspend fun kSingers(@Path("path") path: String): KSingersDto

    @GET("https://m.kugou.com/singer/info/{path}/?json=true")
    suspend fun kSingerInfo(@Path("path") path: String): KSingerDetailDto

    //https://m.kugou.com/singer/list/1?json=true 华语歌手
    //https://m.kugou.com/singer/list/6?json=true 日韩歌手
    //https://m.kugou.com/singer/list/5?json=true 日本歌手
    //https://m.kugou.com/singer/list/2?json=true 欧美歌手
    //https://m.kugou.com/singer/list/4?json=true 其他
    //https://m.kugou.com/singer/list?json=true 音乐人


//    @GET("https://www.hifini.com/{tab_item}.htm")
//    suspend fun homeTabMusic(@Path("tab_item") tab_item: String = ""): String
//
//    /**
//     * 通过标题搜索
//     */
//    @GET("https://www.hifini.com/search-{keyword}-1.htm")
//    suspend fun searchKeyByTitle(@Path("keyword") keyword: String): String
//
//    @GET
//    suspend fun getPlayUrl(@Url url: String): String

    @GET
    suspend fun getKLrcJson(@Url url: String): KLrcDto

    @GET
    suspend fun getMvData(@Url url: String): KMVDto
}