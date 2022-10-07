package com.wgllss.ssmusic.core.asyninflater

import android.content.Context
import android.content.MutableContextWrapper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.UiThread
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.LogTimer
import java.util.concurrent.*
import kotlin.system.measureTimeMillis

/**
 * Created by jun xu on 4/1/21
 *
 *
 * 用来提供子线程inflate view的功能，避免某个view层级太深太复杂，主线程inflate会耗时很长，
 * 实就是对 AsyncLayoutInflater进行了抽取和封装
 */
class AsyncInflateManager private constructor() {
    //保存inflateKey以及InflateItem，里面包含所有要进行inflate的任务
    private val mInflateMap: ConcurrentHashMap<String, AsyncInflateItem?> = ConcurrentHashMap()
    private val mInflateLatchMap: ConcurrentHashMap<String, CountDownLatch> = ConcurrentHashMap()

    companion object {
        private const val TAG = "AsyncInflateManager"

        private var screenWidth = 0
        private var screenHeight = 0
        private var widthSpec = 0
        private var heightSpec = 0
        private val cpuCount = Runtime.getRuntime().availableProcessors()
        val threadPool = ThreadPoolExecutor(cpuCount, cpuCount * 2, 5, TimeUnit.SECONDS, LinkedBlockingDeque()).apply {
            allowCoreThreadTimeOut(true)
        }

        @JvmStatic
        val instance by lazy { AsyncInflateManager() }

        /**
         * 空方法，为了可以提前加载 AsyncInflateManager，并初始化 mThreadPool
         */
        fun init() {

        }

        fun initScreenSize(context: Context) {
            val metric = DisplayMetrics()
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            manager.defaultDisplay.getRealMetrics(metric)
            screenWidth = metric.widthPixels
            screenHeight = metric.heightPixels
            widthSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY)
            heightSpec = View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY)
            LogTimer.LogE(this, "initScreenSize")
        }
    }

    /**
     * 用来获得异步inflate出来的view
     *
     * @param context
     * @param layoutResId 需要拿的layoutId
     * @param parent      container
     * @param inflateKey  每一个View会对应一个inflateKey，因为可能许多地方用的同一个 layout，但是需要inflate多个，用InflateKey进行区分
     * @param inflater    外部传进来的inflater，外面如果有inflater，传进来，用来进行可能的SyncInflate，
     * @return 最后inflate出来的view
     */
    @UiThread
    fun getInflatedView(context: Context?, layoutResId: Int, parent: ViewGroup?, inflateKey: String?, inflater: LayoutInflater): View {
        if (!TextUtils.isEmpty(inflateKey) && mInflateMap.containsKey(inflateKey)) {
            val item = mInflateMap[inflateKey]
            val latch = mInflateLatchMap[inflateKey]
            if (item != null) {
                val resultView = item.inflatedView
                if (resultView != null) {
                    //拿到了view直接返回
                    removeInflateKey(item)
                    replaceContextForView(resultView, context)
//                    Log.i(TAG, "getInflatedView from cache: inflateKey is $inflateKey")
                    return resultView
                }

                if (item.isInflating() && latch != null) {
                    //没拿到view，但是在inflate中，等待返回
                    try {
                        logE("没拿到view，但是在inflate中，等待返回 ")
                        val time = measureTimeMillis {
                            latch.await()
                        }
                        logE("没拿到view，但是在inflate中，等待返回 等待时间: ${time} ms")
                    } catch (e: InterruptedException) {
//                        Log.e(TAG, e.message, e)
                    }

                    val inflatedView = item.inflatedView
                    if (inflatedView != null) {
                        removeInflateKey(item)
//                        Log.i(TAG, "getInflatedView from OtherThread: inflateKey is $inflateKey")
                        replaceContextForView(inflatedView, context)
                        return inflatedView
                    }
                }
                //如果还没开始inflate，则设置为false，UI线程进行inflate
                item.setCancelled(true)
            }
        }
//        Log.i(TAG, "getInflatedView from UI: inflateKey is $inflateKey")
        //拿异步inflate的View失败，UI线程inflate
        return inflater.inflate(layoutResId, parent, false)
    }

    /**
     * 异步里面拿布局，不需要主线程 等待阻塞
     */
    fun getAsynInflatedView(context: Context?, inflateKey: String?, asyncInflateFinlish: OnInflateFinishListener) {
        threadPool.execute {
            if (!TextUtils.isEmpty(inflateKey) && mInflateMap.containsKey(inflateKey)) {
                val item = mInflateMap[inflateKey]
                val latch = mInflateLatchMap[inflateKey]
                if (item != null) {
                    val resultView = item.inflatedView
                    if (resultView != null) {
                        //拿到了view直接返回
                        removeInflateKey(item)
                        replaceContextForView(resultView, context)
                        ThreadUtils.runOnUiThread {
                            asyncInflateFinlish?.onInflateFinished(resultView)
                        }
                    }
                    if (item.isInflating() && latch != null) {
                        //没拿到view，但是在inflate中，等待返回
                        try {
                            latch.await()
                        } catch (e: InterruptedException) {
                        }
                        val inflatedView = item.inflatedView
                        if (inflatedView != null) {
                            removeInflateKey(item)
                            replaceContextForView(inflatedView, context)
                            ThreadUtils.runOnUiThread {
                                asyncInflateFinlish?.onInflateFinished(inflatedView)
                            }
                        }
                    }
                    //如果还没开始inflate，则设置为false，UI线程进行inflate
                    item.setCancelled(true)
                }
            }
        }
    }

    /**
     * 如果  inflater初始化时是传进来的application，inflate出来的 view 的 context 没法用来 startActivity，
     * 因此用 MutableContextWrapper 进行包装，后续进行替换
     */
    private fun replaceContextForView(inflatedView: View?, context: Context?) {
        if (inflatedView == null || context == null) {
            return
        }
        val cxt = inflatedView.context
        if (cxt is MutableContextWrapper) {
            cxt.baseContext = context
        }
    }


    fun asyncInflate(context: Context, vararg items: AsyncInflateItem?) {
        items.forEach { item ->
            if (item == null || item.layoutResId == 0 || mInflateMap.containsKey(item.inflateKey) || item.isCancelled() || item.isInflating()) {
                return
            }
            mInflateMap[item.inflateKey] = item
            onAsyncInflateReady(item)
            inflateWithThreadPool(context, item)
        }
    }

    fun cancel() {

    }

    fun remove(vararg inflateKey: String) {
        inflateKey?.forEach { key ->
            mInflateMap
                ?.takeIf { it.containsKey(key) }
                ?.remove(key)
                .takeIf { it?.parent != null }
                ?.apply {
                    parent?.removeView(inflatedView)
                }
            mInflateLatchMap?.takeIf { it.containsKey(key) }?.remove(key)
        }
    }

    private fun onAsyncInflateReady(item: AsyncInflateItem) {}

    private fun onAsyncInflateStart(item: AsyncInflateItem) {}

    private fun onAsyncInflateEnd(item: AsyncInflateItem, success: Boolean) {
        item.setInflating(false)
        val latch = mInflateLatchMap[item.inflateKey]
        latch?.countDown()
        if (success && item.callback != null) {
            removeInflateKey(item)
            if (item.isCancelled()) { // 已经取消了，不再回调
                return
            }
            ThreadUtils.runOnUiThread { item.callback?.onInflateFinished(item) }
        }
    }

    private fun removeInflateKey(item: AsyncInflateItem) {
//        remove(item.inflateKey)
    }

    private fun inflateWithThreadPool(context: Context, item: AsyncInflateItem) {
        threadPool.execute {
            if (!item.isInflating() && !item.isCancelled()) {
                try {
                    onAsyncInflateStart(item)
                    item.setInflating(true)
                    mInflateLatchMap[item.inflateKey] = CountDownLatch(1)
                    val currentTimeMillis = System.currentTimeMillis()
                    item.inflatedView = BasicInflater(context).inflate(item.layoutResId, item.parent, false)
                    if (screenHeight == 0 || screenWidth == 0 || widthSpec == 0 || heightSpec == 0) {
                        initScreenSize(context)
                    }
                    item.inflatedView?.measure(widthSpec, heightSpec)
                    item.inflatedView?.layout(0, 0, screenWidth, screenHeight)
                    onAsyncInflateEnd(item, true)
                    val l = System.currentTimeMillis() - currentTimeMillis
                    LogTimer.LogE(this@AsyncInflateManager, "inflateWithThreadPool: inflateKey is ${item.inflateKey}, time is ${l}")
//                    Log.i(TAG, "inflateWithThreadPool: inflateKey is ${item.inflateKey}, time is ${l}")
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    LogTimer.LogE(this@AsyncInflateManager,e.message.toString())
//                    Log.e(TAG, "Failed to inflate resource in the background! Retrying on the UI thread", e)
                    onAsyncInflateEnd(item, false)
                }
            }
        }
    }

    /**
     * copy from AsyncLayoutInflater - actual inflater
     */
    private class BasicInflater(context: Context?) : LayoutInflater(context) {
        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = this.createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (ignored: ClassNotFoundException) {
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf("android.widget.", "android.webkit.", "android.app.")
        }
    }
}