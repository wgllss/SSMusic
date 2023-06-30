package com.wgllss.ssmusic.features_ui.page.search.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wgllss.core.ex.HideSoftInputFromWindow
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.fragment.BaseMVVMFragment
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.databinding.FragmentSearchBinding
import com.wgllss.ssmusic.features_ui.page.classics.adapter.HomeMusicAdapter
import com.wgllss.ssmusic.features_ui.page.home.viewmodels.HomeViewModel2
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//@FragmentDestination(pageUrl = "fmt_search", label = "搜索", iconId = R.drawable.ic_dashboard_black_24dp)
@AndroidEntryPoint
class SearchFragment @Inject constructor() : BaseMVVMFragment<HomeViewModel2, FragmentSearchBinding>(R.layout.fragment_search) {

    //    @Inject
//    lateinit var musicAdapterL: Lazy<MusicAdapter>
    private val musicAdapter by lazy { HomeMusicAdapter() }

    override fun activitySameViewModel() = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.apply {
            model = this@SearchFragment.viewModel
            adapter = musicAdapter
            lifecycleOwner = this@SearchFragment
            executePendingBindings()
            shapeableSearch.setOnClickListener {
                viewModel.initPage()
                viewModel.searchKeyByTitle()
                HideSoftInputFromWindow(root)
            }
            rvResult?.run {
                addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                    override fun onItemClickListener(itemRootView: View, position: Int) {
                        viewModel.getDetailFromSearch(position)
                    }
                })
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        if (viewModel.enableLoadMore() && linearLayoutManager!!.itemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                            viewModel.searchKeyByTitle()
                        }
                    }
                })
            }
            etName.requestFocus()
            etName.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        viewModel.initPage()
                        viewModel.searchKeyByTitle()
                        HideSoftInputFromWindow(root)
                        true
                    }
                    else -> {}
                }
                false
            }
            imgBack.setOnClickListener {
                activity?.run {
                    HideSoftInputFromWindow(root)
                    finishActivity()
                }
            }
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel?.run {
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        launchActivity(Intent(this, PlayActivity::class.java))
                    }
                }
            }
            result.observe(viewLifecycleOwner) {
                musicAdapter.notifyData(it)
                musicAdapter.addFooter()
            }
            enableLoadeMore.observe(viewLifecycleOwner) {
                if (!it) {
                    musicAdapter.removeFooter()
                }
            }
        }
    }
}