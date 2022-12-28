package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.ssmusic.core.units.FileUtils
import com.wgllss.ssmusic.core.units.WLog
import com.zjh.download.SimpleDownload
import com.zjh.download.download
import com.zjh.download.helper.State
import com.zjh.download.utils.logD
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoadPluginInitiallzer : Initializer<Unit> {
    private val serviceJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.IO + serviceJob) }

    override fun create(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            SimpleDownload.instance.init(context)
            val url = "http://192.168.3.21:8080/assets/music_plugin/classes3_dex.jar"
            val fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))
            WLog.e(this, "fileName : $fileName")
//            val downloadTask = serviceScope.download(url, fileName, FileUtils.getPathDirs("music_data_source/"))
            val downloadTask = serviceScope.download(url, fileName, "${context.filesDir.absolutePath}/dexs")
            //状态监听
            downloadTask.state().onEach {
                when (it) {
                    is State.None -> logD("未开始任务")
                    is State.Waiting -> logD("等待中")
                    is State.Downloading -> logD("下载中")
                    is State.Failed -> logD("下载失败")
                    is State.Stopped -> logD("下载已暂停")
                    is State.Succeed -> {
//                        DynamicDataSourcePluginManagerUser.getInstance(context, FileUtils.getFilePath("music_data_source", fileName))
                        DynamicDataSourcePluginManagerUser.getInstance(context, "${context.filesDir.absolutePath}/dexs/$fileName")
                        logD("下载成功")
                    }
                }
            }.launchIn(serviceScope)
            //进度监听
            downloadTask.progress().onEach {
                logD("name : progress : ${it.percentStr()}")
            }.launchIn(serviceScope)
            //开始下载任务
            downloadTask.start()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}