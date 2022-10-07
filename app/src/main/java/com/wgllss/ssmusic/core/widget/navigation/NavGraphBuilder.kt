package com.wgllss.ssmusic.core.widget.navigation

import android.content.ComponentName
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.wgllss.annotations.Destination
import com.wgllss.ssmusic.NavigationConfig
import com.wgllss.ssmusic.core.units.AppGlobals
import com.wgllss.ssmusic.core.units.WLog
import java.util.HashMap

object NavGraphBuilder {

    fun build(activity: FragmentActivity, controller: NavController, containerId: Int) {
        val provider = controller.navigatorProvider
        //NavGraphNavigator也是页面路由导航器的一种，只不过他比较特殊。
        //它只为默认的展示页提供导航服务,但真正的跳转还是交给对应的navigator来完成的
        val navGraph = NavGraph(NavGraphNavigator(provider))
        //FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        //fragment的导航此处使用我们定制的FixFragmentNavigator，底部Tab切换时 使用hide()/show(),而不是replace()
        val fragmentNavigator = FixFragmentNavigator(activity, activity.supportFragmentManager, containerId)
        provider.addNavigator(fragmentNavigator)
        val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)
        NavigationConfig.getDestConfig()?.forEach {
            it?.value?.run {
                if (isFragment) {
                    val destination: FragmentNavigator.Destination = fragmentNavigator.createDestination()
                    destination.id = id
                    destination.className = className
                    destination.addDeepLink(pageUrl)
                    navGraph.addDestination(destination)
                } else {
                    val destination: ActivityNavigator.Destination = activityNavigator.createDestination()
                    destination.id = id
                    destination.setComponentName(ComponentName(AppGlobals.getApplication().getPackageName(), className))
                    destination.addDeepLink(pageUrl)
                    navGraph.addDestination(destination)
                }
                if (asStarter) {
                    navGraph.startDestination = id
                }
            }
        }
        controller.graph = navGraph
    }
}