package com.example.ui.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated
import com.example.ui.theme.VyboraSurfaceGlass

@Composable
fun ProfileScreen(
    isSpotifyConnected: Boolean,
    onConnectSpotify: () -> Unit,
    onDisconnectSpotify: () -> Unit,
    onConfigureClientId: () -> Unit,
    audioQuality: String,
    onSelectAudioQuality: (String) -> Unit,
    blurEffectsEnabled: Boolean,
    onToggleBlurEffects: () -> Unit,
    localPlaylistsCount: Int,
    likedSongsCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Title
        item {
            Text(
                text = "Account & Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = SoftWhite
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // User Avatar Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(VyboraSurfaceElevated)
                    .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF163222))
                            .border(width = 2.dp, color = EmeraldGreen, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "VYBORA Listener",
                            style = MaterialTheme.typography.titleLarge,
                            color = SoftWhite
                        )
                        Text(
                            text = "Your Music. Your Vibe.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "$localPlaylistsCount Playlists • $likedSongsCount Liked Tracks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Spotify Integration Section
        item {
            Text(
                text = "Spotify Web API Integration",
                style = MaterialTheme.typography.titleMedium,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(VyboraSurfaceElevated)
                    .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpotifyConnected) EmeraldGreen else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSpotifyConnected) "Connected to Spotify" else "Not Connected",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = SoftWhite
                            )
                        }

                        if (isSpotifyConnected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "Authenticates using official Spotify OAuth PKCE flow with secure token management. Allows searching Spotify's entire global catalogue, tracks, artists, and playlists.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isSpotifyConnected) {
                            OutlinedButton(
                                onClick = onDisconnectSpotify,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Disconnect", color = Color(0xFFEF4444))
                            }
                        } else {
                            Button(
                                onClick = onConnectSpotify,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Connect Spotify", color = Color(0xFF08090B), fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onConfigureClientId,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Client ID", color = SoftWhite)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Audio Quality & Engine Settings
        item {
            Text(
                text = "Audio & Playback Quality",
                style = MaterialTheme.typography.titleMedium,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(VyboraSurfaceElevated)
                    .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    val qualities = listOf(
                        "24-bit • 96kHz Lossless",
                        "High (320 kbps)",
                        "Standard (160 kbps)"
                    )
                    qualities.forEach { quality ->
                        val isSelected = quality == audioQuality
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectAudioQuality(quality) }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = if (isSelected) EmeraldGreen else MutedGrey,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = quality,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) EmeraldGreen else SoftWhite
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Appearance & Visual Effects
        item {
            Text(
                text = "Appearance & Glass Effects",
                style = MaterialTheme.typography.titleMedium,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(VyboraSurfaceElevated)
                    .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Glassmorphism & Frosted Blur",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SoftWhite
                        )
                        Text(
                            text = "Enables translucent player surfaces and ambient background blur.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey
                        )
                    }
                    Switch(
                        checked = blurEffectsEnabled,
                        onCheckedChange = { onToggleBlurEffects() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF08090B),
                            checkedTrackColor = EmeraldGreen
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // App Information & Legal
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VYBORA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SoftWhite
                )
                Text(
                    text = "Version 1.0.0 (Build 2026)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey
                )
                Text(
                    text = "Crafted with Kotlin, Jetpack Compose, Room & Spotify Web API",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
