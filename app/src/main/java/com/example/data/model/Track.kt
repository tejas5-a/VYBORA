package com.example.data.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationMs: Long = 180000L,
    val artworkUrl: String = "",
    val audioUrl: String = "", // Playable audio stream URL
    val previewUrl: String? = null, // Spotify 30s preview URL or alternate
    val isLiked: Boolean = false,
    val isSpotifyTrack: Boolean = false,
    val spotifyUri: String? = null,
    val releaseYear: String = "2024",
    val genre: String = "Electronic"
) {
    val durationFormatted: String
        get() {
            val totalSeconds = (durationMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val monthlyListeners: String = "",
    val genres: List<String> = emptyList(),
    val topTracks: List<Track> = emptyList(),
    val bio: String = ""
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String = "",
    val releaseDate: String = "",
    val totalTracks: Int = 0,
    val tracks: List<Track> = emptyList()
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String = "",
    val coverUrl: String = "",
    val trackCount: Int = 0,
    val isLocal: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val tracks: List<Track> = emptyList()
)

data class MusicCategory(
    val id: String,
    val name: String,
    val startColorHex: Long,
    val endColorHex: Long,
    val iconName: String = "music_note"
)
