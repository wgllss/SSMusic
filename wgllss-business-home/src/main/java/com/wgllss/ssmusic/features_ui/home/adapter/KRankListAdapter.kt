package com.wgllss.ssmusic.features_ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.wgllss.core.adapter.BaseRecyclerAdapter
import com.wgllss.core.ex.getIntToDip
import com.wgllss.core.ex.loadUrl
import com.wgllss.ssmusic.datasource.netbean.rank.KRankBean

class KRankListAdapter : BaseRecyclerAdapter<KRankBean>() {

    private val rankImgID = 3

    override fun getLayoutResId(viewType: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        if (context == null) {
            context = parent.context
        }
        val imgRank = ShapeableImageView(parent.context).apply {
            id = rankImgID
            val size = context.getIntToDip(120f).toInt()
            val lp = RecyclerView.LayoutParams(size, size)
            layoutParams = lp
            scaleType = ImageView.ScaleType.FIT_XY
            shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
                setAllCorners(RoundedCornerTreatment())
                setAllCornerSizes(context.getIntToDip(8f)) //设置圆， 40为正方形边长 80 一半，等于半径 ，需要注意单位
            }.build()
        }
        return BaseBindingViewHolder(imgRank)
    }

    override fun onBindItem(context: Context, item: KRankBean, holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.findViewById<ShapeableImageView>(rankImgID)?.loadUrl(item.imgUrl)
    }

    override fun onViewRecycled(holder: BaseBindingViewHolder) {
//        super.onViewRecycled(holder)
        context?.let { Glide.with(it).clear(holder.itemView.findViewById<ShapeableImageView>(rankImgID)) }
    }
}