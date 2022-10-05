package com.wgllss.ssmusic.core.units

import android.net.Uri

object ChineseUtils {

    fun urlencode(str: String): String {
        var s = Uri.encode(str)
        s = s.replace("_", "%5f")
        s = s.replace("-", "%2d")
        s = s.replace(".", "%2e")
        s = s.replace("~", "%7e")
        s = s.replace("!", "%21")
        s = s.replace("*", "%2a")
        s = s.replace("(", "%28")
        s = s.replace(")", "%29")
        //s = s.replace(/\+/g, "%20")
        s = s.replace("%", "_")
        return s;
    }
}