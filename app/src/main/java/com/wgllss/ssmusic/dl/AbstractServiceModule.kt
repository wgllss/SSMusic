package com.wgllss.ssmusic.dl

import com.wgllss.ssmusic.dl.annotations.BindMediaPlayer
import com.wgllss.ssmusic.dl.annotations.BindWlMusic
import com.wgllss.ssmusic.features_system.music.IMusicPlay
import com.wgllss.ssmusic.features_system.music.impl.mediaplayer.MediaPlayerImpl
import com.wgllss.ssmusic.features_system.music.impl.wlmusicplayer.WlMusicImpl
import com.wgllss.ssmusic.features_system.music.notifications.NotificationsListener
import com.wgllss.ssmusic.features_system.music.notifications.RealNotifications
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@InstallIn(ServiceComponent::class)
@Module
abstract class AbstractServiceModule {

    @BindWlMusic
    @Binds
    @ServiceScoped
    abstract fun bindWlmusic(mumicImpl: WlMusicImpl): IMusicPlay

    @BindMediaPlayer
    @Binds
    @ServiceScoped
    abstract fun bindMediaPlayer(mumicImpl: MediaPlayerImpl): IMusicPlay

    @Binds
    @ServiceScoped
    abstract fun bindNotificationsListener(realNotifications: RealNotifications): NotificationsListener
}