package com.example.ui.search

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MusicCategory
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.data.repository.SearchResult
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated
import com.example.ui.theme.VyboraSurfaceGlass
import com.example.ui.viewmodel.SearchFilter

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    selectedFilter: SearchFilter,
    onSelectFilter: (SearchFilter) -> Unit,
    searchResults: SearchResult,
    isSearching: Boolean,
    recentSearches: List<String>,
    onSelectRecentSearch: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearAllRecentSearches: () -> Unit,
    categories: List<MusicCategory>,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onOpenPlaylist: (Playlist) -> Unit,
    onOpenAddToPlaylist: (Track) -> Unit,
    onToggleLikeTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen"),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // Search Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SoftWhite
                )
                Text(
                    text = "Search millions of tracks, artists & playlists",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGrey,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    placeholder = {
                        Text(
                            text = "Songs, artists, podcasts or albums",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (query.isNotBlank()) EmeraldGreen else MutedGrey
                        )
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldGreen
                            )
                        } else if (query.isNotBlank()) {
                            IconButton(onClick = onClearQuery) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MutedGrey
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = VyboraSurfaceElevated,
                        unfocusedContainerColor = VyboraSurfaceElevated,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = SoftWhite,
                        unfocusedTextColor = SoftWhite
                    ),
                    singleLine = true
                )
            }
        }

        // Entity Filter Chips
        if (query.isNotBlank()) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SearchFilter.values()) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) EmeraldGreen else VyboraSurfaceGlass)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldGreen else GlassBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { onSelectFilter(filter) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color(0xFF08090B) else SoftWhite
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Spotify Source Indicator Banner
        if (query.isNotBlank() && searchResults.isFromSpotify) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2231E981))
                        .border(1.dp, Color(0x4431E981), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Spotify Catalogue results",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldGreen
                    )
                }
            }
        }

        // Search Results Section
        if (query.isNotBlank()) {
            val showTracks = selectedFilter == SearchFilter.ALL || selectedFilter == SearchFilter.TRACKS
            val showArtists = selectedFilter == SearchFilter.ALL || selectedFilter == SearchFilter.ARTISTS
            val showPlaylists = selectedFilter == SearchFilter.ALL || selectedFilter == SearchFilter.PLAYLISTS

            if (showTracks && searchResults.tracks.isNotEmpty()) {
                item {
                    Text(
                        text = "Songs",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                items(searchResults.tracks) { track ->
                    SearchTrackRow(
                        track = track,
                        onPlay = { onPlayTrack(track, searchResults.tracks) },
                        onAddToPlaylist = { onOpenAddToPlaylist(track) },
                        onToggleLike = { onToggleLikeTrack(track) }
                    )
                }
            }

            if (showArtists && searchResults.artists.isNotEmpty()) {
                item {
                    Text(
                        text = "Artists",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                items(searchResults.artists) { artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E212B))
                        ) {
                            AsyncImage(
                                model = artist.imageUrl,
                                contentDescription = artist.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = artist.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = SoftWhite
                            )
                            Text(
                                text = if (artist.genres.isNotEmpty()) artist.genres.take(2).joinToString(" • ") else "Artist",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedGrey
                            )
                        }
                    }
                }
            }

            if (showPlaylists && searchResults.playlists.isNotEmpty()) {
                item {
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftWhite,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                items(searchResults.playlists) { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlaylist(playlist) }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E212B))
                        ) {
                            AsyncImage(
                                model = playlist.coverUrl,
                                contentDescription = playlist.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlist.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = SoftWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${playlist.trackCount} songs",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedGrey
                            )
                        }
                    }
                }
            }

            // Empty state
            if (!isSearching &&
                searchResults.tracks.isEmpty() &&
                searchResults.artists.isEmpty() &&
                searchResults.playlists.isEmpty()
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MutedGrey,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results for \"$query\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftWhite
                        )
                        Text(
                            text = "Try searching for a different track, artist or genre.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            // When Query is empty: Show Recent Searches & Discover Categories
            if (recentSearches.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftWhite
                        )
                        TextButton(onClick = onClearAllRecentSearches) {
                            Text("Clear all", color = EmeraldGreen, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                items(recentSearches) { searchItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRecentSearch(searchItem) }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MutedGrey,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = searchItem,
                                style = MaterialTheme.typography.bodyLarge,
                                color = SoftWhite
                            )
                        }
                        IconButton(
                            onClick = { onRemoveRecentSearch(searchItem) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MutedGrey,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }

            // Categories Grid
            item {
                Text(
                    text = "Explore All Categories",
                    style = MaterialTheme.typography.titleLarge,
                    color = SoftWhite,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            items(categories.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(cat.startColorHex),
                                            Color(cat.endColorHex)
                                        )
                                    )
                                )
                                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(18.dp))
                                .clickable { onQueryChange(cat.name) }
                                .padding(14.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = SoftWhite
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTrackRow(
    track: Track,
    onPlay: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleLike: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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

        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MutedGrey,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Play Now") },
                    onClick = {
                        onPlay()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Save to Liked Songs") },
                    onClick = {
                        onToggleLike()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Playlist") },
                    onClick = {
                        onAddToPlaylist()
                        showMenu = false
                    }
                )
            }
        }
    }
}
