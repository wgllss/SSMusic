package com.wgllss.dynamic.lrclibrary

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class LrcView : View {

    private val TAG = "LrcView"
    private val ADJUST_DURATION: Long = 100
    private val TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS

    private val mLrcEntryList = mutableListOf<LrcEntry>()
    private val mLrcPaint = TextPaint()
    private val mTimePaint = TextPaint()
    private lateinit var mTimeFontMetrics: Paint.FontMetrics
    private var mPlayDrawable: Drawable? = null
    private var mDividerHeight = 0f
    private var mAnimationDuration: Long = 0
    private var mNormalTextColor = 0
    private var mNormalTextSize = 0f
    private var mCurrentTextColor = 0
    private var mCurrentTextSize = 0f
    private var mTimelineTextColor = 0
    private var mTimelineColor = 0
    private var mTimeTextColor = 0
    private var mDrawableWidth = 0
    private var mTimeTextWidth = 0
    private var mDefaultLabel: String? = null
    private var mLrcPadding = 0f
    private var mOnPlayClickListener: OnPlayClickListener? = null
    private var mOnTapListener: OnTapListener? = null
    private var mAnimator: ValueAnimator? = null
    private lateinit var mGestureDetector: GestureDetector
    private var mScroller: Scroller? = null
    private var mOffset = 0f
    private var mCurrentLine = 0
    private var mFlag: Any? = null
    private var isShowTimeline = false
    private var isTouching = false
    private var isFling = false

    /**
     * 歌词显示位置，靠左/居中/靠右
     */
    private var mTextGravity = 0

    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @param view 歌词控件
         * @param time 选中播放进度
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        fun onPlayClick(view: LrcView?, time: Long): Boolean
    }

    /**
     * 歌词控件点击监听器
     */
    interface OnTapListener {
        /**
         * 歌词控件被点击
         *
         * @param view 歌词控件
         * @param x    点击坐标x，相对于控件
         * @param y    点击坐标y，相对于控件
         */
        fun onTap(view: LrcView?, x: Float, y: Float)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LrcView)
        mCurrentTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, resources.getDimension(R.dimen.lrc_text_size))
        mNormalTextSize = ta.getDimension(R.styleable.LrcView_lrcNormalTextSize, resources.getDimension(R.dimen.lrc_text_size))
        if (mNormalTextSize == 0f) {
            mNormalTextSize = mCurrentTextSize
        }
        mDividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, resources.getDimension(R.dimen.lrc_divider_height))
        val defDuration = resources.getInteger(R.integer.lrc_animation_duration)
        mAnimationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration).toLong()
        mAnimationDuration = if (mAnimationDuration < 0) defDuration.toLong() else mAnimationDuration
        mNormalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, resources.getColor(R.color.lrc_normal_text_color))
        mCurrentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, resources.getColor(R.color.lrc_current_text_color))
        mTimelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, resources.getColor(R.color.lrc_timeline_text_color))
        mDefaultLabel = ta.getString(R.styleable.LrcView_lrcLabel)
        mDefaultLabel = if (TextUtils.isEmpty(mDefaultLabel)) context.getString(R.string.lrc_label) else mDefaultLabel
        mLrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0f)
        mTimelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, resources.getColor(R.color.lrc_timeline_color))
        val timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, resources.getDimension(R.dimen.lrc_timeline_height))
        mPlayDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable)
        mPlayDrawable = if (mPlayDrawable == null) resources.getDrawable(R.drawable.lrc_play) else mPlayDrawable
        mTimeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, resources.getColor(R.color.lrc_time_text_color))
        val timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, resources.getDimension(R.dimen.lrc_time_text_size))
        mTextGravity = ta.getInteger(R.styleable.LrcView_lrcTextGravity, LrcEntry.GRAVITY_CENTER)
        ta.recycle()
        mDrawableWidth = resources.getDimension(R.dimen.lrc_drawable_width).toInt()
        mTimeTextWidth = resources.getDimension(R.dimen.lrc_time_width).toInt()
        mLrcPaint.isAntiAlias = true
        mLrcPaint.textSize = mCurrentTextSize
        mLrcPaint.textAlign = Paint.Align.LEFT
        mTimePaint.isAntiAlias = true
        mTimePaint.textSize = timeTextSize
        mTimePaint.textAlign = Paint.Align.CENTER
        mTimePaint.strokeWidth = timelineHeight
        mTimePaint.strokeCap = Paint.Cap.ROUND
        mTimeFontMetrics = mTimePaint.fontMetrics
        mGestureDetector = GestureDetector(context, mSimpleOnGestureListener)
        mGestureDetector.setIsLongpressEnabled(false)
        mScroller = Scroller(context)
    }

    /**
     * 设置非当前行歌词字体颜色
     */
    fun setNormalColor(normalColor: Int) {
        mNormalTextColor = normalColor
        postInvalidate()
    }

    /**
     * 普通歌词文本字体大小
     */
    fun setNormalTextSize(size: Float) {
        mNormalTextSize = size
    }

    /**
     * 当前歌词文本字体大小
     */
    fun setCurrentTextSize(size: Float) {
        mCurrentTextSize = size
    }

    /**
     * 设置当前行歌词的字体颜色
     */
    fun setCurrentColor(currentColor: Int) {
        mCurrentTextColor = currentColor
        postInvalidate()
    }

    /**
     * 设置拖动歌词时选中歌词的字体颜色
     */
    fun setTimelineTextColor(timelineTextColor: Int) {
        mTimelineTextColor = timelineTextColor
        postInvalidate()
    }

    /**
     * 设置拖动歌词时时间线的颜色
     */
    fun setTimelineColor(timelineColor: Int) {
        mTimelineColor = timelineColor
        postInvalidate()
    }

    /**
     * 设置拖动歌词时右侧时间字体颜色
     */
    fun setTimeTextColor(timeTextColor: Int) {
        mTimeTextColor = timeTextColor
        postInvalidate()
    }

    /**
     * 设置歌词是否允许拖动
     *
     * @param draggable           是否允许拖动
     * @param onPlayClickListener 设置歌词拖动后播放按钮点击监听器，如果允许拖动，则不能为 null
     */
    fun setDraggable(draggable: Boolean, onPlayClickListener: OnPlayClickListener) {
        mOnPlayClickListener = if (draggable) {
            onPlayClickListener
        } else {
            null
        }
    }

    /**
     * 设置播放按钮点击监听器
     *
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     */
    @Deprecated("use {@link #setDraggable(boolean, OnPlayClickListener)} instead")
    fun setOnPlayClickListener(onPlayClickListener: OnPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener
    }

    /**
     * 设置歌词控件点击监听器
     *
     * @param onTapListener 歌词控件点击监听器
     */
    fun setOnTapListener(onTapListener: OnTapListener) {
        mOnTapListener = onTapListener
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    fun setLabel(label: String) {
        runOnUi {
            mDefaultLabel = label
            invalidate()
        }
    }

    /**
     * 加载歌词文件
     *
     * @param lrcFile 歌词文件
     */
    fun loadLrc(lrcFile: File) {
        loadLrc(lrcFile, null)
    }

    /**
     * 加载双语歌词文件，两种语言的歌词时间戳需要一致
     *
     * @param mainLrcFile   第一种语言歌词文件
     * @param secondLrcFile 第二种语言歌词文件
     */
    fun loadLrc(mainLrcFile: File, secondLrcFile: File?) {
        runOnUi {
            reset()
            val sb = StringBuilder("file://")
            sb.append(mainLrcFile.path)
            if (secondLrcFile != null) {
                sb.append("#").append(secondLrcFile.path)
            }
            val flag = sb.toString()
            setFlag(flag)
            val lrcArray = arrayOf(mainLrcFile, secondLrcFile)
            MainScope().launch {
                val la = async(Dispatchers.IO) {
                    LrcUtils.parseLrc(lrcArray)
                }
                if (getFlag() === flag) {
                    onLrcLoaded(la.await()!!)
                    setFlag("")
                }
            }
        }
    }

    /**
     * 加载双语歌词文本，两种语言的歌词时间戳需要一致
     *
     * @param mainLrcText   第一种语言歌词文本
     * @param secondLrcText 第二种语言歌词文本
     */
    fun loadLrc(mainLrcText: String, secondLrcText: String? = null) {
        runOnUi {
            reset()
            val sb = StringBuilder("file://")
            sb.append(mainLrcText)
            if (secondLrcText != null) {
                sb.append("#").append(secondLrcText)
            }
            val flag = sb.toString()
            setFlag(flag)
            MainScope().launch {
                val la = async(Dispatchers.IO) {
                    LrcUtils.parseLrc(mainLrcText, secondLrcText)
                }
                if (getFlag() === flag) {
                    onLrcLoaded(la.await()!!)
                    setFlag("")
                }
            }
        }
    }

    /**
     * 加载在线歌词，默认使用 utf-8 编码
     *
     * @param lrcUrl 歌词文件的网络地址
     */
    fun loadLrcByUrl(lrcUrl: String) {
        loadLrcByUrl(lrcUrl, "utf-8")
    }

    /**
     * 加载在线歌词
     *
     * @param lrcUrl  歌词文件的网络地址
     * @param charset 编码格式
     */
    fun loadLrcByUrl(lrcUrl: String, charset: String) {
        val flag = "url://$lrcUrl"
        setFlag(flag)
//        val lrcArray = arrayOf(mainLrcText, secondLrcText)
        MainScope().launch {
            val la = async(Dispatchers.IO) {
                LrcUtils.getContentFromNetwork(lrcUrl, charset)
            }
            if (getFlag() === flag) {
                loadLrc(la.await()!!)
                setFlag("")
            }
        }
    }

    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    fun hasLrc(): Boolean {
        return mLrcEntryList.isNotEmpty()
    }

    /**
     * 刷新歌词
     *
     * @param time 当前播放时间
     */
    fun updateTime(time: Long) {
        runOnUi {
            if (!hasLrc()) {
                return@runOnUi
            }
            val line = findShowLine(time)
            if (line != mCurrentLine) {
                mCurrentLine = line
                if (!isShowTimeline) {
                    smoothScrollTo(line)
                } else {
                    invalidate()
                }
            }
        }
    }

    /**
     * 将歌词滚动到指定时间
     *
     * @param time 指定的时间
     */
    fun onDrag(time: Long) {
        updateTime(time)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            initPlayDrawable()
            initEntryList()
            if (hasLrc()) {
                smoothScrollTo(mCurrentLine, 0L)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2

        // 无歌词文件
        if (!hasLrc()) {
            mLrcPaint.setColor(mCurrentTextColor)
            @SuppressLint("DrawAllocation") val staticLayout = StaticLayout(mDefaultLabel, mLrcPaint, getLrcWidth().toInt(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
            drawText(canvas, staticLayout, centerY.toFloat())
            return
        }
        val centerLine = getCenterLine()
        if (isShowTimeline) {
            mPlayDrawable?.draw(canvas)
            mTimePaint.setColor(mTimelineColor)
            canvas.drawLine(mTimeTextWidth.toFloat(), centerY.toFloat(), (width - mTimeTextWidth).toFloat(), centerY.toFloat(), mTimePaint)
            mTimePaint.setColor(mTimeTextColor)
            val timeText = LrcUtils.formatTime(mLrcEntryList.get(centerLine).time)
            val timeX: Float = (width - mTimeTextWidth / 2).toFloat()
            val timeY: Float = centerY - (mTimeFontMetrics.descent + mTimeFontMetrics.ascent) / 2
            canvas.drawText(timeText!!, timeX, timeY, mTimePaint)
        }
        canvas.translate(0f, mOffset)
        var y = 0f
        for (i in mLrcEntryList.indices) {
            if (i > 0) {
                y += (mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight() shr 1) + mDividerHeight
            }
            if (i == mCurrentLine) {
                mLrcPaint.setTextSize(mCurrentTextSize)
                mLrcPaint.setColor(mCurrentTextColor)
            } else if (isShowTimeline && i == centerLine) {
                mLrcPaint.setColor(mTimelineTextColor)
            } else {
                mLrcPaint.setTextSize(mNormalTextSize)
                mLrcPaint.setColor(mNormalTextColor)
            }
            drawText(canvas, mLrcEntryList.get(i).staticLayout!!, y)
        }
    }

    /**
     * 画一行歌词
     *
     * @param y 歌词中心 Y 坐标
     */
    private fun drawText(canvas: Canvas, staticLayout: StaticLayout, y: Float) {
        canvas.save()
        canvas.translate(mLrcPadding, y - (staticLayout.height shr 1))
        staticLayout.draw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            isTouching = false
            // 手指离开屏幕，启动延时任务，恢复歌词位置
            if (hasLrc() && isShowTimeline) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
        return mGestureDetector.onTouchEvent(event)
    }

    /**
     * 手势监听器
     */
    private val mSimpleOnGestureListener: GestureDetector.SimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        // 本次点击仅仅为了停止歌词滚动，则不响应点击事件
        private var isTouchForStopFling = false
        override fun onDown(e: MotionEvent): Boolean {
            if (!hasLrc()) {
                return mOnTapListener != null
            }
            isTouching = true
            removeCallbacks(hideTimelineRunnable)
            if (isFling) {
                isTouchForStopFling = true
                mScroller!!.forceFinished(true)
            } else {
                isTouchForStopFling = false
            }
            return mOnPlayClickListener != null || mOnTapListener != null
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!hasLrc() || mOnPlayClickListener == null) {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
            if (!isShowTimeline) {
                isShowTimeline = true
            } else {
                mOffset += -distanceY
                mOffset = Math.min(mOffset, getOffset(0))
                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size - 1))
            }
            invalidate()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (!hasLrc() || mOnPlayClickListener == null) {
                return super.onFling(e1, e2, velocityX, velocityY)
            }
            if (isShowTimeline) {
                isFling = true
                removeCallbacks(hideTimelineRunnable)
                mScroller!!.fling(0, mOffset.toInt(), 0, velocityY.toInt(), 0, 0, getOffset(mLrcEntryList.size - 1).toInt(), getOffset(0).toInt())
                return true
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (hasLrc() && mOnPlayClickListener != null && isShowTimeline && mPlayDrawable!!.getBounds().contains(e.x.toInt(), e.y.toInt())) {
                val centerLine = getCenterLine()
                val centerLineTime: Long = mLrcEntryList.get(centerLine).time
                // onPlayClick 消费了才更新 UI
                if (mOnPlayClickListener != null && mOnPlayClickListener!!.onPlayClick(this@LrcView, centerLineTime)) {
                    isShowTimeline = false
                    removeCallbacks(hideTimelineRunnable)
                    mCurrentLine = centerLine
                    invalidate()
                    return true
                }
            } else if (mOnTapListener != null && !isTouchForStopFling) {
                mOnTapListener?.onTap(this@LrcView, e.x, e.y)
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private val hideTimelineRunnable = Runnable {
        if (hasLrc() && isShowTimeline) {
            isShowTimeline = false
            smoothScrollTo(mCurrentLine)
        }
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            mOffset = mScroller!!.getCurrY().toFloat()
            invalidate()
        }
        if (isFling && mScroller!!.isFinished()) {
            isFling = false
            if (hasLrc() && !isTouching) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable)
        super.onDetachedFromWindow()
    }

    private fun onLrcLoaded(entryList: MutableList<LrcEntry>) {
        entryList?.let {
            mLrcEntryList.addAll(it)
            mLrcEntryList.sort()
        }
        initEntryList()
        invalidate()
    }

    private fun initPlayDrawable() {
        val l: Int = (mTimeTextWidth - mDrawableWidth) / 2
        val t: Int = height / 2 - mDrawableWidth / 2
        val r: Int = l + mDrawableWidth
        val b: Int = t + mDrawableWidth
        mPlayDrawable?.setBounds(l, t, r, b)
    }

    private fun initEntryList() {
        if (!hasLrc() || width == 0) {
            return
        }
        for (lrcEntry in mLrcEntryList) {
            lrcEntry.init(mLrcPaint, getLrcWidth().toInt(), mTextGravity)
        }
        mOffset = (height / 2).toFloat()
    }

    private fun reset() {
        endAnimation()
        mScroller?.forceFinished(true)
        isShowTimeline = false
        isTouching = false
        isFling = false
        removeCallbacks(hideTimelineRunnable)
        mLrcEntryList.clear()
        mOffset = 0f
        mCurrentLine = 0
        invalidate()
    }

    /**
     * 将中心行微调至正中心
     */
    private fun adjustCenter() {
        smoothScrollTo(getCenterLine(), ADJUST_DURATION)
    }

    /**
     * 滚动到某一行
     */
    private fun smoothScrollTo(line: Int) {
        smoothScrollTo(line, mAnimationDuration)
    }

    /**
     * 滚动到某一行
     */
    private fun smoothScrollTo(line: Int, duration: Long) {
        val offset = getOffset(line)
        endAnimation()
        mAnimator = ValueAnimator.ofFloat(mOffset, offset).apply {
            setDuration(duration)
            interpolator = LinearInterpolator()
            addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator ->
                mOffset = animation.animatedValue as Float
                invalidate()
            })
        }
        LrcUtils.resetDurationScale()
        mAnimator?.start()
    }

    /**
     * 结束滚动动画
     */
    private fun endAnimation() {
        mAnimator?.end()
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private fun findShowLine(time: Long): Int {
        var left = 0
        var right: Int = mLrcEntryList.size
        while (left <= right) {
            val middle = (left + right) / 2
            val middleTime: Long = mLrcEntryList.get(middle).time
            if (time < middleTime) {
                right = middle - 1
            } else {
                if (middle + 1 >= mLrcEntryList.size || time < mLrcEntryList.get(middle + 1).time) {
                    return middle
                }
                left = middle + 1
            }
        }
        return 0
    }

    /**
     * 获取当前在视图中央的行数
     */
    private fun getCenterLine(): Int {
        var centerLine = 0
        var minDistance = Float.MAX_VALUE
        for (i in mLrcEntryList.indices) {
            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(mOffset - getOffset(i))
                centerLine = i
            }
        }
        return centerLine
    }

    /**
     * 获取歌词距离视图顶部的距离
     * 采用懒加载方式
     */
    private fun getOffset(line: Int): Float {
        if (mLrcEntryList.get(line).offset === Float.MIN_VALUE) {
            var offset = (height / 2).toFloat()
            for (i in 1..line) {
                offset -= (mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight() shr 1) + mDividerHeight
            }
            mLrcEntryList.get(line).offset = offset
        }
        return mLrcEntryList.get(line).offset
    }

    /**
     * 获取歌词宽度
     */
    private fun getLrcWidth(): Float {
        return width - mLrcPadding * 2
    }

    /**
     * 在主线程中运行
     */
    private fun runOnUi(r: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run()
        } else {
            post(r)
        }
    }

    private fun getFlag(): Any? {
        return mFlag
    }

    private fun setFlag(flag: Any) {
        this.mFlag = flag
    }

}