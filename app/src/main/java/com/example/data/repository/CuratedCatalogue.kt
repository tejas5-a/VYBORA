package com.example.data.repository

import com.example.data.model.Album
import com.example.data.model.Artist
import com.example.data.model.MusicCategory
import com.example.data.model.Playlist
import com.example.data.model.Track

object CuratedCatalogue {

    // Reliable royalty-free audio streams for audio playback verification
    private const val STREAM_SYNTHWAVE = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    private const val STREAM_LOFI = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
    private const val STREAM_CHILL = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    private const val STREAM_ELECTRO = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
    private const val STREAM_AMBIENT = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"

    // High quality aesthetic album artwork
    private const val ART_CYBER = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
    private const val ART_NEON = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80"
    private const val ART_RETRO = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"
    private const val ART_CHILL = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80"
    private const val ART_NIGHT = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80"
    private const val ART_VIBE = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80"
    private const val ART_ACOUSTIC = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80"

    val trendingTracks = listOf(
        Track(
            id = "curated_1",
            title = "Midnight Horizon",
            artist = "Aetheria & Kairo",
            album = "Neon Drift",
            durationMs = 214000L,
            artworkUrl = ART_CYBER,
            audioUrl = STREAM_SYNTHWAVE,
            genre = "Synthwave",
            releaseYear = "2024"
        ),
        Track(
            id = "curated_2",
            title = "Glass Reverie",
            artist = "Sola Eclipse",
            album = "Prism Sessions",
            durationMs = 186000L,
            artworkUrl = ART_NEON,
            audioUrl = STREAM_LOFI,
            genre = "Chillwave",
            releaseYear = "2024"
        ),
        Track(
            id = "curated_3",
            title = "Velvet Pulse",
            artist = "Komorebi",
            album = "Quiet Architecture",
            durationMs = 245000L,
            artworkUrl = ART_RETRO,
            audioUrl = STREAM_CHILL,
            genre = "Lo-Fi Beats",
            releaseYear = "2023"
        ),
        Track(
            id = "curated_4",
            title = "Starlight Continuum",
            artist = "Astral Echo",
            album = "Infinite Glow",
            durationMs = 198000L,
            artworkUrl = ART_CHILL,
            audioUrl = STREAM_ELECTRO,
            genre = "Electronic",
            releaseYear = "2024"
        ),
        Track(
            id = "curated_5",
            title = "Emerald Aurora",
            artist = "Vybora Sound Collective",
            album = "Vibe Odyssey",
            durationMs = 230000L,
            artworkUrl = ART_NIGHT,
            audioUrl = STREAM_AMBIENT,
            genre = "Ambient",
            releaseYear = "2024"
        ),
        Track(
            id = "curated_6",
            title = "Tokyo Raindrops",
            artist = "Kenji Mori",
            album = "Subway Nocturne",
            durationMs = 175000L,
            artworkUrl = ART_VIBE,
            audioUrl = STREAM_LOFI,
            genre = "Lo-Fi Beats",
            releaseYear = "2024"
        ),
        Track(
            id = "curated_7",
            title = "Golden Hour Mirage",
            artist = "Sierra Sol",
            album = "Desert Bloom",
            durationMs = 205000L,
            artworkUrl = ART_ACOUSTIC,
            audioUrl = STREAM_CHILL,
            genre = "Indie Chill",
            releaseYear = "2023"
        )
    )

    val popularArtists = listOf(
        Artist(
            id = "art_1",
            name = "Aetheria",
            imageUrl = ART_CYBER,
            monthlyListeners = "4.2M monthly listeners",
            genres = listOf("Synthwave", "Cyberpunk", "Retrowave"),
            bio = "Synthesizer architect weaving cinematic nostalgia with futuristic neon soundscapes."
        ),
        Artist(
            id = "art_2",
            name = "Sola Eclipse",
            imageUrl = ART_NEON,
            monthlyListeners = "6.8M monthly listeners",
            genres = listOf("Electronic", "Downtempo", "Vaporwave"),
            bio = "Multi-instrumentalist creating textured downtempo vibes and lush vocal layers."
        ),
        Artist(
            id = "art_3",
            name = "Komorebi",
            imageUrl = ART_RETRO,
            monthlyListeners = "2.9M monthly listeners",
            genres = listOf("Lo-Fi", "Jazz Hop", "Instrumental"),
            bio = "Warm tape cassettes, vinyl crackle, and soothing Rhodes melodies."
        ),
        Artist(
            id = "art_4",
            name = "Astral Echo",
            imageUrl = ART_CHILL,
            monthlyListeners = "5.1M monthly listeners",
            genres = listOf("Deep House", "Progressive", "Melodic"),
            bio = "Pulsing basslines and crystalline synth leads tailored for late night drives."
        )
    )

    val featuredPlaylists = listOf(
        Playlist(
            id = "feat_pl_1",
            title = "VYBORA High-Res Vibe",
            description = "Mastered in 24-bit 96kHz. Spatial electronic & modern beats.",
            coverUrl = ART_CYBER,
            trackCount = 38,
            isLocal = false
        ),
        Playlist(
            id = "feat_pl_2",
            title = "Night Drive Kyoto",
            description = "Synthwave, dark electro, and neon reflections on asphalt.",
            coverUrl = ART_NEON,
            trackCount = 45,
            isLocal = false
        ),
        Playlist(
            id = "feat_pl_3",
            title = "Deep Focus Flow",
            description = "Gentle binaural lofi rhythms to lock into deep work.",
            coverUrl = ART_CHILL,
            trackCount = 60,
            isLocal = false
        ),
        Playlist(
            id = "feat_pl_4",
            title = "Coffeehouse Acoustic",
            description = "Intimate strings, wooden acoustic guitars, and serene vocals.",
            coverUrl = ART_ACOUSTIC,
            trackCount = 32,
            isLocal = false
        )
    )

    val categories = listOf(
        MusicCategory("cat_synth", "Synthwave", 0xFF8B5CF6, 0xFF3B1C72, "waves"),
        MusicCategory("cat_lofi", "Lo-Fi Beats", 0xFF31E981, 0xFF0D5C35, "nightlife"),
        MusicCategory("cat_electronic", "Electronic", 0xFF06B6D4, 0xFF0E7490, "album"),
        MusicCategory("cat_hiphop", "Hip-Hop", 0xFFF59E0B, 0xFF78350F, "mic"),
        MusicCategory("cat_chill", "Chillout", 0xFF38BDF8, 0xFF0369A1, "spa"),
        MusicCategory("cat_rock", "Modern Rock", 0xFFF43F5E, 0xFF881337, "graphic_eq"),
        MusicCategory("cat_jazz", "Smooth Jazz", 0xFFD946EF, 0xFF701A75, "piano"),
        MusicCategory("cat_ambient", "Ambient Space", 0xFF10B981, 0xFF064E3B, "star")
    )
}
