package com.slyro.innerbeat.playback.queues

import androidx.media3.common.MediaItem
import com.slyro.innertube.YouTube
import com.slyro.innertube.models.WatchEndpoint
import com.slyro.innerbeat.extensions.toMediaItem
import com.slyro.innerbeat.models.MediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class YouTubeAlbumRadio(
    private val playlistId: String,
) : Queue {
    override val preloadItem: MediaMetadata? = null
    private val endpoint = WatchEndpoint(
        playlistId = playlistId,
        params = "wAEB"
    )
    private var continuation: String? = null

    override suspend fun getInitialStatus(): Queue.Status = withContext(IO) {
        val albumSongs = YouTube.albumSongs(playlistId).getOrThrow()
        val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
        continuation = nextResult.continuation
        Queue.Status(
            title = nextResult.title,
            items = (albumSongs + nextResult.items.subList(albumSongs.size, nextResult.items.size)).map { it.toMediaItem() },
            mediaItemIndex = nextResult.currentIndex ?: 0
        )
    }

    override fun hasNextPage(): Boolean = continuation != null

    override suspend fun nextPage(): List<MediaItem> {
        val nextResult = withContext(IO) {
            YouTube.next(endpoint, continuation).getOrThrow()
        }
        continuation = nextResult.continuation
        return nextResult.items.map { it.toMediaItem() }
    }
}
