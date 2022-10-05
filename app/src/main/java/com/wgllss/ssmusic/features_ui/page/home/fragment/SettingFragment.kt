package com.wgllss.ssmusic.features_ui.page.home.fragment

import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentSettingBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel

@FragmentDestination(pageUrl = "fmt_setting", label = "设置", iconId = R.drawable.ic_notifications_black_24dp)
class SettingFragment : BaseMVVMFragment<HomeViewModel, FragmentSettingBinding>(R.layout.fragment_setting) {

    override fun activitySameViewModel() = true
}