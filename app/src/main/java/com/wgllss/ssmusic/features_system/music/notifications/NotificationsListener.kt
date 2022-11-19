package com.wgllss.ssmusic.features_system.music.notifications

import android.app.Notification
import android.support.v4.media.session.MediaSessionCompat

interface NotificationsListener {

    fun updateNotification(mediaSession: MediaSessionCompat)

    fun buildNotification(mediaSession: MediaSessionCompat): Notification
}