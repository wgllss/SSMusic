package com.wgllss.ssmusic.core.widget.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

@Navigator.Name("fixfragment")
class FixFragmentNavigator constructor(private val mContext: Context, private val mManager: FragmentManager, private val mContainerId: Int) : FragmentNavigator(mContext, mManager, mContainerId) {
    val TAG = "FixFragmentNavigator"


    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?): NavDestination? {
        if (mManager!!.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        var className: String = destination.className
        if (className[0] == '.') {
            className = mContext!!.packageName + className
        }
        //注释掉这句话
        // 不需要每次去navigate的时候都去实例化fragment
        //final Fragment frag = instantiateFragment(mContext, mManager,
        //       className, args);
        //frag.setArguments(args);
        val ft = mManager!!.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        //获取到当前显示的fragment
        val fragment = mManager!!.primaryNavigationFragment
        //如果不为空  就隐藏
        if (fragment != null) {
            ft.hide(fragment)
        }
        //去获取目的地的Fragment 即将要显示的Fragment
        var frag: Fragment? = null
        val tag: String = destination.id.toString()
        //去通过tag从manager中获取fragment
        frag = mManager!!.findFragmentByTag(tag)
        //如果不为空就显示
        if (frag != null) {
            ft.show(frag)
        } else {
            //如果为空就创建一个fragment的对象
            frag = instantiateFragment(mContext, mManager, className, args)
            frag!!.setArguments(args)
            ft.add(mContainerId, frag, tag)
        }
        //不再需要replace
        //ft.replace(mContainerId, frag);
        //帮要显示的fragment设置成当前的fragment
        ft.setPrimaryNavigationFragment(frag)
        @IdRes val destId: Int = destination.id
        //通过反射获取mBackStack 然后重新设置参数
        var mBackStack: ArrayDeque<Int>? = null
        try {
            val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            mBackStack = field[this] as ArrayDeque<Int>
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val initialNavigation = mBackStack!!.isEmpty()
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)
        val isAdded: Boolean
        isAdded = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        if (navigatorExtras is Extras) {
            val extras: Extras? = navigatorExtras as Extras?
            extras?.sharedElements?.forEach {
                ft.addSharedElement(it.key, it.value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    open fun generateBackStackName(backStackindex: Int, destid: Int): String? {
        return "$backStackindex-$destid"
    }
}