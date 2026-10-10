package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiresIn: Long,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "scope") val scope: String? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyUserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "product") val product: String?,
    @Json(name = "images") val images: List<SpotifyImage>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class SpotifySearchResponse(
    @Json(name = "tracks") val tracks: SpotifyTrackPaging? = null,
    @Json(name = "artists") val artists: SpotifyArtistPaging? = null,
    @Json(name = "albums") val albums: SpotifyAlbumPaging? = null,
    @Json(name = "playlists") val playlists: SpotifyPlaylistPaging? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyTrackPaging(
    @Json(name = "items") val items: List<SpotifyTrackItem> = emptyList(),
    @Json(name = "total") val total: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyArtistPaging(
    @Json(name = "items") val items: List<SpotifyArtistItem> = emptyList(),
    @Json(name = "total") val total: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbumPaging(
    @Json(name = "items") val items: List<SpotifyAlbumItem> = emptyList(),
    @Json(name = "total") val total: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyPlaylistPaging(
    @Json(name = "items") val items: List<SpotifyPlaylistItem> = emptyList(),
    @Json(name = "total") val total: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyTrackItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "duration_ms") val durationMs: Long = 180000L,
    @Json(name = "preview_url") val previewUrl: String? = null,
    @Json(name = "uri") val uri: String? = null,
    @Json(name = "artists") val artists: List<SpotifyArtistSimple> = emptyList(),
    @Json(name = "album") val album: SpotifyAlbumSimple? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyArtistSimple(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbumSimple(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "images") val images: List<SpotifyImage> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SpotifyArtistItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "images") val images: List<SpotifyImage> = emptyList(),
    @Json(name = "genres") val genres: List<String> = emptyList(),
    @Json(name = "followers") val followers: SpotifyFollowers? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyFollowers(
    @Json(name = "total") val total: Long = 0L
)

@JsonClass(generateAdapter = true)
data class SpotifyAlbumItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "artists") val artists: List<SpotifyArtistSimple> = emptyList(),
    @Json(name = "images") val images: List<SpotifyImage> = emptyList(),
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "total_tracks") val totalTracks: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyPlaylistItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "images") val images: List<SpotifyImage> = emptyList(),
    @Json(name = "tracks") val tracks: SpotifyPlaylistTracksInfo? = null
)

@JsonClass(generateAdapter = true)
data class SpotifyPlaylistTracksInfo(
    @Json(name = "total") val total: Int = 0
)

@JsonClass(generateAdapter = true)
data class SpotifyImage(
    @Json(name = "url") val url: String,
    @Json(name = "height") val height: Int? = null,
    @Json(name = "width") val width: Int? = null
)
