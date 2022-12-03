package com.wgllss.ssmusic.features_system.music.notifications

import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat

interface NotificationsListener {

    fun updateNotification(mediaSession: MediaSessionCompat, largeIcon: Bitmap? = null)

    fun setNotificationListener(notificationsListener: NotificationListener)

}