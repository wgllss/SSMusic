package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.FragmentSettingBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel

@FragmentDestination(pageUrl = "fmt_setting", label = "设置", iconId = R.drawable.ic_notifications_black_24dp)
class SettingFragment : BaseMVVMFragment<HomeViewModel, FragmentSettingBinding>(R.layout.fragment_setting) {

    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogTimer.LogE(this, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogTimer.LogE(this, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogTimer.LogE(this, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        LogTimer.LogE(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        LogTimer.LogE(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        LogTimer.LogE(this, "onResume")
        super.onResume()
    }

    override fun onStop() {
        LogTimer.LogE(this, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        LogTimer.LogE(this, "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        LogTimer.LogE(this, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        WLog.e(this, "onDetach")
        super.onDetach()
    }
}