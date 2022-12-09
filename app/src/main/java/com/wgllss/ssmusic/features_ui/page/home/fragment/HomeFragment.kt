package com.wgllss.ssmusic.features_ui.page.home.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wgllss.annotations.FragmentDestination
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.core.ex.launchActivity
import com.wgllss.ssmusic.core.fragment.BaseMVVMFragment
import com.wgllss.ssmusic.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.databinding.FragmentHomeBinding
import com.wgllss.ssmusic.features_ui.page.home.adapter.PlayListAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@FragmentDestination(pageUrl = "fmt_home", asStarter = true, label = "首页", iconId = R.drawable.ic_home_black_24dp)
class HomeFragment : BaseMVVMFragment<HomeViewModel, FragmentHomeBinding>(R.layout.fragment_home) {

    @Inject
    lateinit var playListAdapterL: Lazy<PlayListAdapter>

    override fun activitySameViewModel() = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            viewModel = this@HomeFragment.viewModel
            adapter = playListAdapterL.get()
            lifecycleOwner = this@HomeFragment
            executePendingBindings()
            rvPlList.addOnItemTouchListener(object : OnRecyclerViewItemClickListener(rvPlList) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    playListAdapterL.get().getItem(position)?.run {
                        viewModel.mediaItemClicked(this, this.description.extras)
                    }
                    activity?.let { it.launchActivity(Intent(it, PlayActivity::class.java)) }
                }
            })
        }
        viewModel.liveData.observe(viewLifecycleOwner) {
            playListAdapterL.get().notifyData(it)
        }
        viewModel.rootMediaId.observe(viewLifecycleOwner) {
            it?.let { viewModel.subscribeByMediaID(it) }
        }
        playListAdapterL.get().setBlockDelete {
            viewModel.deleteFromPlayList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentMediaID.observe(viewLifecycleOwner) {
            playListAdapterL.get().currentMediaID = it
            playListAdapterL.get().notifyDataSetChanged()
        }
    }
}