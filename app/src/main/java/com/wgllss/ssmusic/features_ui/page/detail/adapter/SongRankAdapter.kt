package com.wgllss.ssmusic.features_ui.page.detail.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.data.MusicItemBean
import kotlin.random.Random

class SongRankAdapter : BaseRecyclerAdapter<MusicItemBean>() {
    private var cornerRadiusInt: Int = 0
    private var textColorHighlight: Int = 0

    private fun getTextHightColorPrimary(context: Context): Int {
        if (textColorHighlight == 0) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorHighlight, typedValue, true)
            textColorHighlight = typedValue.data
        }
        return textColorHighlight
    }

    private val array = arrayOf(
        com.wgllss.music.skin.R.color.color_random_0, com.wgllss.music.skin.R.color.color_random_1, com.wgllss.music.skin.R.color.color_random_2,
        com.wgllss.music.skin.R.color.color_random_3, com.wgllss.music.skin.R.color.color_random_4, com.wgllss.music.skin.R.color.color_random_5,
        com.wgllss.music.skin.R.color.color_random_6, com.wgllss.music.skin.R.color.color_random_7, com.wgllss.music.skin.R.color.color_random_8,
        com.wgllss.music.skin.R.color.color_random_9, com.wgllss.music.skin.R.color.color_random_10, com.wgllss.music.skin.R.color.color_random_11,
    )

    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_song_rank

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        if (cornerRadiusInt == 0) {
            cornerRadiusInt = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 999f, parent.context.resources.displayMetrics).toInt()
        }
        return holder
    }

    override fun onBindItem(context: Context, item: MusicItemBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.run {
            findViewById<MaterialButton>(R.id.music_no).apply {
                setTextColor(getTextHightColorPrimary(context))
                cornerRadius = cornerRadiusInt
                gravity = Gravity.CENTER
                isClickable = false
                isFocusable = false
                background.setTint(context.getColor(array[Random.nextInt(array.size)]))
                text = "${position + 1}"
            }
            findViewById<TextView>(R.id.author).text = item.author
            findViewById<TextView>(R.id.title).text = item.musicName
            findViewById<ShapeableImageView>(R.id.img_song).run {
                loadUrl(item.album_sizable_cover)
                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                    setAllCorners(RoundedCornerTreatment())
                    setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                }.build()
            }
        }
    }
}