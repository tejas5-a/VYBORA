package com.example.playback

import com.example.data.model.Track

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlaybackState(
    val currentTrack: Track? = null,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = -1,
    val errorMessage: String? = null,
    val audioQualityBadge: String = "24-bit • 96kHz Lossless",
    val audioSourceLabel: String = "VYBORA Master Audio"
) {
    val progress: Float
        get() = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val isPlaying: Boolean
        get() = status == PlaybackStatus.PLAYING

    val isBuffering: Boolean
        get() = status == PlaybackStatus.BUFFERING

    val hasNext: Boolean
        get() = queue.isNotEmpty() && (queueIndex < queue.size - 1 || repeatMode == RepeatMode.ALL)

    val hasPrevious: Boolean
        get() = queue.isNotEmpty() && (queueIndex > 0 || currentPositionMs > 3000L)

    val currentPositionFormatted: String
        get() {
            val totalSeconds = (currentPositionMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val durationFormatted: String
        get() {
            val totalSeconds = (durationMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
