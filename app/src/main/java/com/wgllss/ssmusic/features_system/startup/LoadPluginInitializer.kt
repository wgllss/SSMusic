package com.wgllss.ssmusic.features_system.startup

import android.content.Context
import androidx.startup.Initializer
import com.wgllss.dynamic.host.library.DynamicDataSourcePluginManagerUser
import com.wgllss.ssmusic.core.units.WLog
import com.zjh.download.SimpleDownload
import com.zjh.download.download
import com.zjh.download.helper.State
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

class LoadPluginInitializer : Initializer<Unit> {
    private val serviceJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.IO + serviceJob) }

    override fun create(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            SimpleDownload.instance.init(context)
            val url = "http://192.168.3.21:8080/assets/music_plugin/classes3_dex.jar"
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
                val downloadTask = serviceScope.download(url, fileName, file.parent)
                var isSucceed = 0
                //状态监听
                downloadTask.state().onEach {
                    when (it) {
                        is State.None -> WLog.e(this@LoadPluginInitializer, "$fileName: 未开始任务")
                        is State.Waiting -> WLog.e(this@LoadPluginInitializer, "$fileName: 等待中")
                        is State.Downloading -> WLog.e(this@LoadPluginInitializer, "$fileName: 下载中")
                        is State.Failed -> WLog.e(this@LoadPluginInitializer, "$fileName: 下载失败")
                        is State.Stopped -> WLog.e(this@LoadPluginInitializer, "$fileName: 下载已暂停")
                        is State.Succeed -> {
                            WLog.e(this@LoadPluginInitializer, "Succeed: 下载成功")
                            if (isSucceed == 1) {
                                return@onEach
                            }
                            isSucceed = 1
                            DynamicDataSourcePluginManagerUser.getInstance(context, file.absolutePath)
                            WLog.e(this@LoadPluginInitializer, "$fileName: 下载成功")
                        }
                    }
                }.launchIn(serviceScope)
                //进度监听
                downloadTask.progress().onEach {
                    WLog.e(this@LoadPluginInitializer, "$fileName: progress : ${it.percentStr()}")
                }.launchIn(serviceScope)
                //开始下载任务
                downloadTask.start()
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}