package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Track
import com.example.playback.PlaybackState
import com.example.playback.PlaybackStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies VYBORA app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VYBORA", appName)
  }

  @Test
  fun `track duration format calculates correctly`() {
    val track = Track(
      id = "test_1",
      title = "Midnight Horizon",
      artist = "Aetheria",
      durationMs = 214000L
    )
    assertEquals("3:34", track.durationFormatted)
  }

  @Test
  fun `playback state progress and navigation flags are computed properly`() {
    val track1 = Track(id = "1", title = "Track 1", artist = "Artist 1", durationMs = 120000L)
    val track2 = Track(id = "2", title = "Track 2", artist = "Artist 2", durationMs = 120000L)

    val state = PlaybackState(
      currentTrack = track1,
      status = PlaybackStatus.PLAYING,
      currentPositionMs = 60000L,
      durationMs = 120000L,
      queue = listOf(track1, track2),
      queueIndex = 0
    )

    assertEquals(0.5f, state.progress, 0.001f)
    assertTrue(state.isPlaying)
    assertTrue(state.hasNext)
    assertTrue(state.hasPrevious) // True because currentPositionMs > 3000ms allows restarting current track
    assertEquals("1:00", state.currentPositionFormatted)
    assertEquals("2:00", state.durationFormatted)

    // Verify hasPrevious is false when at start of first track
    val startState = state.copy(currentPositionMs = 0L, queueIndex = 0)
    assertFalse(startState.hasPrevious)
  }
}
