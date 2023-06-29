package com.wgllss.ssmusic.features_ui.page.classics.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.music.skin.R
import com.wgllss.ssmusic.datasource.netbean.sheet.KSheetListDtoPlistListItem
import kotlin.random.Random

class HomeMusicAdapter : BaseRecyclerAdapter<MusicItemBean>() {

    private val id1 = 1
    private val id2 = 2
    private val id3 = 3

    private var textColorHighlight: Int = 0
    private var textColorPrimary: Int = 0
    private var cornerRadiusInt: Int = 0
    private val textColor by lazy { Color.parseColor("#999999") }

    private val footer by lazy { MusicItemBean("", "", "", "", viewType = 1) }

    fun addFooter() {
        mData.add(footer)
        notifyItemInserted(mData.size - 1)
    }

    fun removeFooter() {
        mData.removeAt(mData.size - 1)
        notifyItemRemoved(mData.size)
    }

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


    private val array = arrayOf(
        R.color.color_random_0, R.color.color_random_1, R.color.color_random_2,
        R.color.color_random_3, R.color.color_random_4, R.color.color_random_5,
        R.color.color_random_6, R.color.color_random_7, R.color.color_random_8,
        R.color.color_random_9, R.color.color_random_10, R.color.color_random_11,
    )

    override fun getItemViewType(position: Int) = mData[position].viewType

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        return if (viewType == 0) {
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
            val materialButton = MaterialButton(context!!).apply {
                id = id1//com.wgllss.ssmusic.R.id.author
                val size = context.getIntToDip(80f).toInt()
                val lp = FrameLayout.LayoutParams(size, size)
                lp.gravity = Gravity.TOP or Gravity.LEFT
                layoutParams = lp
                isClickable = false
                isFocusable = false
                insetBottom = 0
                insetTop = 0
                setTextColor(getTextHightColorPrimary(context))
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                cornerRadius = if (cornerRadiusInt == 0) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 999f, context.resources.displayMetrics).toInt() else 0
            }
            frameLayout.addView(materialButton)
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
            BaseBindingViewHolder(frameLayout)
        } else {
            BaseBindingViewHolder(ProgressBar(context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = context.getIntToDip(5f).toInt()
                }
            })
        }
    }

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            holder.itemView.findViewById<MaterialButton>(id1).apply {
                text = item.author
                background.setTint(context.getColor(array[Random.nextInt(array.size)]))
            }
            holder.itemView.findViewById<TextView>(id3).text = item.musicName
            holder.itemView.findViewById<TextView>(id2).text = item.author
        }
    }
}