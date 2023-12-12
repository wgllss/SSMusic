package com.wgllss.ssmusic.datasource.repository

import android.webkit.JavascriptInterface

class InplJavaScriptX {

    private var block: ((String) -> Unit)? = null

    fun setSearchResponse(block: (String) -> Unit) {
        this.block = block
    }

    @JavascriptInterface
    fun getWebViewData(html: String) {
        block?.invoke(html)
    }
}