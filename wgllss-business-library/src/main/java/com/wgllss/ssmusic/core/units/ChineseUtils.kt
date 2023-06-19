package com.wgllss.ssmusic.core.units

import android.net.Uri


object ChineseUtils {

    fun urlEncode(str: String) = Uri.encode(str)
        .replace("_", "%5f")
        .replace("-", "%2d")
        .replace(".", "%2e")
        .replace("~", "%7e")
        .replace("!", "%21")
        .replace("*", "%2a")
        .replace("(", "%28")
        .replace(")", "%29")
        //s = s.replace(/\+/g, "%20")
        .replace("%", "_")

}