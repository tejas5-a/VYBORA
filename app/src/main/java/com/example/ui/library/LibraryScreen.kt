package com.example.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.DarkGrey
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated
import com.example.ui.theme.VyboraSurfaceGlass
import com.example.ui.viewmodel.LibraryTab

@Composable
fun LibraryScreen(
    currentTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    localPlaylists: List<Playlist>,
    likedTracks: List<Track>,
    selectedPlaylist: Playlist?,
    selectedPlaylistTracks: List<Track>,
    onOpenPlaylistDetail: (Playlist) -> Unit,
    onClosePlaylistDetail: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onRemoveTrackFromPlaylist: (String, String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onToggleLikeTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedPlaylist != null) {
        PlaylistDetailView(
            playlist = selectedPlaylist,
            tracks = selectedPlaylistTracks,
            onBack = onClosePlaylistDetail,
            onPlayTrack = onPlayTrack,
            onRemoveTrack = { trackId -> onRemoveTrackFromPlaylist(selectedPlaylist.id, trackId) },
            onDeletePlaylist = { onDeletePlaylist(selectedPlaylist) },
            modifier = modifier
        )
        return
    }

    Box(modifier = modifier.fillMaxSize().testTag("library_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 150.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Library",
                            style = MaterialTheme.typography.headlineLarge,
                            color = SoftWhite
                        )
                        Text(
                            text = "Locally saved collections & playlists",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey
                        )
                    }

                    // Floating Create Playlist Icon
                    IconButton(
                        onClick = onCreatePlaylistClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                            .testTag("create_playlist_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Playlist",
                            tint = Color(0xFF08090B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Segmented Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LibraryTabItem(
                        title = "Playlists (${localPlaylists.size})",
                        isSelected = currentTab == LibraryTab.PLAYLISTS,
                        onClick = { onTabSelected(LibraryTab.PLAYLISTS) }
                    )
                    LibraryTabItem(
                        title = "Liked Songs (${likedTracks.size})",
                        isSelected = currentTab == LibraryTab.LIKED_SONGS,
                        onClick = { onTabSelected(LibraryTab.LIKED_SONGS) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tab Content
            when (currentTab) {
                LibraryTab.PLAYLISTS -> {
                    if (localPlaylists.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                title = "No Playlists Yet",
                                subtitle = "Create your own custom playlists stored locally in VYBORA.",
                                actionText = "Create First Playlist",
                                onAction = onCreatePlaylistClick
                            )
                        }
                    } else {
                        items(localPlaylists) { playlist ->
                            PlaylistItemRow(
                                playlist = playlist,
                                onClick = { onOpenPlaylistDetail(playlist) },
                                onDelete = { onDeletePlaylist(playlist) }
                            )
                        }
                    }
                }
                LibraryTab.LIKED_SONGS, LibraryTab.ARTISTS, LibraryTab.ALBUMS -> {
                    if (likedTracks.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                title = "No Liked Songs",
                                subtitle = "Tap the heart icon on any track to save it here for quick access.",
                                actionText = null,
                                onAction = {}
                            )
                        }
                    } else {
                        // Play All Liked Songs Button
                        item {
                            Button(
                                onClick = { onPlayTrack(likedTracks.first(), likedTracks) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color(0xFF08090B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Play All Liked Songs (${likedTracks.size})",
                                    color = Color(0xFF08090B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(likedTracks) { track ->
                            LikedTrackRow(
                                track = track,
                                onPlay = { onPlayTrack(track, likedTracks) },
                                onUnlike = { onToggleLikeTrack(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) EmeraldGreen else VyboraSurfaceGlass)
            .border(width = 1.dp, color = if (isSelected) EmeraldGreen else GlassBorder, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color(0xFF08090B) else SoftWhite
        )
    }
}

@Composable
private fun PlaylistItemRow(
    playlist: Playlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1E212B))
                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverUrl.isNotBlank()) {
                AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = playlist.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleMedium,
                color = SoftWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3331E981))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Local",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${playlist.trackCount} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGrey
                )
            }
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MutedGrey
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete Playlist") },
                    onClick = {
                        onDelete()
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LikedTrackRow(
    track: Track,
    onPlay: () -> Unit,
    onUnlike: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E212B))
                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = track.artworkUrl,
                contentDescription = track.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = SoftWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • ${track.durationFormatted}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onUnlike) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Unlike",
                tint = EmeraldGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PlaylistDetailView(
    playlist: Playlist,
    tracks: List<Track>,
    onBack: () -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onRemoveTrack: (String) -> Unit,
    onDeletePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("playlist_detail_view"),
        contentPadding = PaddingValues(bottom = 150.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftWhite
                    )
                }
                Text(
                    text = "Playlist Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftWhite
                )
                IconButton(onClick = onDeletePlaylist) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Playlist",
                        tint = Color(0xFFEF4444)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E212B))
                        .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = playlist.coverUrl,
                        contentDescription = playlist.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = playlist.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = SoftWhite
                )
                if (playlist.description.isNotBlank()) {
                    Text(
                        text = playlist.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = "${tracks.size} tracks • Stored locally in VYBORA",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (tracks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onPlayTrack(tracks.first(), tracks) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF08090B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Play All",
                            color = Color(0xFF08090B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (tracks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "This playlist is empty",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite
                    )
                    Text(
                        text = "Search or browse tracks and choose 'Add to Playlist' to build your collection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            items(tracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayTrack(track, tracks) }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E212B))
                    ) {
                        AsyncImage(
                            model = track.artworkUrl,
                            contentDescription = track.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = SoftWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.artist} • ${track.durationFormatted}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = { onRemoveTrack(track.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MutedGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryView(
    title: String,
    subtitle: String,
    actionText: String?,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LibraryMusic,
            contentDescription = null,
            tint = MutedGrey,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = SoftWhite
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGrey,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (actionText != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = actionText, color = Color(0xFF08090B), fontWeight = FontWeight.Bold)
            }
        }
    }
}
