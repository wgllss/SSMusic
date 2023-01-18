package com.wgllss.ssmusic.features_system.startup

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.wgllss.ssmusic.core.activity.ActivityManager
import com.wgllss.ssmusic.core.units.LogTimer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivityLifecycleInitializer : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    override fun create(context: Context) {
        LogTimer.LogE(this, "create")
        GlobalScope.launch {
            (context.applicationContext as Application).registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    LogTimer.LogE(this@RegisterActivityLifecycleInitializer, "onActivityCreated")
                    ActivityManager.instance.pushActivity(p0)
//                    if (BuildConfig.DEBUG) {
//                        SMFrameCallback.instance?.start()
//                        ViewServer.get(p0).addWindow(p0);
//                    }
                }

                override fun onActivityStarted(p0: Activity) {
                }

                override fun onActivityResumed(p0: Activity) {
//                    if (BuildConfig.DEBUG)
//                        ViewServer.get(p0).setFocusedWindow(p0);
                }

                override fun onActivityPaused(p0: Activity) {
                }

                override fun onActivityStopped(p0: Activity) {
                }

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                }

                override fun onActivityDestroyed(p0: Activity) {
                    ActivityManager.instance.popActivity(p0)
//                    if (BuildConfig.DEBUG)
//                        ViewServer.get(p0).removeWindow(p0);
                }
            })
        }
    }
}