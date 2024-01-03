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
import com.wgllss.ssmusic.datasource.netbean.pindao.PinDaoSideBean

class PinDaoSideAdapter : BaseRecyclerAdapter<PinDaoSideBean>() {
    private var currentPosition: Int = 0
    private var colorInt: Int = 0
    private var colorPrimary: Int = 0
    private fun getAndroidColorBackground(context: Context): Int {
        if (colorInt == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            colorInt = typedValue.data
        }
        return colorInt
    }

    private fun getColorPrimary(context: Context): Int {
        if (colorPrimary == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
            colorPrimary = typedValue.data
        }
        return colorPrimary
    }

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val size = parent.context.getIntToDip(5.0f).toInt()
        val textViewMusicName = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(14 * size, 8 * size).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            }
            gravity = Gravity.CENTER_VERTICAL
            maxLines = 1
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            val array: IntArray = intArrayOf(android.R.attr.selectableItemBackground)
            val typedValue = TypedValue()
            val attr = context.theme.obtainStyledAttributes(typedValue.resourceId, array)
            foreground = attr.getDrawable(0)!!
            attr.recycle()
            setPadding(4 * size, 0, 0, 0)
            isClickable = true
            isFocusable = true
        }
        return BaseBindingViewHolder(textViewMusicName)
    }

    override fun onBindItem(context: Context, item: PinDaoSideBean, holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            text = item.name
            setTextColor(if (position == currentPosition) getColorPrimary(context!!) else getAndroidColorBackground(context!!))
        }
    }
}