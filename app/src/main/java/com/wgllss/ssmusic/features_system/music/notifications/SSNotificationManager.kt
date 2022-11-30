package com.wgllss.ssmusic.features_system.music.notifications

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SSNotificationManager @Inject constructor(@ApplicationContext context: Context, sessionToken: MediaSessionCompat.Token, notificationListener: PlayerNotificationManager.NotificationListener) {


}