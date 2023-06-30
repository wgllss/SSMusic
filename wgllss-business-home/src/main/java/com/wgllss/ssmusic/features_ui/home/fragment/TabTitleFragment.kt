package com.wgllss.ssmusic.features_ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.units.WLog
import com.wgllss.core.viewmodel.BaseViewModel

open class TabTitleFragment<VM : BaseViewModel> : BaseViewModelFragment<VM>(0) {
    var title: String = ""
    protected var key: String = ""

    private var isFirstLoad = false

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun <F : TabTitleFragment<*>> newInstance(titleS: String, keyS: String, fragmentClass: Class<F>): F {
            val fragment = fragmentClass.newInstance().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, titleS)
                    putString(KEY, keyS)
                }
                title = titleS
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(TITLE_KEY, "") ?: ""
        key = arguments?.getString(KEY, "") ?: ""
        LogTimer.LogE(this, "$title onCreate")
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLoad) {
            isFirstLoad = true
            lazyLoad()
        }
        WLog.e(this, "onResume title $title")
    }

    protected open fun lazyLoad() {

    }
}