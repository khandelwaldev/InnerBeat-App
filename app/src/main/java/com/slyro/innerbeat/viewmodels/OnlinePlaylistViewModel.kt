package com.slyro.innerbeat.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slyro.innertube.YouTube
import com.slyro.innertube.models.PlaylistItem
import com.slyro.innertube.models.SongItem
import com.slyro.innertube.utils.completed
import com.slyro.innerbeat.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlinePlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val playlistId = savedStateHandle.get<String>("playlistId")!!

    val playlist = MutableStateFlow<PlaylistItem?>(null)
    val playlistSongs = MutableStateFlow<List<SongItem>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.playlist(playlistId).completed()
                .onSuccess { playlistPage ->
                    playlist.value = playlistPage.playlist
                    playlistSongs.value = playlistPage.songs
                }.onFailure {
                    reportException(it)
                }
        }
    }
}
