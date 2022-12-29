package com.wgllss.ssmusic.core.units

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class DownLoadUtil {

    @SuppressLint("InlinedApi", "NewApi")
    fun downFile(context: Context, downUrl: String, downLoadDir: String, downLoadFileName: String) {
        val startTime = System.currentTimeMillis()
        if (context != null && downUrl != null && downUrl.isNotEmpty()) {
            if (VERSION.SDK_INT <= VERSION_CODES.HONEYCOMB_MR2) {
                val uri = Uri.parse(downUrl)
                val downloadIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(downloadIntent)
            } else {
                WLog.e(this, "downLoadDir :$downLoadDir  downLoadFileName:$downLoadFileName")
                val uri = Uri.parse(downUrl)
                val request = DownloadManager.Request(uri).apply {
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)//   设置允许使用的网络类型，这里是移动网络和wifi都可以
                    setDestinationInExternalFilesDir(context, null, "dex_s/$downLoadFileName")//设置下载内置目录
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downUrl))
                    setMimeType(mimeString)// //设置文件类型
                }
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = downloadManager.enqueue(request).toInt()
                val filePath = StringBuilder()
                    .append(downLoadDir)
                    .append(File.separator)
                    .append(downLoadFileName)
                WLog.e(this, "filePath :$filePath")
                val completeReceiver = AtarCompleteReceiver(downloadId, filePath.toString(), startTime)
                val mIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                context.registerReceiver(completeReceiver, mIntentFilter)
            }
        }
    }

    class AtarCompleteReceiver(private val downloadId: Int, private val filePath: String, private val startTime: Long) : BroadcastReceiver() {

        @SuppressLint("NewApi")
        override fun onReceive(context: Context, intent: Intent) {
            intent?.action?.takeIf {
                it == DownloadManager.ACTION_DOWNLOAD_COMPLETE
            }?.let {
                val completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (completeDownloadId == downloadId.toLong()) {
                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(completeDownloadId)
                    var result = -1
                    var c: Cursor? = null
                    try {
                        c = downloadManager.query(query)
                        if (c != null && c.moveToFirst()) {
                            val statusInt = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            result = c.getInt(statusInt)
                            val fileUriIdx: Int = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            val fileUri: String = c.getString(fileUriIdx)

                            WLog.e(this, "fileName： ms fileUri:$fileUri")
                        }
                    } finally {
                        c?.close()
                    }
                    if (result == DownloadManager.STATUS_SUCCESSFUL) {
                        val endTime = System.currentTimeMillis()
                        val times: Long = endTime - startTime
                        WLog.e(this, "耗时：$times ms")
                    }
                }
            }
        }
    }

    suspend fun downFile(context: Context, downUrl: String, file: File) = flow {
        WLog.e(this@DownLoadUtil, "耗时333  ：downFile ms")
        val startTime = System.currentTimeMillis()
        if (context != null && downUrl != null && downUrl.isNotEmpty()) {
            val url = URL(downUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            val inputStream = conn.inputStream
            val fos = FileOutputStream(file)
            val bis = BufferedInputStream(inputStream)
            val buffer = ByteArray(1024)
            var len: Int
            var total = 0
            while (bis.read(buffer).also { len = it } != -1) {
                fos.write(buffer, 0, len)
                total += len
            }
            fos.close()
            bis.close()
            inputStream.close()
            val endTime = System.currentTimeMillis()
            val times: Long = endTime - startTime
            WLog.e(this@DownLoadUtil, "耗时333:${times} ms")
        }
        emit(0)
    }.flowOn(Dispatchers.IO).catch { it.printStackTrace() }

}