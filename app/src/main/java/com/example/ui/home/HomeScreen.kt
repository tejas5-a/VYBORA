package com.example.ui.home

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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Artist
import com.example.data.model.MusicCategory
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated
import com.example.ui.theme.VyboraSurfaceGlass
import java.util.Calendar

@Composable
fun HomeScreen(
    trendingTracks: List<Track>,
    featuredPlaylists: List<Playlist>,
    popularArtists: List<Artist>,
    categories: List<MusicCategory>,
    selectedVibe: String,
    onSelectVibe: (String) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onOpenPlaylist: (Playlist) -> Unit,
    onOpenAddToPlaylist: (Track) -> Unit,
    onToggleLikeTrack: (Track) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember { getGreeting() }
    val vibes = listOf("All", "Late Night", "Focus", "Chillwave", "Synthwave", "Lo-Fi")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        // Top Header
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
                        text = greeting,
                        style = MaterialTheme.typography.labelLarge,
                        color = MutedGrey
                    )
                    Text(
                        text = "VYBORA",
                        style = MaterialTheme.typography.headlineLarge,
                        color = SoftWhite
                    )
                }

                // Profile Avatar Shortcut
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E212B))
                        .border(width = 1.5.dp, color = EmeraldGreen, shape = CircleShape)
                        .clickable(onClick = onNavigateToProfile)
                        .testTag("home_profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Profile",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Vibe Filters Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vibes) { vibe ->
                    val isSelected = vibe == selectedVibe
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) EmeraldGreen else VyboraSurfaceGlass)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) EmeraldGreen else GlassBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectVibe(vibe) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = vibe,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color(0xFF08090B) else SoftWhite
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section: Trending Tracks Hero Carousel
        item {
            Text(
                text = "Trending Now",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trendingTracks) { track ->
                    HeroTrackCard(
                        track = track,
                        onPlay = { onPlayTrack(track, trendingTracks) },
                        onAddToPlaylist = { onOpenAddToPlaylist(track) },
                        onToggleLike = { onToggleLikeTrack(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Section: Featured Playlists
        item {
            Text(
                text = "Featured Playlists",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(featuredPlaylists) { playlist ->
                    PlaylistCard(
                        playlist = playlist,
                        onClick = { onOpenPlaylist(playlist) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Section: Popular Artists
        item {
            Text(
                text = "Popular Artists",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(popularArtists) { artist ->
                    ArtistCard(artist = artist)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Section: Explore Categories & Moods
        item {
            Text(
                text = "Browse Moods & Genres",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // 2-column discovery categories
        items(categories.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { cat ->
                    CategoryCard(
                        category = cat,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroTrackCard(
    track: Track,
    onPlay: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onToggleLike: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(VyboraSurfaceElevated)
            .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onPlay)
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(156.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B1E28)),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = track.artworkUrl,
                    contentDescription = track.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Quick Play FAB
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                        .clickable(onClick = onPlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color(0xFF08090B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = SoftWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MutedGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
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
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B1E28))
                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = playlist.coverUrl,
                contentDescription = playlist.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = SoftWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = playlist.description.ifBlank { "${playlist.trackCount} tracks" },
            style = MaterialTheme.typography.labelSmall,
            color = MutedGrey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ArtistCard(artist: Artist) {
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF1B1E28))
                .border(width = 1.5.dp, color = GlassBorder, shape = CircleShape)
        ) {
            AsyncImage(
                model = artist.imageUrl,
                contentDescription = artist.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = SoftWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Artist",
            style = MaterialTheme.typography.labelSmall,
            color = EmeraldGreen
        )
    }
}

@Composable
private fun CategoryCard(
    category: MusicCategory,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(category.startColorHex),
                        Color(category.endColorHex)
                    )
                )
            )
            .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleMedium,
            color = SoftWhite
        )
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..22 -> "Good Evening"
        else -> "Late Night Vibes"
    }
}
