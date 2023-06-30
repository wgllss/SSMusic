package com.wgllss.ssmusic.features_ui.page.detail.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.ex.finishActivity
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.launchActivity
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.fragment.BaseViewModelFragment
import com.wgllss.core.material.ThemeUtils
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.features_ui.page.detail.adapter.SongSheetAdapter
import com.wgllss.ssmusic.features_ui.page.detail.viewmodel.SongSheetViewModel
import com.wgllss.ssmusic.features_ui.page.mv.activity.KMVActivity
import com.wgllss.ssmusic.features_ui.page.playing.activity.PlayActivity

class SongSheetFragment(private val encodeID: String) : BaseViewModelFragment<SongSheetViewModel>(0) {

    private lateinit var rootView: View
    private lateinit var toolbar2: Toolbar
    private lateinit var img_bg: ImageView
    private lateinit var img_author: ShapeableImageView
    private lateinit var img_sheet: ShapeableImageView
    private lateinit var txt_tro: TextView
    private lateinit var txt_author: TextView
    private lateinit var recycler_view_sheet: RecyclerView
    private lateinit var toolbar_layout: CollapsingToolbarLayout

    private val homeItem1Adapter by lazy { SongSheetAdapter() }

    override fun activitySameViewModel() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::rootView.isInitialized) {
            rootView = inflater.inflate(R.layout.fragment_song_sheet_detail, container, false)
            img_sheet = rootView.findViewById(R.id.img_sheet)
            img_bg = rootView.findViewById<ImageView>(R.id.img_bg).apply {
                (layoutParams as CollapsingToolbarLayout.LayoutParams).collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
            }
            img_author = rootView.findViewById(R.id.img_author)
            txt_tro = rootView.findViewById(R.id.txt_tro)
            txt_author = rootView.findViewById(R.id.txt_author)
            toolbar2 = rootView.findViewById<Toolbar>(R.id.toolbar2).apply {
                navigationIcon = resources.getDrawable(com.wgllss.music.skin.R.drawable.ic_baseline_arrow_back_24)
                (layoutParams as CollapsingToolbarLayout.LayoutParams).collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
            }
            toolbar_layout = rootView.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).apply {
                setContentScrimColor(ThemeUtils.getColorPrimary(inflater.context))
                setStatusBarScrimColor(ThemeUtils.getColorPrimary(inflater.context))
                expandedTitleGravity = Gravity.TOP or Gravity.LEFT
                expandedTitleMarginStart = inflater.context.getIntToDip(165f).toInt()
                expandedTitleMarginTop = inflater.context.getIntToDip(81f).toInt()
//                setExpandedTitleTextAppearance(R.style.UserpageUserNameExpandedStyle)
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            }

            recycler_view_sheet = rootView.findViewById<RecyclerView?>(R.id.recycler_view_sheet).apply {
                val itemDecoration = View(context)
                val size = context.getIntToDip(1.0f).toInt()
                itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                itemDecoration.setBackgroundColor(Color.parseColor("#20000000"))
                addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
            }

            img_sheet.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setAllCorners(RoundedCornerTreatment())
                setAllCornerSizes(inflater.context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
            }.build()
            img_author.shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setAllCorners(RoundedCornerTreatment())
                setAllCornerSizes(inflater.context.getIntToDip(23f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
            }.build()
        }
        rootView?.parent?.takeIf {
            it is ViewGroup
        }?.let {
            (it as ViewGroup).removeView(rootView)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar2?.setNavigationOnClickListener {
            requireActivity().finishActivity()
        }
        recycler_view_sheet.apply {
            adapter = homeItem1Adapter
            layoutManager = LinearLayoutManager(context)
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
            addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                override fun onItemClickListener(itemRootView: View, position: Int) {
                    viewModel.doPlay(homeItem1Adapter.getItem(position))
                }
            })
        }
    }

    override fun initObserve() {
        super.initObserve()
        viewModel.run {
            kSongSheetDetail(encodeID)
            nowPlay.observe(viewLifecycleOwner) {
                it?.takeIf {
                    it
                }?.let {
                    activity?.run {
                        launchActivity(Intent(this, PlayActivity::class.java))
                    }
                }
            }
            liveDataMV.observe(viewLifecycleOwner) {
                activity?.run {
                    KMVActivity.startKMVActivity(this, it.url, it.title)
                }
            }

            songSheetDetail.observe(viewLifecycleOwner) {
                img_sheet.loadUrl(it.info.list.imgurl)
                toolbar_layout.title = it.info.list.specialname
                txt_author.text = it.info.list.nickname
                txt_tro.text = it.info.list.intro
                img_author.loadUrl(it.info.list.user_avatar)
                Glide.with(this@SongSheetFragment).asBitmap()
                    .load(it.info.list.imgurl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            resource?.let { it ->
                                Palette.from(it).generate { p ->
                                    p?.lightMutedSwatch?.let { s ->
                                        img_bg.setBackgroundColor(s.rgb)
                                        val colors = intArrayOf(s.titleTextColor)
                                        val states = arrayOfNulls<IntArray>(1)
                                        states[0] = intArrayOf(android.R.attr.state_enabled)
                                        toolbar_layout.setExpandedTitleTextColor(ColorStateList(states, colors))
                                        txt_author.setTextColor(s.bodyTextColor)
                                        txt_tro.setTextColor(s.bodyTextColor)
                                    }
                                }
                            }
                        }
                    })
                homeItem1Adapter.notifyData(it.listData)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.nowPlay.postValue(false)
    }
}