package com.wgllss.ssmusic.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent

class SideBar : androidx.appcompat.widget.AppCompatTextView {
    private var letters = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z", "#"
    )
    private var textPaint: Paint? = null
    private var bigTextPaint: Paint? = null
    private var scaleTextPaint: Paint? = null

    private var canvas: Canvas? = null
    private var itemH = 0
    private var w = 0
    private var h = 0

    /**
     * 普通情况下字体大小
     */
    var singleTextH = 0f

    /**
     * 缩放离原始的宽度
     */
    private var scaleWidth = dp(100).toFloat()

    /**
     * 滑动的Y
     */
    private var eventY = 0f

    /**
     * 缩放的倍数
     */
    private var scaleSize = 1

    /**
     * 缩放个数item，即开口大小
     */
    private var scaleItemCount = 6
    private var callBack: ISideBarSelectCallBack? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint!!.color = currentTextColor
        textPaint!!.textSize = textSize
        textPaint!!.textAlign = Paint.Align.CENTER
        bigTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bigTextPaint!!.color = currentTextColor
        bigTextPaint!!.textSize = textSize * (scaleSize + 3)
        bigTextPaint!!.textAlign = Paint.Align.CENTER
        scaleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scaleTextPaint!!.color = currentTextColor
        scaleTextPaint!!.textSize = textSize * (scaleSize + 1)
        scaleTextPaint!!.textAlign = Paint.Align.CENTER
    }

    fun setDataResource(data: Array<String>) {
        letters = data
        invalidate()
    }

    fun setOnStrSelectCallBack(callBack: ISideBarSelectCallBack?) {
        this.callBack = callBack
    }

    /**
     * 设置字体缩放比例
     *
     * @param scale
     */
    fun setScaleSize(scale: Int) {
        scaleSize = scale
        invalidate()
    }

    /**
     * 设置缩放字体的个数，即开口大小
     *
     * @param scaleItemCount
     */
    fun setScaleItemCount(scaleItemCount: Int) {
        this.scaleItemCount = scaleItemCount
        invalidate()
    }

    private fun dp(px: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (px * scale + 0.5f).toInt()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> return if (event.x > w - paddingRight - singleTextH - 10) {
                eventY = event.y
                invalidate()
                true
            } else {
                eventY = 0f
                invalidate()
                return super.onTouchEvent(event)
            }
            MotionEvent.ACTION_CANCEL -> {
                eventY = 0f
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> return if (event.x > w - paddingRight - singleTextH - 10) {
                eventY = 0f
                invalidate()
                true
            } else return super.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas?) {
        this.canvas = canvas
        DrawView(eventY)
    }

    private fun DrawView(y: Float) {
        var currentSelectIndex = -1
        if (y != 0f) {
            for (i in letters.indices) {
                val currentItemY = (itemH * i).toFloat()
                val nextItemY = (itemH * (i + 1)).toFloat()
                if (y >= currentItemY && y < nextItemY) {
                    currentSelectIndex = i
                    if (callBack != null) {
                        callBack!!.onSelectStr(currentSelectIndex, letters[i])
                    }
                    //画大的字母
                    val fontMetrics = bigTextPaint!!.fontMetrics
                    val bigTextSize = fontMetrics.descent - fontMetrics.ascent
                    canvas!!.drawText(letters[i], w - paddingRight - scaleWidth - bigTextSize, singleTextH + itemH * i, bigTextPaint!!)
                }
            }
        }
        drawLetters(y, currentSelectIndex)
    }

    private fun drawLetters(y: Float, index: Int) {
        //第一次进来没有缩放情况，默认画原图
        if (index == -1) {
            w = measuredWidth
            h = measuredHeight
            itemH = h / letters.size
            val fontMetrics = textPaint!!.fontMetrics
            singleTextH = fontMetrics.descent - fontMetrics.ascent
            for (i in letters.indices) {
                canvas!!.drawText(letters[i], (w - paddingRight).toFloat(), singleTextH + itemH * i, textPaint!!)
            }
            //触摸的时候画缩放图
        } else {
            //遍历所有字母
            for (i in letters.indices) {
                //要画的字母的起始Y坐标
                val currentItemToDrawY = singleTextH + itemH * i
                var centerItemToDrawY: Float
                centerItemToDrawY = if (index < i) singleTextH + itemH * (index + scaleItemCount) else singleTextH + itemH * (index - scaleItemCount)
                val delta = 1 - Math.abs((y - currentItemToDrawY) / (centerItemToDrawY - currentItemToDrawY))
                val maxRightX = (w - paddingRight).toFloat()
                //如果大于0，表明在y坐标上方
                scaleTextPaint!!.textSize = textSize + textSize * delta
                val drawX = maxRightX - scaleWidth * delta
                //超出边界直接花在边界上
                if (drawX > maxRightX) canvas!!.drawText(letters[i], maxRightX, singleTextH + itemH * i, textPaint!!) else canvas!!.drawText(letters[i], drawX, singleTextH + itemH * i, scaleTextPaint!!)
            }
        }
    }

    interface ISideBarSelectCallBack {
        fun onSelectStr(index: Int, selectStr: String)
    }
}