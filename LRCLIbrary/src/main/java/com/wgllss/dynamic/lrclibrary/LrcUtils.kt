package com.wgllss.dynamic.lrclibrary

import android.animation.ValueAnimator
import android.os.Build
import android.text.TextUtils
import android.text.format.DateUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object LrcUtils {

    private val PATTERN_LINE = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}\\])+)(.+)")
    private val PATTERN_TIME = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})\\]")

    /**
     * 从文件解析双语歌词
     */
    fun parseLrc(lrcFiles: Array<File?>?): MutableList<LrcEntry>? {
        if (lrcFiles == null || lrcFiles.size != 2 || lrcFiles[0] == null) {
            return null
        }
        val mainLrcFile = lrcFiles[0]
        val secondLrcFile = lrcFiles[1]
        val mainEntryList = parseLrc(mainLrcFile)
        val secondEntryList = parseLrc(secondLrcFile)
        if (mainEntryList != null && secondEntryList != null) {
            for (mainEntry in mainEntryList) {
                for (secondEntry in secondEntryList) {
                    if (mainEntry.time === secondEntry.time) {
                        mainEntry.secondText = secondEntry.text
                    }
                }
            }
        }
        return mainEntryList
    }

    /**
     * 从文件解析歌词
     */
    private fun parseLrc(lrcFile: File?): MutableList<LrcEntry>? {
        if (lrcFile == null || !lrcFile.exists()) {
            return null
        }
        val entryList: MutableList<LrcEntry> = ArrayList()
        try {
            val br = BufferedReader(InputStreamReader(FileInputStream(lrcFile), "utf-8"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                val list = parseLine(line)
                if (list != null && !list.isEmpty()) {
                    entryList.addAll(list)
                }
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        entryList.sort()
        return entryList
    }

    /**
     * 从文本解析双语歌词
     */
    fun parseLrc(mainLrcText: String, secondLrcText: String? = null): MutableList<LrcEntry>? {
        val mainEntryList = parseLrc(mainLrcText)!!
        secondLrcText?.let {
            val secondEntryList = parseLrc(it)!!
            for (mainEntry in mainEntryList) {
                for (secondEntry in secondEntryList) {
                    if (mainEntry.time === secondEntry.time) {
                        mainEntry.secondText = secondEntry.text
                    }
                }
            }
        }
        return mainEntryList
    }

    /**
     * 从文本解析歌词
     */
    private fun parseLrc(lrcText: String): MutableList<LrcEntry>? {
        var lrcText = lrcText
        if (TextUtils.isEmpty(lrcText)) {
            return null
        }
        if (lrcText.startsWith("\uFEFF")) {
            lrcText = lrcText.replace("\uFEFF", "")
        }
        val entryList: MutableList<LrcEntry> = ArrayList()
        val array = lrcText.split("\\n".toRegex()).toTypedArray()
        for (line in array) {
            val list = parseLine(line)
            if (list != null && !list.isEmpty()) {
                entryList.addAll(list)
            }
        }
        Collections.sort(entryList)
        return entryList
    }

    /**
     * 获取网络文本，需要在工作线程中执行
     */
    fun getContentFromNetwork(url: String?, charset: String?): String? {
        var lrcText: String? = null
        try {
            val _url = URL(url)
            val conn = _url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            if (conn.responseCode == 200) {
                val `is` = conn.inputStream
                val bos = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len: Int
                while (`is`.read(buffer).also { len = it } != -1) {
                    bos.write(buffer, 0, len)
                }
                `is`.close()
                bos.close()
                lrcText = bos.toString(charset)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lrcText
    }

    /**
     * 解析一行歌词
     */
    private fun parseLine(line: String): List<LrcEntry>? {
        var line = line
        if (TextUtils.isEmpty(line)) {
            return null
        }
        line = line.trim { it <= ' ' }
        // [00:17.65]让我掉下眼泪的
        val lineMatcher: Matcher = PATTERN_LINE.matcher(line)
        if (!lineMatcher.matches()) {
            return null
        }
        val times = lineMatcher.group(1)
        val text = lineMatcher.group(3)
        val entryList: MutableList<LrcEntry> = ArrayList()

        // [00:17.65]
        val timeMatcher: Matcher = PATTERN_TIME.matcher(times)
        while (timeMatcher.find()) {
            val min = timeMatcher.group(1).toLong()
            val sec = timeMatcher.group(2).toLong()
            val milString = timeMatcher.group(3)
            var mil = milString.toLong()
            // 如果毫秒是两位数，需要乘以10
            if (milString.length == 2) {
                mil = mil * 10
            }
            val time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil
            entryList.add(LrcEntry(time, text))
        }
        return entryList
    }

    /**
     * 转为[分:秒]
     */
    fun formatTime(milli: Long): String? {
        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
        val mm = String.format(Locale.getDefault(), "%02d", m)
        val ss = String.format(Locale.getDefault(), "%02d", s)
        return "$mm:$ss"
    }

    /**
     * 强制开启动画
     * Android 10 以后无法使用
     */
    fun resetDurationScale() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                val mField = ValueAnimator::class.java.getDeclaredField("sDurationScale")
                mField.isAccessible = true
                mField.setFloat(null, 1f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}