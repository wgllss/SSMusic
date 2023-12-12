package com.wgllss.ssmusic.features_system.music.music_web

import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

class ImplWebViewClient : WebViewClient() {

    private var lrcUrl = ""

    private var mp3Url = ""

    private var sTdMusicUrl = ""
    private var mvRequestUrl = ""

    private val strOfflineResources by lazy { LrcHelp.getJsPath() }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url.toString()
        val lastSlash: Int = url.lastIndexOf("/")
        if (lastSlash != -1) {
            val suffix: String = url.substring(lastSlash + 1)
            if (suffix.endsWith(".m4a") || suffix.endsWith(".mp3")) {
                mp3Url = url
                android.util.Log.e("ImplWebViewClient", "request :${url}")
            }
            if (suffix.endsWith(".js")) {
                if (strOfflineResources.contains(suffix)) {
                    val mimeType = "application/x-javascript"
                    val offlineRes = "js/"
                    val inputs = view.context.assets.open("$offlineRes$suffix")
                    return WebResourceResponse(mimeType, "UTF-8", inputs)
                } else {
                    android.util.Log.e("ImplWebViewClient", "request js :${url}")
                }
            }
            if (url.contains("https://m3ws.kugou.com/api/v1/mv/infov2?")) {
                mvRequestUrl = url
                android.util.Log.e("ImplWebViewClient", "request mvRequestUrl :${mvRequestUrl}")
            }
            if (suffix.endsWith(".jpg") && url.contains("stdmusic")) {
                sTdMusicUrl = url
            }
            if (url.contains("get_lyrics")) {
                lrcUrl = url
                android.util.Log.e("ImplWebViewClient", "request :${url}")
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    fun getMusicFileUrl(): String {
        val url = mp3Url
        mp3Url = ""
        return url
    }

    fun getMusicLrcUrl(): String {
        val url = lrcUrl
        lrcUrl = ""
        return url
    }

    fun getSTdMusicUrl(): String {
        val url = sTdMusicUrl
        sTdMusicUrl = ""
        return url
    }

    fun getMvRequestUrl(): String {
        val url = mvRequestUrl
        mvRequestUrl = ""
        return url
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        if (url.contains("search-")) {
            view.loadUrl(
                "javascript:window.script_ex.getWebViewData('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');"
            )/**/
        }
    }
}