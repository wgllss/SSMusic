package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.FragmentHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel

@FragmentDestination(pageUrl = "fmt_home", label = "首页", iconId = R.drawable.ic_home_black_24dp)
class HomeFragment : BaseMVVMFragment<HomeViewModel, FragmentHomeBinding>(R.layout.fragment_home) {
    override fun activitySameViewModel() = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        WLog.e(this, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WLog.e(this, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        WLog.e(this, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        WLog.e(this, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        WLog.e(this, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        WLog.e(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        WLog.e(this, "onResume")
        super.onResume()
    }

    override fun onStop() {
        WLog.e(this, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        WLog.e(this, "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        WLog.e(this, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        WLog.e(this, "onDetach")
        super.onDetach()
    }
}