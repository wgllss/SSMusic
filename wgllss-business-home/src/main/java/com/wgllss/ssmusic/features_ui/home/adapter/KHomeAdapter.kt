package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.core.widget.DividerGridItemDecoration
import com.wgllss.core.widget.OnRecyclerViewItemClickListener
import com.wgllss.ssmusic.data.HomeItemBean
import com.wgllss.ssmusic.data.MusicItemBean

class KHomeAdapter : BaseRecyclerAdapter<HomeItemBean>() {
    private val img_singer1 = 1
    private val img_singer2 = 2
    private val img_singer3 = 3

    private val txt_author1 = 5
    private val txt_author2 = 6
    private val txt_author3 = 7

    var block: ((MusicItemBean) -> Any)? = null

    fun setOnItemClickNewList(block: (MusicItemBean) -> Any) {
        this.block = block
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].itemType
    }

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        return when (viewType) {
            1, 2 -> {
                val recyclerView = RecyclerView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                    layoutManager = GridLayoutManager(context, 3)
                    val itemDecoration = View(context)
                    val size = context.getIntToDip(5.0f).toInt()
                    itemDecoration.layoutParams = ViewGroup.LayoutParams(size, size)
                    itemDecoration.setBackgroundColor(Color.parseColor("#30000000"))
                    addItemDecoration(DividerGridItemDecoration(context, GridLayoutManager.VERTICAL, itemDecoration))
                    if (viewType == 1) {
                        val homeItem1Adapter = KHomeItem1Adapter()
                        adapter = homeItem1Adapter
                        addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                            override fun onItemClickListener(itemRootView: View, position: Int) {
                                block?.invoke(homeItem1Adapter.getItem(position))
                            }
                        })
                    }
                    if (viewType == 2) {
                        val homeItem2Adapter = KHomeItem2Adapter()
                        adapter = homeItem2Adapter
                        addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                            override fun onItemClickListener(itemRootView: View, position: Int) {
//                                SongSheetDetailActivity.startSongSheetDetailActivity(context, homeItem2Adapter.getItem(position).detailUrl)
                            }
                        })
                    }
                }
                BaseBindingViewHolder(recyclerView)
            }
            3 -> {
                val recyclerView = RecyclerView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                    layoutManager = LinearLayoutManager(context)
                    val homeItem3Adapter = KHomeItem3Adapter()
                    adapter = homeItem3Adapter
                    addOnItemTouchListener(object : OnRecyclerViewItemClickListener(this) {
                        override fun onItemClickListener(itemRootView: View, position: Int) {
//                            SongSheetDetailActivity.startSongSheetDetailActivity(context, homeItem3Adapter.getItem(position).linkUrl, 1)
                        }
                    })
                }
                BaseBindingViewHolder(recyclerView)
            }
            4 -> {
                val frameLayout = FrameLayout(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                }
                val imgSinger1 = ShapeableImageView(parent.context).apply {
                    id = img_singer1
                    val size = context.getIntToDip(120f).toInt()
                    val lp = FrameLayout.LayoutParams(size, size)
                    lp.gravity = Gravity.LEFT or Gravity.TOP;
                    layoutParams = lp
                    scaleType = ImageView.ScaleType.FIT_XY
                    shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                        setAllCorners(RoundedCornerTreatment())
                        setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                    }.build()
                }
                frameLayout.addView(imgSinger1)
                val txtAuthor1 = TextView(parent.context).apply {
                    id = txt_author1
                    val size = context.getIntToDip(10f).toInt()
                    val lp = FrameLayout.LayoutParams(8 * size, 3 * size)
                    lp.gravity = Gravity.LEFT or Gravity.TOP
                    lp.topMargin = context.getIntToDip(125f).toInt()
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = lp
                }
                frameLayout.addView(txtAuthor1)

                val imgSinger2 = ShapeableImageView(parent.context).apply {
                    id = img_singer2
                    val size = context.getIntToDip(120f).toInt()
                    val lp = FrameLayout.LayoutParams(size, size)
                    lp.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    layoutParams = lp
                    scaleType = ImageView.ScaleType.FIT_XY
                    shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                        setAllCorners(RoundedCornerTreatment())
                        setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                    }.build()
                }
                frameLayout.addView(imgSinger2)
                val txtAuthor2 = TextView(parent.context).apply {
                    id = txt_author2
                    val size = context.getIntToDip(10f).toInt()
                    val lp = FrameLayout.LayoutParams(8 * size, 3 * size)
                    lp.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    lp.topMargin = context.getIntToDip(125f).toInt()
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = lp
                }
                frameLayout.addView(txtAuthor2)

                val imgSinger3 = ShapeableImageView(parent.context).apply {
                    id = img_singer3
                    val size = context.getIntToDip(120f).toInt()
                    val lp = FrameLayout.LayoutParams(size, size)
                    lp.gravity = Gravity.RIGHT or Gravity.TOP
                    layoutParams = lp
                    layoutParams = lp
                    scaleType = ImageView.ScaleType.FIT_XY
                    shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                        setAllCorners(RoundedCornerTreatment())
                        setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                    }.build()
                }
                frameLayout.addView(imgSinger3)
                val txtAuthor3 = TextView(parent.context).apply {
                    id = txt_author3
                    val size = context.getIntToDip(10f).toInt()
                    val lp = FrameLayout.LayoutParams(12 * size, 3 * size)
                    lp.gravity = Gravity.RIGHT or Gravity.TOP
                    lp.topMargin = context.getIntToDip(125f).toInt()
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = lp
                }
                frameLayout.addView(txtAuthor3)
                BaseBindingViewHolder(frameLayout)
            }
            else -> {
                val textView = TextView(parent.context).apply {
                    val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(50f).toInt())
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22f)
                    paint.isFakeBoldText = true
                    layoutParams = lp
                    gravity = Gravity.CENTER_VERTICAL
                }
                BaseBindingViewHolder(textView)
            }
        }
    }

    override fun onBindItem(context: Context, item: HomeItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        when (item.itemType) {
            0 -> {
                (holder.itemView as TextView).text = item.homeLableBean?.lable
            }
            1 -> {
                ((holder.itemView as RecyclerView).adapter as KHomeItem1Adapter).notifyData(item.listNew!!)
            }
            2 -> {
                ((holder.itemView as RecyclerView).adapter as KHomeItem2Adapter).notifyData(item.listHot!!)
            }
            3 -> {
                ((holder.itemView as RecyclerView).adapter as KHomeItem3Adapter).notifyData(item.rankList!!)
            }
            4 -> {
                item.singers?.let {
                    holder.itemView.findViewById<ShapeableImageView>(img_singer1).apply {
                        loadUrl(it[0].imgUrl)
                        setOnClickListener { _ ->
//                            SongSheetDetailActivity.startSongSheetDetailActivity(context, it[0].encodeID, 2, it[0].name)
                        }
                    }
                    holder.itemView.findViewById<ShapeableImageView>(img_singer2).apply {
                        loadUrl(it[1].imgUrl)
                        setOnClickListener { _ ->
//                            SongSheetDetailActivity.startSongSheetDetailActivity(context, it[1].encodeID, 2, it[0].name)
                        }
                    }
                    holder.itemView.findViewById<ShapeableImageView>(img_singer3).apply {
                        loadUrl(it[2].imgUrl)
                        setOnClickListener { _ ->
//                            SongSheetDetailActivity.startSongSheetDetailActivity(context, it[2].encodeID, 2, it[0].name)
                        }
                    }

                    holder.itemView.findViewById<TextView>(txt_author1).text = it[0].name
                    holder.itemView.findViewById<TextView>(txt_author2).text = it[1].name
                    holder.itemView.findViewById<TextView>(txt_author3).text = it[2].name
                }

            }
        }
    }
}