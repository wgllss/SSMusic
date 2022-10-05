package com.wgllss.ssmusic.core.units

import android.app.Application

object AppGlobals {
    lateinit var sApplication: Application;

    //    companion object {
//        var sApplication: Application = getApplication()
//
    fun getApplication(): Application {
        if (!this::sApplication.isInitialized) {
            //去反射得到
            try {
                val aClass = Class.forName("android.app.ActivityThread")
                //获取里面的currentApplication
                val currentApplication = aClass.getDeclaredMethod("currentApplication")
                sApplication = currentApplication.invoke(null, null) as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sApplication
    }
//    }
}
