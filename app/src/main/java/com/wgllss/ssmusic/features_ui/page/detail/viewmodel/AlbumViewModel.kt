package com.wgllss.ssmusic.features_ui.page.detail.viewmodel

import kotlinx.coroutines.flow.onEach

class AlbumViewModel : SongSheetViewModel() {
    override fun kSongSheetDetail(encodeID: String) {

        flowAsyncWorkOnViewModelScopeLaunch {
            kRepository.getAlbumDetail(encodeID)
                .onEach {
                    songSheetDetail.postValue(it)
                }
        }
    }
}