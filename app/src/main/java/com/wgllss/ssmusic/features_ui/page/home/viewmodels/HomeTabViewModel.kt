package com.wgllss.ssmusic.features_ui.page.home.viewmodels

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.wgllss.ssmusic.core.units.WLog
import com.wgllss.ssmusic.core.viewmodel.BaseViewModel
import com.wgllss.ssmusic.data.MusicItemBean
import com.wgllss.ssmusic.datasource.repository.MusicRepository
import com.wgllss.ssmusic.features_system.globle.Constants
import com.wgllss.ssmusic.features_system.music.impl.exoplayer.MusicServiceConnection
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeTabViewModel @Inject constructor(private val musicServiceConnectionL: Lazy<MusicServiceConnection>) : BaseViewModel() {
    @Inject
    lateinit var musicRepositoryL: Lazy<MusicRepository>

    private val transportControls by lazy { musicServiceConnectionL.get().transportControls }

    val result by lazy { MutableLiveData<MutableList<MusicItemBean>>() }

    override fun start() {
    }

    fun getData(html: String) {
        flowAsyncWorkOnViewModelScopeLaunch {
            musicRepositoryL.get().homeMusic(html)
                .onEach {
                    result.postValue(it)
                }
        }
    }

    fun getDetailFromSearch(position: Int) {
        result?.value?.takeIf {
            it.size > position
        }?.run {
            flowAsyncWorkOnViewModelScopeLaunch {
                val detailUrl = get(position).detailUrl
                musicRepositoryL.get().getPlayUrl(detailUrl)
                    .onEach {
                        val extras = Bundle().apply {
                            putString(Constants.MEDIA_ID_KEY, it.id.toString())
                            putString(Constants.MEDIA_TITLE_KEY, it.title)
                            putString(Constants.MEDIA_AUTHOR_KEY, it.author)
                            putString(Constants.MEDIA_ARTNETWORK_URL_KEY, it.pic)
                            putString(Constants.MEDIA_URL_KEY, it.url)
                        }
                        transportControls.prepareFromUri(it.url.toUri(), extras)
                        musicRepositoryL.get().addToPlayList(it).collect()
                    }
            }
        }
    }

}