package com.example.ui.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Track
import com.example.playback.PlaybackState
import com.example.playback.PlaybackStatus
import com.example.playback.RepeatMode
import com.example.ui.theme.DarkGrey
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraBackground
import com.example.ui.theme.VyboraSurfaceElevated
import com.example.ui.theme.VyboraSurfaceGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingSheet(
    isOpen: Boolean,
    playbackState: PlaybackState,
    isLiked: Boolean,
    onClose: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: (Track) -> Unit,
    onPlayQueueTrack: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = isOpen) {
        onClose()
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val track = playbackState.currentTrack ?: return@AnimatedVisibility
        var showQueueSheet by remember { mutableStateOf(false) }

        // Scrubbing state
        var isDraggingSlider by remember { mutableStateOf(false) }
        var dragProgress by remember { mutableFloatStateOf(0f) }

        val currentProgress = if (isDraggingSlider) dragProgress else playbackState.progress

        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("now_playing_sheet")
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF14241B),
                            VyboraSurfaceElevated,
                            VyboraBackground
                        )
                    )
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("player_collapse_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Collapse Player",
                            tint = SoftWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Source & Audio Engine Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x33000000))
                            .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = playbackState.audioSourceLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftWhite
                        )
                    }

                    // Queue button
                    IconButton(
                        onClick = { showQueueSheet = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("player_queue_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Playback Queue",
                            tint = SoftWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Album Artwork
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF1E212B))
                        .border(width = 1.5.dp, color = GlassBorder, shape = RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (track.artworkUrl.isNotBlank()) {
                        AsyncImage(
                            model = track.artworkUrl,
                            contentDescription = track.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Track Title, Artist, & Like Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            color = SoftWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MutedGrey,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onToggleLike(track) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("player_like_button")
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Remove from favorites" else "Add to favorites",
                            tint = if (isLiked) EmeraldGreen else MutedGrey,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Scrubbable Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = currentProgress,
                        onValueChange = {
                            isDraggingSlider = true
                            dragProgress = it
                        },
                        onValueChangeFinished = {
                            isDraggingSlider = false
                            val targetMs = (dragProgress * playbackState.durationMs).toLong()
                            onSeekTo(targetMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = SoftWhite,
                            activeTrackColor = EmeraldGreen,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_progress_slider")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = playbackState.currentPositionFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                        Text(
                            text = playbackState.durationFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                    }
                }

                // Playback Controls Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    IconButton(
                        onClick = onToggleShuffle,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("player_shuffle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (playbackState.isShuffle) EmeraldGreen else MutedGrey,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Previous
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("player_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = SoftWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Big Play/Pause
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                            .clickable(onClick = onTogglePlayPause)
                            .testTag("player_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playbackState.status == PlaybackStatus.BUFFERING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp,
                                color = VyboraBackground
                            )
                        } else {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = VyboraBackground,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Next
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = SoftWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Repeat
                    IconButton(
                        onClick = onToggleRepeat,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("player_repeat_button")
                    ) {
                        Icon(
                            imageVector = if (playbackState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (playbackState.repeatMode != RepeatMode.OFF) EmeraldGreen else MutedGrey,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Audio Format Info Badge
                Text(
                    text = playbackState.audioQualityBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // Queue Bottom Sheet
        if (showQueueSheet) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { showQueueSheet = false },
                sheetState = sheetState,
                containerColor = VyboraSurfaceElevated
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Playing Next",
                        style = MaterialTheme.typography.titleLarge,
                        color = SoftWhite,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(playbackState.queue) { index, qTrack ->
                            val isCurrent = index == playbackState.queueIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCurrent) Color(0x3331E981) else Color.Transparent)
                                    .clickable {
                                        onPlayQueueTrack(qTrack)
                                        showQueueSheet = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCurrent) EmeraldGreen else MutedGrey,
                                    modifier = Modifier.width(28.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = qTrack.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                        color = if (isCurrent) EmeraldGreen else SoftWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = qTrack.artist,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MutedGrey,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = qTrack.durationFormatted,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
