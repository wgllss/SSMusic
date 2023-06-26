package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
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

    private val txt_author1 = 5

    private val img = 1
    private val music_name = 2
    private val name = 3

    private val img2 = 1
    private val music_name2 = 2

    private val img3 = 1
    private val txt1 = 2
    private val txt2 = 3
    private val txt3 = 4

    override fun getItemViewType(position: Int): Int {
        return mData[position].itemType
    }

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        return when (viewType) {
            1 -> {
                val linearLayout = LinearLayout(context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(180f).toInt())
                    val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
                    val typedValue = TypedValue()
                    val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
                    foreground = attr.getDrawable(0)!!
                    attr.recycle()
                    val size = context.getIntToDip(8f).toInt()
                    setPadding(size, size, size, size)
                    isClickable = true
                    isFocusable = true
                    gravity = Gravity.CENTER_HORIZONTAL
                    orientation = LinearLayout.VERTICAL
                }
                val image = ShapeableImageView(parent.context).apply {
                    id = img
                    scaleType = ImageView.ScaleType.FIT_XY
                    val size = context.getIntToDip(120f).toInt()
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size)
                    layoutParams = lp
                    shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                        setAllCorners(RoundedCornerTreatment())
                        setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                    }.build()
                }
                linearLayout.addView(image)
                val textViewMusicName = TextView(context).apply {
                    id = music_name
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    gravity = Gravity.CENTER_HORIZONTAL
                    maxLines = 1
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    layoutParams = lp
                }
                linearLayout.addView(textViewMusicName)

                val textName = TextView(context).apply {
                    id = name
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    gravity = Gravity.CENTER_HORIZONTAL
                    lp.topMargin = context.getIntToDip(5f).toInt()
                    maxLines = 1
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    layoutParams = lp
                }
                linearLayout.addView(textName)
                BaseBindingViewHolder(linearLayout)
            }
            2 -> {
                val linearLayout = LinearLayout(context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(180f).toInt())
                    val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
                    val typedValue = TypedValue()
                    val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
                    foreground = attr.getDrawable(0)!!
                    attr.recycle()
                    val size = context.getIntToDip(8f).toInt()
                    setPadding(size, size, size, size)
                    isClickable = true
                    isFocusable = true
                    gravity = Gravity.CENTER_HORIZONTAL
                    orientation = LinearLayout.VERTICAL
                }
                val image = ShapeableImageView(parent.context).apply {
                    id = img2
                    val size = context.getIntToDip(120f).toInt()
                    scaleType = ImageView.ScaleType.FIT_XY
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size)
                    layoutParams = lp
                    shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                        setAllCorners(RoundedCornerTreatment())
                        setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                    }.build()
                }
                linearLayout.addView(image)
                val textViewMusicName = TextView(context).apply {
                    id = music_name2
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.gravity = Gravity.CENTER_HORIZONTAL
                    maxLines = 2
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    layoutParams = lp
                }
                linearLayout.addView(textViewMusicName)
                BaseBindingViewHolder(linearLayout)
            }
            3 -> {
                val frameLayout = FrameLayout(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(110f).toInt())
                    val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
                    val typedValue = TypedValue()
                    val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
                    foreground = attr.getDrawable(0)!!
                    attr.recycle()
                    isClickable = true
                    isFocusable = true
                }
                val image = ShapeableImageView(parent.context).apply {
                    id = img
                    val size = context.getIntToDip(90f).toInt()
                    val lp = LinearLayout.LayoutParams(size, size)
                    lp.gravity = Gravity.TOP or Gravity.LEFT
                    layoutParams = lp
                }
                frameLayout.addView(image)

                val textView1 = TextView(context).apply {
                    id = txt1
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
                    lp.leftMargin = context.getIntToDip(100f).toInt()
                    maxLines = 1
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = lp
                }
                frameLayout.addView(textView1)

                val textView2 = TextView(context).apply {
                    id = txt2
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
                    lp.leftMargin = context.getIntToDip(100f).toInt()
                    lp.topMargin = context.getIntToDip(30f).toInt()
                    maxLines = 1
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = lp
                }
                frameLayout.addView(textView2)

                val textView3 = TextView(context).apply {
                    id = txt3
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getIntToDip(25f).toInt())
                    lp.leftMargin = context.getIntToDip(100f).toInt()
                    lp.topMargin = context.getIntToDip(60f).toInt()
                    maxLines = 1
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = lp
                }
                frameLayout.addView(textView3)
                BaseBindingViewHolder(frameLayout)
            }
            4 -> {
                val frameLayout = FrameLayout(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                    val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
                    val typedValue = TypedValue()
                    val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
                    foreground = attr.getDrawable(0)!!
                    attr.recycle()
                    isClickable = true
                    isFocusable = true
                    val size = context.getIntToDip(8f).toInt()
                    setPadding(size, size, size, size)
                }
                val imgSinger1 = ShapeableImageView(parent.context).apply {
                    id = img_singer1
                    val size = context.getIntToDip(120f).toInt()
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size)
                    lp.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP;
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
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 3 * size)
                    lp.gravity = Gravity.LEFT or Gravity.TOP
                    lp.topMargin = context.getIntToDip(125f).toInt()
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = lp
                }
                frameLayout.addView(txtAuthor1)
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
                holder.itemView.findViewById<TextView>(name).text = item.kMusicItemBean!!.author

                holder.itemView.findViewById<TextView>(music_name).text = item.kMusicItemBean!!.musicName

                holder.itemView.findViewById<ShapeableImageView>(img).loadUrl(item.kMusicItemBean!!.album_sizable_cover)
            }
            2 -> {
                holder.itemView.findViewById<TextView>(music_name2).text = item.kKMusicHotSongBean!!.musicName
                holder.itemView.findViewById<ShapeableImageView>(img2).loadUrl(item.kKMusicHotSongBean!!.imgUrl)
            }
            3 -> {
                holder.itemView.findViewById<ImageView>(img3).loadUrl(item.kRankExBean!!.imgUrl)
                item.kRankExBean!!.topBean[0]?.run {
                    holder.itemView.findViewById<TextView>(txt1).text = "$no $musicName  $author"
                }
                item.kRankExBean!!.topBean[1]?.run {
                    holder.itemView.findViewById<TextView>(txt2).text = "$no $musicName  $author"
                }
                item.kRankExBean!!.topBean[2]?.run {
                    holder.itemView.findViewById<TextView>(txt3).text = "$no $musicName  $author"
                }
            }
            4 -> {
                item.kSingerBean?.run {
                    holder.itemView.findViewById<ShapeableImageView>(img_singer1).loadUrl(imgUrl)
                    holder.itemView.findViewById<TextView>(txt_author1).text = name
                }
            }
        }
    }
}