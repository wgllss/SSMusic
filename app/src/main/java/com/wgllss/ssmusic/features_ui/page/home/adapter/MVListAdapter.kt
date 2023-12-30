package com.wgllss.ssmusic.features_ui.page.home.adapter


import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.datasource.netbean.mv.KMVItem

class MVListAdapter : BaseRecyclerAdapter<KMVItem>() {
    private val img = 1
    private val titleName = 2
    private val play = 3
    private val footer by lazy { KMVItem("", "", "", 1) }

    override fun getLayoutResId(viewType: Int): Int {
        return R.layout.adapter_item_2_hot
    }

    fun addFooter() {
        mData.add(footer)
        notifyItemInserted(mData.size - 1)
    }

    fun removeFooter() {
        mData.removeAt(mData.size - 1)
        notifyItemRemoved(mData.size)
    }

    override fun getItemViewType(position: Int) = mData[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        if (viewType == 0) {
            val frameLayout = FrameLayout(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(170f).toInt())
                val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
                val typedValue = TypedValue()
                val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
                foreground = attr.getDrawable(0)!!
                attr.recycle()
                isClickable = true
                isFocusable = true
//                gravity = Gravity.CENTER_HORIZONTAL
//                orientation = LinearLayout.VERTICAL
                val size = context.getIntToDip(6f).toInt()
                setPadding(size, size, size, size)
            }
            val imgHeight = parent.context.getIntToDip(120f).toInt()
            val image = ShapeableImageView(parent.context).apply {
                id = img
                scaleType = ImageView.ScaleType.FIT_XY
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imgHeight)
                layoutParams = lp
                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                    setAllCorners(RoundedCornerTreatment())
                    setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                }.build()
            }
            frameLayout.addView(image)
            val textViewMusicName = TextView(context).apply {
                id = titleName
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.gravity = Gravity.CENTER_HORIZONTAL
                lp.topMargin = imgHeight
                maxLines = 2
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                layoutParams = lp
            }
            frameLayout.addView(textViewMusicName)
            val imgPlay = ImageView(parent.context).apply {
                id = play
                val size = context.getIntToDip(45f).toInt()
                layoutParams = FrameLayout.LayoutParams(size, size).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = imgHeight / 2 - size / 2
                }
//                val padding = context.getIntToDip(3f).toInt()
//                setPadding(padding, padding, padding, padding)
            }
            frameLayout.addView(imgPlay)
            return BaseBindingViewHolder(frameLayout)
        } else {
            return BaseBindingViewHolder(ProgressBar(context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = context.getIntToDip(5f).toInt()
                }
            })
        }
    }

    override fun onBindItem(context: Context, item: KMVItem, holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            holder.itemView.findViewById<TextView>(titleName).text = item.title
            holder.itemView.findViewById<ShapeableImageView>(img).loadUrl(item.imgUrl)
            val res = context.resources
            holder.itemView.findViewById<ImageView>(play).apply {
                setImageDrawable(res.getDrawable(res.getIdentifier("play_btn_play_selector", "drawable", context.packageName)))
//                background = res.getDrawable(res.getIdentifier("oval_play", "drawable", context.packageName))
            }
        }
    }
}