package com.wgllss.ssmusic.core.asyninflater

import android.content.Context
import android.content.MutableContextWrapper
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.annotation.UiThread
import androidx.collection.SparseArrayCompat
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.LogTimer
import java.util.concurrent.CountDownLatch


/**
 * 调用入口类；同时解决加载和获取View在不同类的场景
 */
class AsyncLayoutLoader private constructor(context: Context) {
    private var mLayoutId = 0
    private var mRealView: View? = null
    private val mContext: Context
    private var mRootView: ViewGroup? = null
    private val mCountDownLatch: CountDownLatch
    private var mInflater: AsyncLayoutInflaterPlus? = null

    @UiThread
    fun inflate(@LayoutRes resid: Int, @Nullable parent: ViewGroup?) {
        inflate(resid, parent, null)
    }

    @UiThread
    fun inflate(@LayoutRes resid: Int, @Nullable parent: ViewGroup?, listener: AsyncLayoutInflaterPlus.OnInflateFinishedListener?) {
        var listener = listener
        mRootView = parent
        mLayoutId = resid
        sArrayCompat.append(mLayoutId, this)
        if (listener == null) {
            listener = object : AsyncLayoutInflaterPlus.OnInflateFinishedListener {
                override fun onInflateFinished(view: View?, resid: Int, parent: ViewGroup?) {
                    mRealView = view;
                    LogTimer.LogE(this, "onInflateFinished")
                }
            }
        }
        mInflater = AsyncLayoutInflaterPlus(mContext)
        mInflater?.inflate(resid, parent, mCountDownLatch, listener)
    }

    /**
     * getLayoutLoader 和 getRealView 方法配对出现
     * 用于加载和获取View在不同类的场景
     *
     * @param resid
     * @return
     */
    val realView: View?
        get() {
            if (mRealView == null && mInflater!!.isRunning) {
                mInflater!!.cancel()
                inflateSync()
            } else if (mRealView == null) {
                try {
                    mCountDownLatch.await()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                setLayoutParamByParent(mContext, mRootView, mLayoutId, mRealView)
            } else {
                setLayoutParamByParent(mContext, mRootView, mLayoutId, mRealView)
            }
            replaceContextForView(mRealView!!, mContext)
            return mRealView
        }

    private fun inflateSync() {
        mRealView = LayoutInflater.from(mContext).inflate(mLayoutId, mRootView, false)
    }

    companion object {
        private val sArrayCompat = SparseArrayCompat<AsyncLayoutLoader>()
        fun getInstance(context: Context): AsyncLayoutLoader {
            return AsyncLayoutLoader(context)
        }

        /**
         * getLayoutLoader 和 getRealView 方法配对出现
         * 用于加载和获取View在不同类的场景
         *
         * @param resid
         * @return
         */
        fun getLayoutLoader(resid: Int): AsyncLayoutLoader? {
            return sArrayCompat[resid]
        }

        /**
         * 根据Parent设置异步加载View的LayoutParamsView
         *
         * @param context
         * @param parent
         * @param layoutResId
         * @param view
         */
        private fun setLayoutParamByParent(context: Context, parent: ViewGroup?, layoutResId: Int, view: View?) {
            if (parent == null) {
                return
            }
            val parser: XmlResourceParser = context.resources.getLayout(layoutResId)
            try {
                val attrs: AttributeSet = Xml.asAttributeSet(parser)
                val params = parent.generateLayoutParams(attrs)
                view?.layoutParams = params
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                parser.close()
            }
        }
    }

    init {
        mContext = context
        mCountDownLatch = CountDownLatch(1)
    }

    open fun replaceContextForView(inflatedView: View, context: Context) {
        val cxt = inflatedView.context
        if (cxt is MutableContextWrapper) {
            (cxt as MutableContextWrapper).baseContext = context
            logE("cxt -->${cxt.javaClass.simpleName}")
        }
    }
}