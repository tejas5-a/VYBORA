package com.example.data.repository

import android.util.Log
import com.example.data.local.LikedTrackEntity
import com.example.data.local.LocalPlaylistEntity
import com.example.data.local.PlaylistTrackCrossRef
import com.example.data.local.RecentSearchEntity
import com.example.data.local.VyboraDao
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.MusicCategory
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.data.remote.SpotifyApiClient
import com.example.data.remote.SpotifyAuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class SearchResult(
    val tracks: List<Track> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val isFromSpotify: Boolean = false,
    val query: String = ""
)

class VyboraRepository(
    private val dao: VyboraDao,
    private val spotifyApiClient: SpotifyApiClient,
    private val authManager: SpotifyAuthManager
) {
    private val tag = "VyboraRepo"

    fun getTrendingTracks(): List<Track> = CuratedCatalogue.trendingTracks

    fun getFeaturedPlaylists(): List<Playlist> = CuratedCatalogue.featuredPlaylists

    fun getPopularArtists(): List<Artist> = CuratedCatalogue.popularArtists

    fun getCategories(): List<MusicCategory> = CuratedCatalogue.categories

    // Search: Queries Spotify API if token available, plus matches Curated catalog
    suspend fun search(query: String): SearchResult {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return SearchResult()

        // 1. Try Spotify Web API if user is connected
        if (authManager.hasValidToken()) {
            try {
                val response = spotifyApiClient.apiService.search(query = trimmed, limit = 15)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    val spotifyTracks = body.tracks?.items?.map { item ->
                        Track(
                            id = item.id,
                            title = item.name,
                            artist = item.artists.joinToString(", ") { it.name },
                            album = item.album?.name ?: "",
                            durationMs = item.durationMs,
                            artworkUrl = item.album?.images?.firstOrNull()?.url ?: "",
                            audioUrl = item.previewUrl ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                            previewUrl = item.previewUrl,
                            isSpotifyTrack = true,
                            spotifyUri = item.uri
                        )
                    } ?: emptyList()

                    val spotifyArtists = body.artists?.items?.map { item ->
                        Artist(
                            id = item.id,
                            name = item.name,
                            imageUrl = item.images.firstOrNull()?.url ?: "",
                            monthlyListeners = "${item.followers?.total ?: 0} followers",
                            genres = item.genres
                        )
                    } ?: emptyList()

                    val spotifyAlbums = body.albums?.items?.map { item ->
                        Album(
                            id = item.id,
                            title = item.name,
                            artist = item.artists.joinToString(", ") { it.name },
                            coverUrl = item.images.firstOrNull()?.url ?: "",
                            releaseDate = item.releaseDate ?: "",
                            totalTracks = item.totalTracks
                        )
                    } ?: emptyList()

                    val spotifyPlaylists = body.playlists?.items?.map { item ->
                        Playlist(
                            id = item.id,
                            title = item.name,
                            description = item.description ?: "",
                            coverUrl = item.images.firstOrNull()?.url ?: "",
                            trackCount = item.tracks?.total ?: 0,
                            isLocal = false
                        )
                    } ?: emptyList()

                    Log.d(tag, "Spotify search returned ${spotifyTracks.size} tracks")
                    return SearchResult(
                        tracks = spotifyTracks,
                        artists = spotifyArtists,
                        albums = spotifyAlbums,
                        playlists = spotifyPlaylists,
                        isFromSpotify = true,
                        query = trimmed
                    )
                }
            } catch (e: Exception) {
                Log.w(tag, "Spotify search failed, falling back to curated: ${e.message}")
            }
        }

        // 2. Curated Catalogue search
        val filteredTracks = CuratedCatalogue.trendingTracks.filter {
            it.title.contains(trimmed, ignoreCase = true) ||
            it.artist.contains(trimmed, ignoreCase = true) ||
            it.album.contains(trimmed, ignoreCase = true) ||
            it.genre.contains(trimmed, ignoreCase = true)
        }

        val filteredArtists = CuratedCatalogue.popularArtists.filter {
            it.name.contains(trimmed, ignoreCase = true) ||
            it.genres.any { g -> g.contains(trimmed, ignoreCase = true) }
        }

        val filteredPlaylists = CuratedCatalogue.featuredPlaylists.filter {
            it.title.contains(trimmed, ignoreCase = true) ||
            it.description.contains(trimmed, ignoreCase = true)
        }

        return SearchResult(
            tracks = filteredTracks,
            artists = filteredArtists,
            albums = emptyList(),
            playlists = filteredPlaylists,
            isFromSpotify = false,
            query = trimmed
        )
    }

    // Liked Songs
    fun getLikedTracksFlow(): Flow<List<Track>> {
        return dao.getAllLikedTracks().map { list ->
            list.map { entity ->
                Track(
                    id = entity.id,
                    title = entity.title,
                    artist = entity.artist,
                    album = entity.album,
                    durationMs = entity.durationMs,
                    artworkUrl = entity.artworkUrl,
                    audioUrl = entity.audioUrl,
                    isLiked = true,
                    isSpotifyTrack = entity.isSpotifyTrack
                )
            }
        }
    }

    suspend fun isTrackLiked(trackId: String): Boolean = dao.isTrackLiked(trackId)

    suspend fun toggleLiked(track: Track): Boolean {
        val currentlyLiked = dao.isTrackLiked(track.id)
        if (currentlyLiked) {
            dao.deleteLikedTrack(track.id)
            return false
        } else {
            dao.insertLikedTrack(
                LikedTrackEntity(
                    id = track.id,
                    title = track.title,
                    artist = track.artist,
                    album = track.album,
                    durationMs = track.durationMs,
                    artworkUrl = track.artworkUrl,
                    audioUrl = track.audioUrl,
                    isSpotifyTrack = track.isSpotifyTrack
                )
            )
            return true
        }
    }

    // Local Playlists
    fun getLocalPlaylistsFlow(): Flow<List<Playlist>> {
        return dao.getAllPlaylists().map { list ->
            list.map { entity ->
                val count = dao.getTrackCountForPlaylist(entity.id)
                Playlist(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    coverUrl = entity.coverUrl,
                    trackCount = count,
                    isLocal = true,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    suspend fun createPlaylist(title: String, description: String = ""): Playlist {
        val id = UUID.randomUUID().toString()
        val defaultCover = CuratedCatalogue.trendingTracks.random().artworkUrl
        val entity = LocalPlaylistEntity(
            id = id,
            title = title.ifBlank { "New Playlist" },
            description = description,
            coverUrl = defaultCover
        )
        dao.insertPlaylist(entity)
        return Playlist(id = id, title = entity.title, description = description, coverUrl = defaultCover, isLocal = true)
    }

    suspend fun deletePlaylist(playlistId: String) {
        dao.deletePlaylistTracks(playlistId)
        dao.deletePlaylist(playlistId)
    }

    suspend fun addTrackToPlaylist(playlistId: String, track: Track) {
        dao.addTrackToPlaylist(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                album = track.album,
                durationMs = track.durationMs,
                artworkUrl = track.artworkUrl,
                audioUrl = track.audioUrl
            )
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        dao.removeTrackFromPlaylist(playlistId, trackId)
    }

    fun getPlaylistTracks(playlistId: String): Flow<List<Track>> {
        return dao.getTracksForPlaylist(playlistId).map { list ->
            list.map { entity ->
                Track(
                    id = entity.trackId,
                    title = entity.title,
                    artist = entity.artist,
                    album = entity.album,
                    durationMs = entity.durationMs,
                    artworkUrl = entity.artworkUrl,
                    audioUrl = entity.audioUrl
                )
            }
        }
    }

    // Recent Searches
    fun getRecentSearchesFlow(): Flow<List<String>> =
        dao.getRecentSearches().map { list -> list.map { it.query } }

    suspend fun addRecentSearch(query: String) {
        if (query.isNotBlank()) {
            dao.insertRecentSearch(RecentSearchEntity(query = query.trim()))
        }
    }

    suspend fun deleteRecentSearch(query: String) {
        dao.deleteRecentSearch(query.trim())
    }

    suspend fun clearRecentSearches() {
        dao.clearRecentSearches()
    }
}
