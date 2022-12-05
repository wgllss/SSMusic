package com.wgllss.ssmusic.features_system.music.notifications

import android.app.Notification

interface NotificationListener {

    fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean)

    fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean)

    fun onNotificationActionNext()

    fun onNotificationPrev()
}