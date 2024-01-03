package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetListDtoPlistListItem

class KSongSheetAdapter : BaseRecyclerAdapter<KSheetListDtoPlistListItem>() {
    private val img = 1
    private val music_name = 2
    private val txtListener = 12
    private val footer by lazy { KSheetListDtoPlistListItem("", "", "", "", 1) }
    private val t2Color by lazy { Color.parseColor("#20000000") }
    private var textColorPrimary: Int = 0

    override fun getLayoutResId(viewType: Int) = 0

    fun addFooter() {
        mData.add(footer)
        notifyItemInserted(mData.size - 1)
    }

    fun removeFooter() {
        mData.removeAt(mData.size - 1)
        notifyItemRemoved(mData.size)
    }

    private fun getTextColorPrimary(context: Context): Int {
        if (textColorPrimary == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            textColorPrimary = typedValue.data
        }
        return textColorPrimary
    }

    override fun getItemViewType(position: Int) = mData[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        return if (viewType == 0) {
            val frameLayout = FrameLayout(parent.context).apply {
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
            }
            val image = ShapeableImageView(parent.context).apply {
                id = img
                val size = context.getIntToDip(120f).toInt()
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
                    topMargin = context.getIntToDip(125f).toInt()
                }
                maxLines = 2
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            }
            frameLayout.addView(textViewMusicName)
            val txtListeners = MaterialButton(parent.context).apply {
                id = txtListener
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, context.getIntToDip(25f).toInt()).apply {
                    gravity = Gravity.RIGHT or Gravity.TOP
                    topMargin = context.getIntToDip(80f).toInt()
                }
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
                maxLines = 1
                insetTop = 0
                insetBottom = 0
                setPadding(context.getIntToDip(6f).toInt(), 0, 0, 0)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                val colors = intArrayOf(t2Color, t2Color)
                val states = arrayOfNulls<IntArray>(2)
                states[0] = intArrayOf(android.R.attr.state_pressed)
                states[1] = intArrayOf(android.R.attr.state_enabled)
                backgroundTintList = ColorStateList(states, colors)
                cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, context.resources.displayMetrics).toInt()// else 0
            }
            frameLayout.addView(txtListeners)
            BaseBindingViewHolder(frameLayout)
        } else {
            BaseBindingViewHolder(ProgressBar(context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = context.getIntToDip(5f).toInt()
                }
            })
        }
    }

    override fun onBindItem(context: Context, item: KSheetListDtoPlistListItem, holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            holder.itemView.findViewById<TextView>(music_name).text = item.specialname
            holder.itemView.findViewById<TextView>(txtListener).apply {
                text = item.play_count_text
                val res = context.resources
                val leftDrawable = res.getDrawable(res.getIdentifier("ic_baseline_play_arrow_12", "drawable", context.packageName))
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
            }
            holder.itemView.findViewById<ShapeableImageView>(img).loadUrl(item.imgurl)
        }
    }
}