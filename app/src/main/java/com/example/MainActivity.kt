package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.components.AddToPlaylistSheet
import com.example.ui.components.ClientIdDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.VyboraBottomNav
import com.example.ui.home.HomeScreen
import com.example.ui.library.LibraryScreen
import com.example.ui.player.NowPlayingSheet
import com.example.ui.profile.ProfileScreen
import com.example.ui.search.SearchScreen
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraBackground
import com.example.ui.theme.VyboraTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationItem

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle OAuth callback intent on startup
        intent?.data?.let { handleIntentUri(it) }

        setContent {
            VyboraTheme {
                VyboraApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.data?.let { handleIntentUri(it) }
    }

    private fun handleIntentUri(uri: Uri) {
        if (uri.scheme == "vybora" && uri.host == "callback") {
            viewModel.handleSpotifyAuthUri(uri)
        }
    }
}

@Composable
fun VyboraApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Home state
    val selectedVibe by viewModel.selectedVibe.collectAsState()

    // Search state
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSearchFilter by viewModel.selectedSearchFilter.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    // Library state
    val libraryTab by viewModel.libraryTab.collectAsState()
    val localPlaylists by viewModel.localPlaylists.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsState()
    val selectedPlaylistTracks by viewModel.selectedPlaylistTracks.collectAsState()

    // Profile state
    val isSpotifyConnected by viewModel.isSpotifyConnected.collectAsState()
    val audioQuality by viewModel.audioQuality.collectAsState()
    val blurEffectsEnabled by viewModel.blurEffectsEnabled.collectAsState()

    // Dialogs state
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsState()
    val trackToAddToPlaylist by viewModel.trackToAddToPlaylist.collectAsState()
    val showClientIdDialog by viewModel.showClientIdDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    val isCurrentTrackLiked = playbackState.currentTrack?.let { ct ->
        likedTracks.any { it.id == ct.id }
    } ?: false

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VyboraBackground),
        containerColor = VyboraBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 90.dp)
            ) { data ->
                Snackbar(
                    containerColor = Color(0xFF1E212B),
                    contentColor = SoftWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = data.visuals.message, color = SoftWhite)
                }
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Main Screen Content with Crossfade
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition",
                modifier = Modifier.fillMaxSize()
            ) { screen ->
                when (screen) {
                    NavigationItem.HOME -> {
                        HomeScreen(
                            trendingTracks = viewModel.trendingTracks,
                            featuredPlaylists = viewModel.featuredPlaylists,
                            popularArtists = viewModel.popularArtists,
                            categories = viewModel.categories,
                            selectedVibe = selectedVibe,
                            onSelectVibe = viewModel::selectVibe,
                            onPlayTrack = viewModel::playTrack,
                            onOpenPlaylist = viewModel::openPlaylistDetail,
                            onOpenAddToPlaylist = viewModel::openAddToPlaylist,
                            onToggleLikeTrack = viewModel::toggleLikeTrack,
                            onNavigateToProfile = { viewModel.navigateTo(NavigationItem.PROFILE) }
                        )
                    }
                    NavigationItem.SEARCH -> {
                        SearchScreen(
                            query = searchQuery,
                            onQueryChange = viewModel::updateSearchQuery,
                            onClearQuery = viewModel::clearSearchQuery,
                            selectedFilter = selectedSearchFilter,
                            onSelectFilter = viewModel::selectSearchFilter,
                            searchResults = searchResults,
                            isSearching = isSearching,
                            recentSearches = recentSearches,
                            onSelectRecentSearch = viewModel::updateSearchQuery,
                            onRemoveRecentSearch = viewModel::removeRecentSearch,
                            onClearAllRecentSearches = viewModel::clearAllRecentSearches,
                            categories = viewModel.categories,
                            onPlayTrack = viewModel::playTrack,
                            onOpenPlaylist = viewModel::openPlaylistDetail,
                            onOpenAddToPlaylist = viewModel::openAddToPlaylist,
                            onToggleLikeTrack = viewModel::toggleLikeTrack
                        )
                    }
                    NavigationItem.LIBRARY -> {
                        LibraryScreen(
                            currentTab = libraryTab,
                            onTabSelected = viewModel::setLibraryTab,
                            localPlaylists = localPlaylists,
                            likedTracks = likedTracks,
                            selectedPlaylist = selectedPlaylist,
                            selectedPlaylistTracks = selectedPlaylistTracks,
                            onOpenPlaylistDetail = viewModel::openPlaylistDetail,
                            onClosePlaylistDetail = viewModel::closePlaylistDetail,
                            onCreatePlaylistClick = { viewModel.showCreatePlaylistDialog(true) },
                            onDeletePlaylist = viewModel::deletePlaylist,
                            onRemoveTrackFromPlaylist = viewModel::removeTrackFromPlaylist,
                            onPlayTrack = viewModel::playTrack,
                            onToggleLikeTrack = viewModel::toggleLikeTrack
                        )
                    }
                    NavigationItem.PROFILE -> {
                        ProfileScreen(
                            isSpotifyConnected = isSpotifyConnected,
                            onConnectSpotify = viewModel::startSpotifyAuth,
                            onDisconnectSpotify = viewModel::disconnectSpotify,
                            onConfigureClientId = { viewModel.showClientIdDialog(true) },
                            audioQuality = audioQuality,
                            onSelectAudioQuality = viewModel::setAudioQuality,
                            blurEffectsEnabled = blurEffectsEnabled,
                            onToggleBlurEffects = viewModel::toggleBlurEffects,
                            localPlaylistsCount = localPlaylists.size,
                            likedSongsCount = likedTracks.size
                        )
                    }
                }
            }

            // Bottom Navigation & Mini Player Floating Stack
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // Mini Player (Visible above Bottom Navigation when track is active)
                MiniPlayerBar(
                    playbackState = playbackState,
                    onExpand = viewModel::openNowPlaying,
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onNext = viewModel::nextTrack
                )

                // Translucent Bottom Navigation Bar
                VyboraBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = viewModel::navigateTo
                )
            }

            // Full-Screen Now Playing Modal Sheet
            NowPlayingSheet(
                isOpen = isNowPlayingExpanded,
                playbackState = playbackState,
                isLiked = isCurrentTrackLiked,
                onClose = viewModel::closeNowPlaying,
                onTogglePlayPause = viewModel::togglePlayPause,
                onNext = viewModel::nextTrack,
                onPrevious = viewModel::previousTrack,
                onSeekTo = viewModel::seekTo,
                onToggleShuffle = viewModel::toggleShuffle,
                onToggleRepeat = viewModel::toggleRepeat,
                onToggleLike = viewModel::toggleLikeTrack,
                onPlayQueueTrack = { qTrack -> viewModel.playTrack(qTrack, playbackState.queue) }
            )

            // Dialogs
            CreatePlaylistDialog(
                isOpen = showCreatePlaylistDialog,
                onDismiss = { viewModel.showCreatePlaylistDialog(false) },
                onCreate = viewModel::createPlaylist
            )

            AddToPlaylistSheet(
                track = trackToAddToPlaylist,
                playlists = localPlaylists,
                onDismiss = { viewModel.openAddToPlaylist(null) },
                onSelectPlaylist = viewModel::addTrackToPlaylist,
                onCreateNewPlaylist = {
                    viewModel.openAddToPlaylist(null)
                    viewModel.showCreatePlaylistDialog(true)
                }
            )

            ClientIdDialog(
                isOpen = showClientIdDialog,
                currentClientId = viewModel.authManager.getClientId(),
                onDismiss = { viewModel.showClientIdDialog(false) },
                onSave = viewModel::saveCustomClientId
            )
        }
    }
}
