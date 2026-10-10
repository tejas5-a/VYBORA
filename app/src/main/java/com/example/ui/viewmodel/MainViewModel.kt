package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VyboraDatabase
import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.MusicCategory
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.data.remote.SpotifyApiClient
import com.example.data.remote.SpotifyAuthManager
import com.example.data.repository.SearchResult
import com.example.data.repository.VyboraRepository
import com.example.playback.AudioPlaybackController
import com.example.playback.PlaybackState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NavigationItem {
    HOME,
    SEARCH,
    LIBRARY,
    PROFILE
}

enum class SearchFilter {
    ALL,
    TRACKS,
    ARTISTS,
    PLAYLISTS,
    ALBUMS
}

enum class LibraryTab {
    PLAYLISTS,
    LIKED_SONGS,
    ARTISTS,
    ALBUMS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VyboraDatabase.getInstance(application)
    val authManager = SpotifyAuthManager(application)
    private val spotifyApiClient = SpotifyApiClient(authManager)
    val repository = VyboraRepository(db.dao(), spotifyApiClient, authManager)
    val playbackController = AudioPlaybackController(application, viewModelScope)

    // Navigation
    private val _currentScreen = MutableStateFlow(NavigationItem.HOME)
    val currentScreen: StateFlow<NavigationItem> = _currentScreen.asStateFlow()

    // Now Playing Screen Modal
    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    // Home Screen Data
    val trendingTracks: List<Track> = repository.getTrendingTracks()
    val featuredPlaylists: List<Playlist> = repository.getFeaturedPlaylists()
    val popularArtists: List<Artist> = repository.getPopularArtists()
    val categories: List<MusicCategory> = repository.getCategories()

    private val _selectedVibe = MutableStateFlow("All")
    val selectedVibe: StateFlow<String> = _selectedVibe.asStateFlow()

    // Search Screen State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSearchFilter = MutableStateFlow(SearchFilter.ALL)
    val selectedSearchFilter: StateFlow<SearchFilter> = _selectedSearchFilter.asStateFlow()

    private val _searchResults = MutableStateFlow(SearchResult())
    val searchResults: StateFlow<SearchResult> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val recentSearches: StateFlow<List<String>> = repository.getRecentSearchesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Library Screen State
    private val _libraryTab = MutableStateFlow(LibraryTab.PLAYLISTS)
    val libraryTab: StateFlow<LibraryTab> = _libraryTab.asStateFlow()

    val localPlaylists: StateFlow<List<Playlist>> = repository.getLocalPlaylistsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedTracks: StateFlow<List<Track>> = repository.getLikedTracksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Playlist Detail View
    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    private val _selectedPlaylistTracks = MutableStateFlow<List<Track>>(emptyList())
    val selectedPlaylistTracks: StateFlow<List<Track>> = _selectedPlaylistTracks.asStateFlow()

    // Dialogs State
    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _trackToAddToPlaylist = MutableStateFlow<Track?>(null)
    val trackToAddToPlaylist: StateFlow<Track?> = _trackToAddToPlaylist.asStateFlow()

    private val _showClientIdDialog = MutableStateFlow(false)
    val showClientIdDialog: StateFlow<Boolean> = _showClientIdDialog.asStateFlow()

    // Message banner
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Spotify Auth status
    val isSpotifyConnected: StateFlow<Boolean> = authManager.isConnected

    // Settings
    private val _audioQuality = MutableStateFlow("24-bit • 96kHz Lossless")
    val audioQuality: StateFlow<String> = _audioQuality.asStateFlow()

    private val _blurEffectsEnabled = MutableStateFlow(true)
    val blurEffectsEnabled: StateFlow<Boolean> = _blurEffectsEnabled.asStateFlow()

    // Playback state from controller
    val playbackState: StateFlow<PlaybackState> = playbackController.playbackState

    init {
        setupSearchDebounce()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(350L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    _isSearching.value = true
                    try {
                        val results = repository.search(query)
                        _searchResults.value = results
                    } catch (e: Exception) {
                        _searchResults.value = SearchResult(query = query)
                    } finally {
                        _isSearching.value = false
                    }
                } else {
                    _searchResults.value = SearchResult()
                    _isSearching.value = false
                }
            }
            .launchIn(viewModelScope)
    }

    fun navigateTo(item: NavigationItem) {
        _currentScreen.value = item
    }

    fun openNowPlaying() {
        _isNowPlayingExpanded.value = true
    }

    fun closeNowPlaying() {
        _isNowPlayingExpanded.value = false
    }

    fun selectVibe(vibe: String) {
        _selectedVibe.value = vibe
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.addRecentSearch(query)
            }
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        _searchResults.value = SearchResult()
    }

    fun selectSearchFilter(filter: SearchFilter) {
        _selectedSearchFilter.value = filter
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            repository.deleteRecentSearch(query)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    fun setLibraryTab(tab: LibraryTab) {
        _libraryTab.value = tab
    }

    fun openPlaylistDetail(playlist: Playlist) {
        _selectedPlaylist.value = playlist
        viewModelScope.launch {
            repository.getPlaylistTracks(playlist.id).collect { tracks ->
                _selectedPlaylistTracks.value = tracks
            }
        }
    }

    fun closePlaylistDetail() {
        _selectedPlaylist.value = null
        _selectedPlaylistTracks.value = emptyList()
    }

    fun showCreatePlaylistDialog(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun createPlaylist(name: String, desc: String) {
        viewModelScope.launch {
            val pl = repository.createPlaylist(name, desc)
            _showCreatePlaylistDialog.value = false
            showToast("Created playlist '${pl.title}'")
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist.id)
            if (_selectedPlaylist.value?.id == playlist.id) {
                closePlaylistDetail()
            }
            showToast("Deleted playlist '${playlist.title}'")
        }
    }

    fun openAddToPlaylist(track: Track?) {
        _trackToAddToPlaylist.value = track
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlist.id, track)
            _trackToAddToPlaylist.value = null
            showToast("Added to '${playlist.title}'")
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, trackId)
            showToast("Removed from playlist")
        }
    }

    fun toggleLikeTrack(track: Track) {
        viewModelScope.launch {
            val liked = repository.toggleLiked(track)
            showToast(if (liked) "Saved to Liked Songs" else "Removed from Liked Songs")
        }
    }

    // Playback delegation
    fun playTrack(track: Track, queue: List<Track> = listOf(track)) {
        playbackController.playTrack(track, queue)
    }

    fun togglePlayPause() {
        playbackController.togglePlayPause()
    }

    fun nextTrack() {
        playbackController.next()
    }

    fun previousTrack() {
        playbackController.previous()
    }

    fun seekTo(positionMs: Long) {
        playbackController.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playbackController.toggleShuffle()
    }

    fun toggleRepeat() {
        playbackController.toggleRepeat()
    }

    // Spotify PKCE Auth
    fun startSpotifyAuth() {
        authManager.startAuthorizationFlow()
    }

    fun handleSpotifyAuthUri(uri: Uri) {
        viewModelScope.launch {
            val result = authManager.handleAuthCallback(uri)
            if (result.isSuccess) {
                showToast("Connected to Spotify!")
            } else {
                showToast("Spotify connection: ${result.exceptionOrNull()?.message ?: "Failed"}")
            }
        }
    }

    fun disconnectSpotify() {
        authManager.disconnect()
        showToast("Disconnected from Spotify")
    }

    fun showClientIdDialog(show: Boolean) {
        _showClientIdDialog.value = show
    }

    fun saveCustomClientId(clientId: String) {
        authManager.setClientId(clientId)
        _showClientIdDialog.value = false
        showToast("Spotify Client ID updated")
    }

    fun setAudioQuality(quality: String) {
        _audioQuality.value = quality
    }

    fun toggleBlurEffects() {
        _blurEffectsEnabled.update { !it }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playbackController.release()
    }
}
