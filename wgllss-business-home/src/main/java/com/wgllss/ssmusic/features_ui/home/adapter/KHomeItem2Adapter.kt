package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.datasource.netbean.KMusicHotSongBean

class KHomeItem2Adapter : BaseRecyclerAdapter<KMusicHotSongBean>() {
    private val img = 1
    private val music_name = 2

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val linearLayout = LinearLayout(context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(170f).toInt())
            val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
            val typedValue = TypedValue()
            val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
            foreground = attr.getDrawable(0)!!
            attr.recycle()
            isClickable = true
            isFocusable = true
            gravity = Gravity.CENTER_HORIZONTAL
            orientation = LinearLayout.VERTICAL
        }
        val image = ShapeableImageView(parent.context).apply {
            id = img
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
            id = music_name
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            maxLines = 2
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            layoutParams = lp
        }
        linearLayout.addView(textViewMusicName)
        return BaseBindingViewHolder(linearLayout)
    }

    override fun onBindItem(context: Context, item: KMusicHotSongBean, holder: RecyclerView.ViewHolder, position: Int) {
//        holder.itemView.findViewById<TextView>(R.id.name).apply {
//            text = item.author
//        }
        holder.itemView.findViewById<TextView>(music_name).text = item.musicName
        holder.itemView.findViewById<ShapeableImageView>(img).loadUrl(item.imgUrl)

    }
}