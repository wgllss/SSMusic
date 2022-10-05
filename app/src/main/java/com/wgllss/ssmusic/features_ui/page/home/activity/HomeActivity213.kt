package com.wgllss.ssmusic.features_ui.page.home.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.activity.BaseMVVMActivity
import com.wgllss.ssmusic.core.units.ChineseUtils
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.ActivityHome2323Binding
import com.wgllss.ssmusic.databinding.ActivityHomeBinding
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity213 : BaseMVVMActivity<HomeViewModel, ActivityHome2323Binding>(R.layout.activity_home2323) {

    @Inject
    lateinit var MusicRepositoryL: Lazy<MusicRepository>

//    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)


        binding.fab.setOnClickListener { view ->
            binding.textInputEdit.text?.toString()?.takeIf {
                it.isNotEmpty()
            }?.let {
                viewModel.searchKeyByTitle(it)
//                val pl = ChineseUtils.urlencode(it)
//                WLog.e(this@HomeActivity, pl)
//                val sb = StringBuilder("https://www.hifini.com/search-")
//                sb.append(pl)
//                sb.append("-1.htm")
//                getListHtml(sb.toString())
//                get(it)
            }

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
//            getHtml("https://www.hifini.com/thread-915.htm")
        }
    }

    fun get(keyword: String) {
        lifecycleScope.launch {
            MusicRepositoryL.get().searchKeyByTitle(keyword)
                .onStart { }
                .catch { it.printStackTrace() }
                .flowOn(Dispatchers.IO)
                .onEach { }
                .collect()
        }
    }

    fun getListHtml(url: String) {
        val startTime = System.currentTimeMillis()
        lifecycleScope.launch {
            flow {
                WLog.e(this@HomeActivity213, url)
                val document = Jsoup.connect(url).get()
                val dcS = document.select(".break-all")
                WLog.e(this@HomeActivity213, "dcs-->${dcS.size}")
                dcS?.forEach {
//                    val links = it.select("a[href]");
                    val links = it.select("a[href]");
                    val link = links.first()
                    val url = link!!.attr("abs:href")
                    WLog.e(this@HomeActivity213, "url:${url}")
                    getHtml(url)
                    WLog.e(this@HomeActivity213, "links:${links}")
                }
                emit("")
            }.onStart { }
                .catch { }
                .flowOn(Dispatchers.IO).onEach { }
                .collect()

        }
    }

    fun getHtml(url: String) {
        val startTime = System.currentTimeMillis()
        lifecycleScope.launch {
            flow {
                val document = Jsoup.connect(url).get()
                val element = document.select("script")
                element?.forEach {
                    if (it.html().contains("var ap4 = new APlayer")) {
                        val str = it.html()
                        var startIndex = str.indexOf("[")
                        val endIndex = str.lastIndexOf("]")
                        var subStr = str.substring(startIndex + 1, endIndex - 2).trim()
                        subStr = subStr.replace("{", "{\"")
                            .replace(": '", "\":\"")
                            .replace(":'", "\":\"")
                            .replace("',", "\",\"")
                            .replace("'", "\"")
                            .replace("},", "}")
                            .replace("\n", "")
                            .replace(" ", "")
                        WLog.e(this@HomeActivity213, "${System.currentTimeMillis() - startTime}ms  ___ ${subStr}")
                    }
                    return@forEach
                }

                var sff = ChineseUtils.urlencode("三国杀")
                WLog.e(this@HomeActivity213, "sff ${sff}")
//                val chatcontentElement: Element = doc.getElementById("script")
//                WLog.e(this@HomeActivity, dfd.toString())
                emit(document)
            }.onStart { }
                .catch { }
                .flowOn(Dispatchers.IO)
                .onEach { }
                .collect()
        }
    }
}