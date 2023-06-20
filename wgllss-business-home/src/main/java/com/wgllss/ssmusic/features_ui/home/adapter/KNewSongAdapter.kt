package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.wgllss.ssmusic.data.MusicItemBean

class KNewSongAdapter : BaseRecyclerAdapter<MusicItemBean>() {
    private val id1 = 1
    private val id2 = 2
    private val id3 = 3

    private var textColorHighlight: Int = 0
    private var textColorPrimary: Int = 0
    private var cornerRadiusInt: Int = 0
    private val textColor by lazy { Color.parseColor("#999999") }
    private fun getTextHightColorPrimary(context: Context): Int {
        if (textColorHighlight == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorHighlight, typedValue, true)
            textColorHighlight = typedValue.data
        }
        return textColorHighlight
    }

    private fun getTextColorPrimary(context: Context): Int {
        if (textColorPrimary == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            textColorPrimary = typedValue.data
        }
        return textColorPrimary
    }

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val frameLayout = FrameLayout(context!!).apply {
            val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, context.getIntToDip(88f).toInt())
            layoutParams = lp
            val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
            val typedValue = TypedValue()
            val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
            foreground = attr.getDrawable(0)!!
            attr.recycle()
            isClickable = true
            isFocusable = true
            val size = context.getIntToDip(20f).toInt()
            setPadding(size, 0, size, 0)
        }
        val image = ShapeableImageView(parent.context).apply {
            id = id1
            val size = context.getIntToDip(80f).toInt()
            val lp = LinearLayout.LayoutParams(size, size)
            lp.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            layoutParams = lp
            scaleType = ImageView.ScaleType.FIT_XY
            shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setAllCorners(RoundedCornerTreatment())
                setAllCornerSizes(context.getIntToDip(40f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
            }.build()
        }
        frameLayout.addView(image)
        val materialTextView = TextView(context).apply {
            id = id2// com.wgllss.ssmusic.R.id.sample_hz
            val size = context.getIntToDip(20f).toInt()
            val w = context.getIntToDip(150f).toInt()
            val lp = FrameLayout.LayoutParams(w, size)
            lp.gravity = Gravity.TOP or Gravity.LEFT
            lp.leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90f, context.resources.displayMetrics).toInt()
            lp.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, context.resources.displayMetrics).toInt()
            layoutParams = lp
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        }
        frameLayout.addView(materialTextView)
        val textViewName = TextView(context).apply {
            id = id3//com.wgllss.ssmusic.R.id.mater_music_name
            val size = context.getIntToDip(30f).toInt()
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size)
            lp.gravity = Gravity.TOP or Gravity.LEFT
            lp.leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90f, context.resources.displayMetrics).toInt()
            lp.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics).toInt()
            layoutParams = lp
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(getTextColorPrimary(context))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            maxLines = 1
        }
        frameLayout.addView(textViewName)
        return BaseBindingViewHolder(frameLayout)
    }

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.run {
            findViewById<ShapeableImageView>(id1).apply {
                loadUrl(item.album_sizable_cover)
                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                    setAllCorners(RoundedCornerTreatment())
                    setAllCornerSizes(context.getIntToDip(40f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                }.build()
            }
            findViewById<TextView>(id2).text = item.author
            findViewById<TextView>(id3).text = item.musicName
        }
    }
}