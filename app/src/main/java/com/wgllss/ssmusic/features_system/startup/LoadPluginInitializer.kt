package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.ssmusic.core.units.DownLoadUtil
import com.wgllss.ssmusic.core.units.WLog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.io.File

class LoadPluginInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://gitee.com/wgllss888/wgllss-music-data-source/raw/master/jar/classes3_dex.jar"
            val fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))
            val sb = StringBuilder(context.filesDir.absolutePath)
                .append(File.separator)
                .append("dex_s")
                .append(File.separator)
                .append(fileName)
            val file = File(sb.toString())
            if (file.exists()) {
                DynamicDataSourcePluginManagerUser.getInstance(context, file.absolutePath)
                WLog.e(this@LoadPluginInitializer, "文件已经存在:${file.absolutePath}")
            } else {
                val file2 = File(file.parent)
                file2.mkdirs()
                val downLoadUtil = DownLoadUtil()
                downLoadUtil.downFile(context, url, file).onEach {
                    DynamicDataSourcePluginManagerUser.getInstance(context, file.absolutePath)
                }.collect()
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}