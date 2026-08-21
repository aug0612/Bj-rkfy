package com.rodrigofy.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyImage(
    val url: String,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class SpotifyArtist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage> = emptyList()
)

@Serializable
data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage> = emptyList(),
    @SerialName("release_date") val releaseDate: String? = null
)

@Serializable
data class SpotifyTrack(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtist>,
    val album: SpotifyAlbum,
    @SerialName("duration_ms") val durationMs: Long,
    val explicit: Boolean = false,
    val popularity: Int = 0,
    @SerialName("preview_url") val previewUrl: String? = null
) {
    val primaryArtistName: String
        get() = artists.firstOrNull()?.name ?: "Unknown Artist"
}

@Serializable
data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val description: String? = null,
    val images: List<SpotifyImage> = emptyList(),
    val tracks: List<SpotifyTrack> = emptyList()
)

@Serializable
data class SpotifyUserProfile(
    val id: String,
    @SerialName("display_name") val displayName: String? = null,
    val images: List<SpotifyImage> = emptyList()
)

/** Local (non-API) representation of what's currently loaded in the player. */
data class PlaybackState(
    val track: SpotifyTrack?,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.OFF
)

enum class RepeatMode { OFF, CONTEXT, TRACK }
