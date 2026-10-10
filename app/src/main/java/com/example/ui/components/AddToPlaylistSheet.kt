package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistSheet(
    track: Track?,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelectPlaylist: (Playlist, Track) -> Unit,
    onCreateNewPlaylist: () -> Unit
) {
    if (track == null) return

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VyboraSurfaceElevated,
        modifier = Modifier.testTag("add_to_playlist_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Add to Playlist",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Adding: \"${track.title}\" by ${track.artist}",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGrey,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            if (playlists.isEmpty()) {
                Text(
                    text = "You don't have any playlists yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGrey,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(playlists) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectPlaylist(playlist, track)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = playlist.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = SoftWhite
                                )
                                Text(
                                    text = "${playlist.trackCount} songs",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
