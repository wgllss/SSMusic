package com.wgllss.ssmusic.datasource.net

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authorised = request
            .newBuilder()
            .addHeader("Connection", "keep-alive") //
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9") //
            .addHeader("Upgrade-insecure-Requests", "1") //
        if (request.header("User-Agent") == null) {
            authorised.addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Mobile/15E148 Safari/604.1") //
        }
        return chain.proceed(authorised.build())
    }
}