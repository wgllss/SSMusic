package com.wgllss.ssmusic.datasource.repository

import android.webkit.JavascriptInterface

class InplJavaScriptX {

    var html: String? = null

    @JavascriptInterface
    fun getWebViewData(html: String) {
        this.html = html
    }
}