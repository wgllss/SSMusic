package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.data.MusicItemBean

class PinDaoAdapter : BaseRecyclerAdapter<MusicItemBean>() {

    private val img = 1
    private val music_name = 2

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val frameLayout = FrameLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(145f).toInt())
            val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
            val typedValue = TypedValue()
            val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
            foreground = attr.getDrawable(0)!!
            attr.recycle()
            val size = context.getIntToDip(8f).toInt()
            setPadding(size, size, size, size)
            isClickable = true
            isFocusable = true
        }
        val image = ShapeableImageView(parent.context).apply {
            id = img
            val size = context.getIntToDip(85f).toInt()
            scaleType = ImageView.ScaleType.FIT_XY
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            }
            shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setAllCorners(RoundedCornerTreatment())
                setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
            }.build()
        }
        frameLayout.addView(image)
        val textViewMusicName = TextView(context).apply {
            id = music_name
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                topMargin = context.getIntToDip(90f).toInt()
            }
            maxLines = 2
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        }
        frameLayout.addView(textViewMusicName)
        return BaseBindingViewHolder(frameLayout)
    }

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(music_name).text = item.musicName
        holder.itemView.findViewById<ShapeableImageView>(img).loadUrl(item.album_sizable_cover)
    }

}