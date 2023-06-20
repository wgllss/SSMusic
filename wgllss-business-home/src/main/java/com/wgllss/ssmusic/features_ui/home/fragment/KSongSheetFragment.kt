package com.wgllss.ssmusic.features_ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.features_system.startup.HomeContains
import com.wgllss.ssmusic.features_system.startup.LaunchInflateKey
import com.wgllss.ssmusic.features_ui.home.adapter.KSongSheetAdapter
import com.wgllss.ssmusic.features_ui.home.viewmodels.HomeViewModel
import com.wgllss.ssmusic.features_ui.home.viewmodels.SongSheetTabViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KSongSheetFragment : TabTitleFragment<HomeViewModel>() {
    private lateinit var rvPlList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val songSheetAdapter by lazy { KSongSheetAdapter() }

    private val homeTabViewModel by lazy { viewModels<SongSheetTabViewModel>().value }

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"
        private const val KEY = "KEY"

        fun newInstance(titleS: String, keyS: String): KSongSheetFragment {
            val fragment = KSongSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, titleS)
                    putString(KEY, keyS)
                }
                title = titleS
            }
            return fragment
        }
    }

    override fun activitySameViewModel() = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!this::swipeRefreshLayout.isInitialized) {
            swipeRefreshLayout = HomeContains.getViewByKey(inflater.context, LaunchInflateKey.home_fragment)!! as SwipeRefreshLayout
            rvPlList = swipeRefreshLayout.findViewById(R.id.home_recycle_view)
        }
        return swipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                delay(100)
                swipeRefreshLayout.isRefreshing = false
            }
//            homeTabViewModel.homeKuGouSongSheet()
        }
        rvPlList.apply {
            adapter = songSheetAdapter
            layoutManager = GridLayoutManager(context, 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(i: Int): Int {
                        //spanCount 当横向时，2代表每列2行，
                        return when (songSheetAdapter.getItemViewType(i)) {
                            0 -> 1 //  spanCount/2  个位置占满一格
                            else -> 3 // spanCount/1  个位置占满一格
                        }
                    }
                }
            }

            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    activity?.let {
                        val intent = Intent(it, Class.forName("com.wgllss.ssmusic.features_ui.page.detail.activity.SongSheetDetailActivity"))
                            .putExtra("ENCODE_ID_KEY", songSheetAdapter.getItem(position).encode_id)
                            .putExtra("TYPE_KEY", 0)
                        startActivity(intent)
                    }
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    when (newState) {
//                        //滑动停止
//                        RecyclerView.SCROLL_STATE_IDLE -> activity?.let {
//                            Glide.with(it).resumeRequests()
//                        }
//                        else -> activity?.let {
//                            Glide.with(it).pauseRequests()
//                        }
//                    }
//                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (homeTabViewModel.enableLoadMore() && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                        homeTabViewModel.homeKSongSheetLoadMore()
                    }
                }
            })
            setHasFixedSize(true)
            val itemDecoration = View(context)
            val size = context.getIntToDip(5.0f).toInt()
            itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
            itemDecoration.setBackgroundColor(Color.parseColor("#30000000"))
            addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
        }
        homeTabViewModel.homeKuGouSongSheet()
    }

    override fun initObserve() {
        super.initObserve()
        homeTabViewModel.run {
            showUIDialog.observe(viewLifecycleOwner) {
                swipeRefreshLayout.isRefreshing = it.isShow && !homeTabViewModel.isLoadingMore
            }
            errorMsgLiveData.observe(viewLifecycleOwner) {
                onToast(it)
            }
            result.observe(viewLifecycleOwner) {
                songSheetAdapter.notifyData(it)
                songSheetAdapter.addFooter()
            }
            enableLoadeMore.observe(viewLifecycleOwner) {
                it?.takeIf {
                    !it
                }?.run {
                    songSheetAdapter.removeFooter()
                }
            }
        }
    }
}