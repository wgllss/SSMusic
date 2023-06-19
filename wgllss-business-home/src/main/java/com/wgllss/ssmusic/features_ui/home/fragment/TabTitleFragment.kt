package com.wgllss.ssmusic.features_ui.home.fragment

import android.os.Bundle
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.units.LogTimer
import com.wgllss.core.viewmodel.BaseViewModel

open class TabTitleFragment<VM : BaseViewModel> : BaseViewModelFragment<VM>(0) {
    var title: String = ""
    protected var key: String = ""
    private val TITLE_KEY = "TITLE_KEY"
    private val KEY = "KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(TITLE_KEY, "") ?: ""
        key = arguments?.getString(KEY, "") ?: ""
        LogTimer.LogE(this, "$title onCreate")
    }
}