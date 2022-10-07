package com.wgllss.ssmusic.core.asyninflater

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pools
import androidx.core.view.LayoutInflaterCompat
import android.util.Log
import com.wgllss.ssmusic.core.ex.logE
import com.wgllss.ssmusic.core.units.ScreenManager
import java.util.concurrent.*


/**
 * 实现异步加载布局的功能，修改点：
 *
 * 1. super.onCreate之前调用没有了默认的Factory；
 * 2. 排队过多的优化；
 */
class AsyncLayoutInflaterPlus(context: Context) {
    private val mHandler: Handler
    private lateinit var mInflater: LayoutInflater
    private var mInflateRunnable: InflateRunnable? = null
    private var future: Future<*>? = null

    @UiThread
    fun inflate(@LayoutRes resid: Int, @Nullable parent: ViewGroup?, countDownLatch: CountDownLatch, callback: OnInflateFinishedListener) {
        if (callback == null) {
            throw NullPointerException("callback argument may not be null!")
        }
        val request = obtainRequest()
        request.inflater = this
        request.resid = resid
        request.parent = parent
        request.callback = callback
        request.countDownLatch = countDownLatch
        mInflateRunnable = InflateRunnable(request)
        future = sExecutor.submit(mInflateRunnable)
    }

    fun cancel() {
        future?.cancel(true)
    }

    /**
     * 判断这个任务是否已经开始执行
     *
     * @return
     */
    val isRunning: Boolean
        get() = mInflateRunnable!!.isRunning

    private val mHandlerCallback: Handler.Callback = object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            val request = msg.obj as InflateRequest
            if (request.view == null) {
                logE("request.view == null")
                request.view = mInflater.inflate(request.resid, request.parent, false)
            }
            request.callback?.onInflateFinished(request.view, request.resid, request.parent)
            request.countDownLatch?.countDown()
            releaseRequest(request)
            return true
        }
    }

    interface OnInflateFinishedListener {
        fun onInflateFinished(view: View?, resid: Int, parent: ViewGroup?)
    }

    private inner class InflateRunnable(private val request: InflateRequest) : Runnable {
        var isRunning = false
            private set

        override fun run() {
            isRunning = true
            try {
                request.view = request.inflater!!.mInflater.inflate(request.resid, request.parent, false)
                request.view?.apply {
                    if (ScreenManager.screenHeight == 0 || ScreenManager.screenWidth == 0 || ScreenManager.widthSpec == 0 || ScreenManager.heightSpec == 0) {
                        ScreenManager.initScreenSize(context)
                    }
                    measure(ScreenManager.widthSpec, ScreenManager.heightSpec)
                    layout(0, 0, ScreenManager.screenWidth, ScreenManager.screenHeight)
                }
            } catch (ex: RuntimeException) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI" + " thread", ex)
            }
            Message.obtain(request?.inflater?.mHandler, 0, request).sendToTarget()
        }

    }

    class InflateRequest internal constructor() {
        var inflater: AsyncLayoutInflaterPlus? = null
        var parent: ViewGroup? = null
        var resid = 0
        var view: View? = null
        var callback: OnInflateFinishedListener? = null
        var countDownLatch: CountDownLatch? = null
    }

    private class BasicInflater(context: Context) : LayoutInflater(context) {
        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String?, attrs: AttributeSet?): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view: View? = createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (e: ClassNotFoundException) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf("android.widget.", "android.webkit.", "android.app.")
        }

        init {
            if (context is AppCompatActivity) {
                // 加上这些可以保证AppCompatActivity的情况下，super.onCreate之前
                // 使用AsyncLayoutInflater加载的布局也拥有默认的效果
                val appCompatDelegate = (context as AppCompatActivity).delegate
                if (appCompatDelegate is Factory2) {
                    LayoutInflaterCompat.setFactory2(this, appCompatDelegate as Factory2)
                }
            }
        }
    }

    fun obtainRequest(): InflateRequest {
        var obj: InflateRequest? = sRequestPool.acquire()
        if (obj == null) {
            obj = InflateRequest()
        }
        return obj
    }

    fun releaseRequest(obj: InflateRequest) {
        obj.callback = null
        obj.inflater = null
        obj.parent = null
        obj.resid = 0
        obj.view = null
        sRequestPool.release(obj)
    }

    companion object {
        private const val TAG = "AsyncLayoutInflaterPlus"

        // 真正执行加载任务的线程池
        private val sExecutor: ExecutorService = //Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() - 2))
            ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS, LinkedBlockingDeque<Runnable>());

        // InflateRequest pool
        private val sRequestPool: Pools.SynchronizedPool<InflateRequest> = Pools.SynchronizedPool(10)
    }

    init {
        mInflater = BasicInflater(context)
        mHandler = Handler(mHandlerCallback)
    }
}