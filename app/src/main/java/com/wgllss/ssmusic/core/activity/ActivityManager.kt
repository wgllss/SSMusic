package com.wgllss.ssmusic.core.activity

import android.app.Activity
import android.os.Process
import java.util.*

class ActivityManager {
    private lateinit var activityStack: Stack<Activity>

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ActivityManager() }
    }


    fun getActivityStack() = activityStack

    /**
     * 指定activity退出栈
     *
     * @param activity
     * @author :Atar
     * @createTime:2011-9-5下午3:31:00
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun popActivity(activity: Activity?) = activity?.apply {
        if (instance::activityStack.isInitialized) {
            activityStack?.remove(this)
        }

    }

    /**
     * 得到栈顶activity
     *
     * @return
     * @author :Atar
     * @createTime:2011-9-5下午3:32:20
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun currentActivity() = activityStack?.takeIf { !it.empty() }?.lastElement()

    /**
     * 将当前activity压入栈
     *
     * @param activity
     * @author :Atar
     * @createTime:2011-9-5下午3:33:13
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun pushActivity(activity: Activity) {
        if (!this::activityStack.isInitialized) {
            activityStack = Stack()
        }
        activityStack.add(activity)
    }

    /**
     * 除指定的activity其余退出栈
     *
     * @param cls
     * @author :Atar
     * @createTime:2011-9-5下午3:30:06
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun <A : Activity?> popAllActivityExceptOne(cls: Class<A>) {
        if (!this::activityStack.isInitialized) {
            return
        }
        try {
            for (i in activityStack.indices.reversed()) {
                try {
                    activityStack[i].takeIf {
                        it.javaClass != null && it.javaClass != cls
                    }?.apply {
                        finish()
                        activityStack.removeAt(i)
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 退出所有activity
     *
     * @author :Atar
     * @createTime:2011-9-5下午3:28:39
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun popAllActivity() {
        while (true) {
            val activity = currentActivity() ?: break
            activity.takeIf { !it.isFinishing }
                ?.apply {
                    finish()
                    popActivity(this)
                }
        }
    }

    /**
     * 得到前一个activity
     *
     * @return
     * @author :Atar
     * @createTime:2014-9-5下午2:18:44
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun getPreviousActivity(): Activity? {
        return if (activityStack == null || activityStack.size == 0 || activityStack.size < 2) {
            null
        } else activityStack[activityStack.size - 2]
    }

    /**
     * 倒数第几个Activity
     *
     * @param lastPosition
     * @return
     * @author :Atar
     * @createTime:2016-6-16下午7:30:58
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun getActivity(lastPosition: Int): Activity? {
        return if (activityStack == null || activityStack.size == 0 || activityStack.size < lastPosition) {
            null
        } else activityStack[activityStack.size - lastPosition]
    }

    /**
     * 得到指定activity
     *
     * @param cls
     * @return
     * @author :Atar
     * @createTime:2014-9-5下午2:21:04
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun <A : Activity?> getActivity(cls: Class<A>): A? {
        if (activityStack == null) {
            return null
        }
        var a: A? = null
        for (i in activityStack.indices.reversed()) {
            try {
                if (activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == cls) {
                    a = activityStack[i] as A?
                    break
                }
            } catch (e: Exception) {
            }
        }
        return a
    }

    /**
     * 得到指定activity 倒数第几个 从倒数第0开始算
     *
     * @param cls
     * @return
     * @author :Atar
     * @createTime:2014-9-5下午2:21:04
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun <A : Activity?> getActivity(cls: Class<A>, lastPosition: Int): A? {
        if (activityStack == null) {
            return null
        }
        var a: A? = null
        var tempLastPosition = 0
        for (i in activityStack.indices.reversed()) {
            try {
                if (activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == cls) {
                    if (tempLastPosition == lastPosition) {
                        a = activityStack[i] as A?
                        break
                    }
                    tempLastPosition++
                }
            } catch (e: Exception) {
            }
        }
        return a
    }

    /**
     * 关闭指定activity同时也将此activity退出栈
     *
     * @param cls
     * @author :Atar
     * @createTime:2014-9-5下午2:32:12
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description: 从最后算起，若有相同的关闭最后一个 用i--
     */
    fun <A : Activity?> finishActivity(cls: Class<A>) {
        if (activityStack == null) {
            return
        }
        for (i in activityStack.indices.reversed()) {
            try {
                if (activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == cls) {
                    activityStack[i]!!.finish()
                    activityStack.removeAt(i)
                    break
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * 关闭指定Activity
     *
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        try {
            if (activity != null) {
                finishActivity(activity.javaClass)
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 关闭两个Activity
     *
     * @param clsA
     * @param clsB
     * @author :Atar
     * @createTime:2015-11-9下午4:58:15
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun <A : Activity?, B : Activity?> finishActivity2(clsA: Class<A>, clsB: Class<B>) {
        if (activityStack == null) {
            return
        }
        var flag = 0
        for (i in activityStack.indices.reversed()) {
            if (flag == 2) {
                break
            }
            try {
                if (activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == clsA) {
                    activityStack[i]!!.finish()
                    activityStack.removeAt(i)
                    flag++
                } else if (activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == clsB) {
                    activityStack[i]!!.finish()
                    activityStack.removeAt(i)
                    flag++
                }
            } catch (e: Exception) {
            }
        }
    }

    fun finishActivity3(vararg clsA: Class<*>) {
        if (activityStack == null) {
            return
        }
        for (k in clsA.size - 1 downTo 0) {
            for (i in activityStack.indices.reversed()) {
                try {
                    if (activityStack.size > i && activityStack[i] != null && activityStack[i]!!.javaClass != null && activityStack[i]!!.javaClass == clsA[k]) {
                        activityStack[i]!!.finish()
                        activityStack.removeAt(i)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    /**
     * 关闭当前activity同时退出栈
     *
     * @author :Atar
     * @createTime:2014-9-5下午3:27:55
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun finishCurrentActivity() {
        if (activityStack != null && activityStack.size > 0) {
            activityStack[activityStack.size - 1]!!.finish()
            activityStack.removeAt(activityStack.size - 1)
        }
    }


    /**
     * 退出程序
     *
     * @author :Atar
     * @createTime:2016-8-18下午2:30:03
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    fun exitApplication() {
        object : Thread() {
            override fun run() {
                super.run()
                popAllActivity()
                Process.killProcess(Process.myPid())
                System.exit(0) // 常规java、c#的标准退出法，返回值为0代表正常退出
            }
        }.start()
    }
}