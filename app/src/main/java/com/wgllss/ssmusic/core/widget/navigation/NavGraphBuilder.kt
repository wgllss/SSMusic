package com.wgllss.ssmusic.core.widget.navigation

import android.content.ComponentName
import androidx.fragment.app.FragmentActivity
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import com.wgllss.ssmusic.core.units.AppConfig
import com.wgllss.ssmusic.core.units.AppGlobals

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
        val destConfig: HashMap<String, Destination> = AppConfig.getDestConfig()
        val iterator: Iterator<Destination> = destConfig.values.iterator()
        while (iterator.hasNext()) {
            val node: Destination = iterator.next()
            if (node.isFragment) {
                val destination: FragmentNavigator.Destination = fragmentNavigator.createDestination()
                destination.id = node.id
                destination.setClassName(node.className)
                destination.addDeepLink(node.pageUrl)
                navGraph.addDestination(destination)
            } else {
                val destination: ActivityNavigator.Destination = activityNavigator.createDestination()
                destination.id = node.id
                destination.setComponentName(ComponentName(AppGlobals.getApplication().getPackageName(), node.className))
                destination.addDeepLink(node.pageUrl)
                navGraph.addDestination(destination)
            }

            //给APP页面导航结果图 设置一个默认的展示页的id
            if (node.asStarter) {
                navGraph.setStartDestination(node.id)
            }
        }
        controller.graph = navGraph
    }
}