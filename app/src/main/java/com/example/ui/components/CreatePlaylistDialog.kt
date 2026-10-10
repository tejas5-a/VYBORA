package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.VyboraSurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    if (!isOpen) return

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("create_playlist_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(VyboraSurfaceElevated)
                .border(width = 1.dp, color = GlassBorder, shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "New Playlist",
                style = MaterialTheme.typography.titleLarge,
                color = SoftWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Give your playlist a title and vibe description.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGrey,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Playlist Name") },
                placeholder = { Text("e.g., Midnight Driving") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playlist_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldGreen,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                placeholder = { Text("e.g., Synth beats and night skies") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldGreen,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = SoftWhite,
                    unfocusedTextColor = SoftWhite
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel", color = MutedGrey)
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onCreate(title.trim(), description.trim())
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("confirm_create_playlist_button")
                ) {
                    Text("Create", color = Color(0xFF08090B), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
