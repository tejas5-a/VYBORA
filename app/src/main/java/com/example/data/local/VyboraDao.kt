package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VyboraDao {

    // Liked Tracks
    @Query("SELECT * FROM liked_tracks ORDER BY likedAt DESC")
    fun getAllLikedTracks(): Flow<List<LikedTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedTrack(track: LikedTrackEntity)

    @Query("DELETE FROM liked_tracks WHERE id = :trackId")
    suspend fun deleteLikedTrack(trackId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM liked_tracks WHERE id = :trackId)")
    suspend fun isTrackLiked(trackId: String): Boolean

    // Playlists
    @Query("SELECT * FROM local_playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<LocalPlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: LocalPlaylistEntity)

    @Query("DELETE FROM local_playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deletePlaylistTracks(playlistId: String)

    // Tracks in Playlist
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt ASC")
    fun getTracksForPlaylist(playlistId: String): Flow<List<PlaylistTrackCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackCountForPlaylist(playlistId: String): Int

    // Recent Searches
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun deleteRecentSearch(query: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()
}
