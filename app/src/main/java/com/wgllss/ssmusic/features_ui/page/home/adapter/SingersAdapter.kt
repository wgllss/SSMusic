package com.wgllss.ssmusic.features_ui.page.home.adapter

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.R
import com.wgllss.ssmusic.datasource.netbean.singer.KSingerItem

class SingersAdapter : BaseRecyclerAdapter<KSingerItem>() {
    override fun getLayoutResId(viewType: Int) = R.layout.adapter_item_singer

    override fun onBindItem(context: Context, item: KSingerItem, holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<ShapeableImageView>(R.id.img_singer_item).apply {
                loadUrl(item.imgurl)
                shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                    setAllCorners(RoundedCornerTreatment())
                    setAllCornerSizes(context.getIntToDip(40f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
                }.build()
            }
            findViewById<TextView>(R.id.txt_name).text = item.singername
        }
    }

    fun isGroupHead(position: Int): Boolean {
        return if (position == 0) {
            true
        } else {
            val currentGroup = getGroupName(position)
            val preGroup = getGroupName(position - 1)
            currentGroup != preGroup
        }
    }

    fun getLetterPosition(letter: String): Int {
        var position = 0
        mData?.forEach {
            if (letter == it.title) {
                position = it.titlePosition
                return@forEach
            }
        }
        return position
    }

    /**
     * 获取组名
     */
    fun getGroupName(position: Int): String = mData[position].title

}