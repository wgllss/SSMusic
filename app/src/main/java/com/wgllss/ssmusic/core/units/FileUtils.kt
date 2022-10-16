package com.wgllss.ssmusic.core.units

import android.os.Environment
import android.text.TextUtils
import com.wgllss.ssmusic.core.ex.logE
import java.io.File

object FileUtils {
    private val environmentGetExternalStorageDirectory: String = Environment.getExternalStorageDirectory().path
    var sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    lateinit var dbPath: String

    fun getSDPath(): String = if (sdCardExist) environmentGetExternalStorageDirectory else ""

    fun getDBPath(): String {

        val sdCardPath: String = getSDPath()
        if (TextUtils.isEmpty(sdCardPath)) {
            logE("SD 卡不存在")
            return ""
        } else {
            val sb = StringBuilder()
            sb.append(sdCardPath)
            sb.append(File.separator)
            sb.append(".android")
            sb.append(File.separator)
            sb.append(".ssmusic")
            sb.append(File.separator)
            sb.append(".db")
            sb.append(File.separator)
            return sb.toString()
        }
    }

    /**
     * 文件的路径名称
     *
     * @return
     */
    fun getDBPath(dbName: String): String {
        if (!this::dbPath.isInitialized || dbPath.isNullOrEmpty()) {
            val sdCardPath: String = getSDPath()
            if (TextUtils.isEmpty(sdCardPath)) {
                logE("SD 卡不存在")
                return ""
            } else {
                val sb = StringBuilder()
                sb.append(sdCardPath)
                sb.append(File.separator)
                sb.append(".android")
                sb.append(File.separator)
                sb.append(".ssmusic")
                sb.append(File.separator)
                sb.append(".db")
                sb.append(File.separator)
                val file = File(sb.toString())
                if (!file.exists()) {
                    file.mkdirs()
                }
                sb.append(dbName)
                logE("sb:${sb.toString()}")
                dbPath = sb.toString()
                return dbPath
            }
        }
        return dbPath
    }

    //获取SD卡上目录，没有就创建
    fun getPathDirs(path: String): String {
        val sdCardPath: String = getSDPath()
        if (TextUtils.isEmpty(sdCardPath)) {
            logE("SD 卡不存在")
            return ""
        } else {
            path?.let {
                val sb = StringBuilder()
                sb.append(sdCardPath)
                sb.append(File.separator)
                sb.append(path)
                val file = File(sb.toString())
                if (!file.exists()) {
                    file.mkdirs()
                }
                return sb.toString()
            }
        }
    }
}