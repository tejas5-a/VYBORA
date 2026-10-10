package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "liked_tracks")
data class LikedTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkUrl: String,
    val audioUrl: String,
    val isSpotifyTrack: Boolean,
    val likedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_playlists")
data class LocalPlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index(value = ["playlistId"]), Index(value = ["trackId"])]
)
data class PlaylistTrackCrossRef(
    val playlistId: String,
    val trackId: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkUrl: String,
    val audioUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
