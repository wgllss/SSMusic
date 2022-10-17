package com.wgllss.ssmusic.core.units

import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.wgllss.annotations.Destination
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
//App Key：634d040505844627b5672f2f
//App Master Secret：6y2akwgydlplnewzdhqczfmld5kan6xo
//Umeng Message Secret：163045f64d1a5f94f8e50cbf8bb81f65
object AppConfig {

    private var sDestConfig: HashMap<String, Destination>? = null


    fun getDestConfig(context: Context): HashMap<String, Destination> {
        if (sDestConfig == null) {
            HashMap::class.java
            val content = parseFile(context, "destination.json")
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
    private fun parseFile(context: Context = AppGlobals.getApplication(), fileName: String): String {
        val assets = context.assets
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