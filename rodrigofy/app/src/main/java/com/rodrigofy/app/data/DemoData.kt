package com.rodrigofy.app.data

/**
 * Offline demo catalogue, used whenever there is no live Spotify session
 * (no token, no network, or an API error). Track and album titles are
 * used purely as factual metadata to theme the UI — no lyrics, audio, or
 * artwork are bundled or reproduced anywhere in this project.
 */
object DemoData {

    private fun image(url: String) = SpotifyImage(url = url)

    val sourAlbum = SpotifyAlbum(
        id = "album_sour",
        name = "SOUR",
        images = listOf(image("https://placehold.co/500x500/2D1B4E/F3E8FF?text=SOUR")),
        releaseDate = "2021-05-21"
    )

    val gutsAlbum = SpotifyAlbum(
        id = "album_guts",
        name = "GUTS",
        images = listOf(image("https://placehold.co/500x500/4A1D96/F3E8FF?text=GUTS")),
        releaseDate = "2023-09-08"
    )

    val artist = SpotifyArtist(
        id = "artist_or",
        name = "Olivia Rodrigo",
        images = listOf(image("https://placehold.co/500x500/7C3AED/F3E8FF?text=OR"))
    )

    val sourTracks: List<SpotifyTrack> = listOf(
        "brutal", "traitor", "drivers license", "1 step forward, 3 steps back",
        "deja vu", "good 4 u", "enough for you", "happier",
        "jealousy, jealousy", "favorite crime", "hope ur ok"
    ).mapIndexed { index, title ->
        SpotifyTrack(
            id = "sour_$index",
            name = title,
            artists = listOf(artist),
            album = sourAlbum,
            durationMs = (150_000..260_000).random().toLong()
        )
    }

    val gutsTracks: List<SpotifyTrack> = listOf(
        "all-american bitch", "bad idea right?", "vampire", "lacy",
        "ballad of a homeschooled girl", "making the bed", "logical",
        "get him back!", "love is embarrassing", "the grudge",
        "pretty isn't pretty", "world tour", "teenage dream"
    ).mapIndexed { index, title ->
        SpotifyTrack(
            id = "guts_$index",
            name = title,
            artists = listOf(artist),
            album = gutsAlbum,
            durationMs = (140_000..250_000).random().toLong()
        )
    }

    val playlists: List<SpotifyPlaylist> = listOf(
        SpotifyPlaylist(
            id = "pl_sour",
            name = "SOUR",
            description = "the good 4 u era.",
            images = sourAlbum.images,
            tracks = sourTracks
        ),
        SpotifyPlaylist(
            id = "pl_guts",
            name = "GUTS",
            description = "you seem pretty sad for a girl in love.",
            images = gutsAlbum.images,
            tracks = gutsTracks
        ),
        SpotifyPlaylist(
            id = "pl_mixtape",
            name = "vampire energy only",
            description = "for when logical isn't an option.",
            images = gutsAlbum.images,
            tracks = (gutsTracks + sourTracks).shuffled().take(10)
        )
    )

    val featuredTrack: SpotifyTrack = gutsTracks.first { it.name == "vampire" }
}
