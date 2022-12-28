package com.wgllss.ssmusic.core.units

import android.os.Environment
import android.text.TextUtils
import java.io.File

object FileUtils {
    private val environmentGetExternalStorageDirectory: String = Environment.getExternalStorageDirectory().path
    var sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    lateinit var dbPath: String

    fun getSDPath(): String = if (sdCardExist) environmentGetExternalStorageDirectory else ""

    fun getSDPathByRootFile(rootFileName: String): String {
        val sdCardPath: String = getSDPath()
        return if (TextUtils.isEmpty(sdCardPath)) {
            WLog.e(this, "SD 卡不存在")
            ""
        } else {
            val sb = StringBuilder()
            sb.append(sdCardPath)
            sb.append(File.separator)
            sb.append(rootFileName)
            sb.toString()
        }
    }

    fun getDBPath(): String {
        val sdCardPath: String = getSDPath()
        return if (TextUtils.isEmpty(sdCardPath)) {
            WLog.e(this, "SD 卡不存在")
            ""
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
            sb.toString()
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
                WLog.e(this, "SD 卡不存在")
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
                WLog.e(this, "sb:${sb.toString()}")
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
            WLog.e(this, "SD 卡不存在")
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

    /**
     * 获取SD卡上的文件 路径
     * @param fileDirs :路径目录
     * @param fileName ：文件名称
     */
    fun getFilePath(fileDirs: String, fileName: String): String {
        val sdCardPath: String = getSDPath()
        return if (TextUtils.isEmpty(sdCardPath)) {
            WLog.e(this, "SD 卡不存在")
            ""
        } else {
            val sb = StringBuilder()
            sb.append(sdCardPath)
            sb.append(File.separator)
            sb.append(fileDirs)
            val file = File(sb.toString())
            if (!file.exists()) {
                file.mkdirs()
            }
            sb.append(File.separator)
            sb.append(fileName)
            WLog.e(this, "fileName:路径: $:${sb.toString()}")
            sb.toString()
        }
    }
}