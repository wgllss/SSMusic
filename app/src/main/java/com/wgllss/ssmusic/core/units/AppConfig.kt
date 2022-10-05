package com.wgllss.ssmusic.core.units

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.wgllss.ssmusic.core.widget.navigation.Destination
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object AppConfig {

    private var sDestConfig: HashMap<String, Destination>? = null


    fun getDestConfig(): HashMap<String, Destination> {
        if (sDestConfig == null) {
            val content = parseFile("destination.json")
            content?.let {
                sDestConfig = JSON.parseObject(content, object : TypeReference<HashMap<String, Destination>>() {})
            }
        }
        return sDestConfig!!
    }


    /**
     * 解析文件
     * @param fileName
     * @return
     */
    private fun parseFile(fileName: String): String {
        val assets = AppGlobals.getApplication().assets
        var `is`: InputStream? = null
        var br: BufferedReader? = null
        val builder = StringBuilder()
        try {
            `is` = assets.open(fileName)
            br = BufferedReader(InputStreamReader(`is`))
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                br?.close()
            } catch (e: Exception) {
            }
        }
        return builder.toString()
    }
}