package com.example.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import com.example.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlaybackController(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val tag = "VyboraPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var originalQueue: List<Track> = emptyList()

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { mp ->
                Log.d(tag, "MediaPlayer prepared, duration=${mp.duration}")
                val duration = mp.duration.toLong()
                _playbackState.update {
                    it.copy(
                        status = PlaybackStatus.PLAYING,
                        durationMs = if (duration > 0) duration else it.currentTrack?.durationMs ?: 180000L,
                        errorMessage = null
                    )
                }
                mp.start()
                startProgressTracker()
            }
            setOnCompletionListener {
                Log.d(tag, "MediaPlayer playback completed")
                handleTrackCompletion()
            }
            setOnErrorListener { _, what, extra ->
                Log.e(tag, "MediaPlayer error: what=$what extra=$extra")
                _playbackState.update {
                    it.copy(
                        status = PlaybackStatus.ERROR,
                        errorMessage = "Audio playback error ($what, $extra)"
                    )
                }
                stopProgressTracker()
                true
            }
            setOnBufferingUpdateListener { _, percent ->
                // Buffering feedback if needed
            }
        }
    }

    fun playTrack(track: Track, newQueue: List<Track> = listOf(track)) {
        originalQueue = if (newQueue.isNotEmpty()) newQueue else listOf(track)
        val activeQueue = if (_playbackState.value.isShuffle) originalQueue.shuffled() else originalQueue
        val index = activeQueue.indexOfFirst { it.id == track.id }.let { if (it >= 0) it else 0 }

        _playbackState.update {
            it.copy(
                currentTrack = track,
                queue = activeQueue,
                queueIndex = index,
                status = PlaybackStatus.BUFFERING,
                currentPositionMs = 0L,
                durationMs = track.durationMs,
                errorMessage = null,
                audioSourceLabel = if (track.isSpotifyTrack) "Spotify Web Catalogue" else "VYBORA Master Audio"
            )
        }

        prepareAndPlay(track)
    }

    private fun prepareAndPlay(track: Track) {
        stopProgressTracker()
        val url = track.audioUrl.ifBlank { track.previewUrl }

        if (url.isNullOrBlank()) {
            _playbackState.update {
                it.copy(
                    status = PlaybackStatus.PAUSED,
                    errorMessage = "Preview unavailable for this track."
                )
            }
            return
        }

        try {
            if (mediaPlayer == null) {
                initMediaPlayer()
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
            _playbackState.update { it.copy(status = PlaybackStatus.BUFFERING) }
        } catch (e: Exception) {
            Log.e(tag, "Failed to setDataSource or prepareAsync", e)
            _playbackState.update {
                it.copy(
                    status = PlaybackStatus.ERROR,
                    errorMessage = "Unable to load stream: ${e.localizedMessage}"
                )
            }
        }
    }

    fun togglePlayPause() {
        val current = _playbackState.value
        val mp = mediaPlayer ?: return

        when (current.status) {
            PlaybackStatus.PLAYING -> {
                try {
                    mp.pause()
                    _playbackState.update { it.copy(status = PlaybackStatus.PAUSED) }
                    stopProgressTracker()
                } catch (e: Exception) {
                    Log.e(tag, "Error pausing player", e)
                }
            }
            PlaybackStatus.PAUSED -> {
                try {
                    mp.start()
                    _playbackState.update { it.copy(status = PlaybackStatus.PLAYING) }
                    startProgressTracker()
                } catch (e: Exception) {
                    Log.e(tag, "Error resuming player", e)
                    current.currentTrack?.let { prepareAndPlay(it) }
                }
            }
            PlaybackStatus.IDLE, PlaybackStatus.ERROR -> {
                current.currentTrack?.let { prepareAndPlay(it) }
            }
            PlaybackStatus.BUFFERING -> {
                // Wait for buffering
            }
        }
    }

    fun next() {
        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val nextIndex = state.queueIndex + 1
        if (nextIndex < queue.size) {
            val nextTrack = queue[nextIndex]
            _playbackState.update { it.copy(queueIndex = nextIndex, currentTrack = nextTrack) }
            prepareAndPlay(nextTrack)
        } else if (state.repeatMode == RepeatMode.ALL) {
            val firstTrack = queue.first()
            _playbackState.update { it.copy(queueIndex = 0, currentTrack = firstTrack) }
            prepareAndPlay(firstTrack)
        }
    }

    fun previous() {
        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (state.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }

        val prevIndex = state.queueIndex - 1
        if (prevIndex >= 0) {
            val prevTrack = queue[prevIndex]
            _playbackState.update { it.copy(queueIndex = prevIndex, currentTrack = prevTrack) }
            prepareAndPlay(prevTrack)
        } else if (state.repeatMode == RepeatMode.ALL) {
            val lastTrack = queue.last()
            _playbackState.update { it.copy(queueIndex = queue.size - 1, currentTrack = lastTrack) }
            prepareAndPlay(lastTrack)
        } else {
            seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        val mp = mediaPlayer ?: return
        try {
            val target = positionMs.coerceIn(0L, _playbackState.value.durationMs).toInt()
            mp.seekTo(target)
            _playbackState.update { it.copy(currentPositionMs = target.toLong()) }
        } catch (e: Exception) {
            Log.e(tag, "Error seeking", e)
        }
    }

    fun toggleShuffle() {
        _playbackState.update { state ->
            val newShuffle = !state.isShuffle
            val newQueue = if (newShuffle) {
                val current = state.currentTrack
                val shuffled = originalQueue.filter { it.id != current?.id }.shuffled()
                if (current != null) listOf(current) + shuffled else shuffled
            } else {
                originalQueue
            }
            val newIndex = newQueue.indexOfFirst { it.id == state.currentTrack?.id }.coerceAtLeast(0)
            state.copy(isShuffle = newShuffle, queue = newQueue, queueIndex = newIndex)
        }
    }

    fun toggleRepeat() {
        _playbackState.update { state ->
            val nextRepeat = when (state.repeatMode) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
            state.copy(repeatMode = nextRepeat)
        }
    }

    private fun handleTrackCompletion() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0L)
                mediaPlayer?.start()
                _playbackState.update { it.copy(status = PlaybackStatus.PLAYING) }
                startProgressTracker()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                if (_playbackState.value.hasNext) {
                    next()
                } else {
                    _playbackState.update {
                        it.copy(
                            status = PlaybackStatus.PAUSED,
                            currentPositionMs = it.durationMs
                        )
                    }
                    stopProgressTracker()
                }
            }
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val current = mp.currentPosition.toLong()
                        val duration = mp.duration.toLong().let { if (it > 0) it else _playbackState.value.durationMs }
                        _playbackState.update {
                            it.copy(
                                currentPositionMs = current,
                                durationMs = duration
                            )
                        }
                    }
                }
                delay(250L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        releasePlayer()
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
