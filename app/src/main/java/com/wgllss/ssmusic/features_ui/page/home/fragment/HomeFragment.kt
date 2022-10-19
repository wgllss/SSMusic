package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scclzkj.base_core.base.BaseMVVMFragment
import com.scclzkj.base_core.widget.OnRecyclerViewItemClickListener
import com.umeng.analytics.MobclickAgent
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.units.LogTimer
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.databinding.FragmentHomeBinding
import com.wgllss.ssmusic.features_system.app.AppViewModel
import com.wgllss.ssmusic.features_ui.page.home.adapter.MusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.adapter.PlayListAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@FragmentDestination(pageUrl = "fmt_home", asStarter = true, label = "首页", iconId = R.drawable.ic_home_black_24dp)
class HomeFragment : BaseMVVMFragment<HomeViewModel, FragmentHomeBinding>(R.layout.fragment_home) {
    @Inject
    lateinit var appViewModel: Lazy<AppViewModel>

    @Inject
    lateinit var playListAdapterL: Lazy<PlayListAdapter>

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
        binding?.apply {
            viewModel = this@HomeFragment.viewModel
            adapter = playListAdapterL.get()
            lifecycleOwner = this@HomeFragment
            executePendingBindings()
            rvPlList.addOnItemTouchListener(object : OnRecyclerViewItemClickListener(rvPlList) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    appViewModel.get().playPosition(position)
                }
            })
        }
        appViewModel.get().run {
            queryPlayList()
            isInitSuccess.observe(viewLifecycleOwner) {
                it.takeIf {
                    it == true
                }?.let {
                    liveData.observe(viewLifecycleOwner) { data ->
                        playListAdapterL.get().notifyData(data)
                    }
                }
            }
        }
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