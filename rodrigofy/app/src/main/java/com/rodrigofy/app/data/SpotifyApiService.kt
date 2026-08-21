package com.rodrigofy.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Thin wrapper around the Spotify Web API (https://api.spotify.com/v1).
 *
 * Every call gracefully falls back to [DemoData] whenever it can't
 * complete — no access token, no network, or a non-2xx response — so the
 * UI is always browsable, connected account or not ("logical? not an
 * option here").
 */
class SpotifyApiService {

    companion object {
        private const val BASE_URL = "https://api.spotify.com/v1"
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    @Serializable
    private data class PlaylistsResponse(val items: List<SpotifyPlaylist> = emptyList())

    @Serializable
    private data class SavedTrackItem(val track: SpotifyTrack)

    @Serializable
    private data class SavedTracksResponse(val items: List<SavedTrackItem> = emptyList())

    @Serializable
    private data class TracksWrapper(val items: List<SpotifyTrack> = emptyList())

    @Serializable
    private data class SearchResponse(val tracks: TracksWrapper? = null)

    suspend fun getCurrentUserProfile(accessToken: String): SpotifyUserProfile? =
        runCatching {
            val response = client.get("$BASE_URL/me") {
                header("Authorization", "Bearer $accessToken")
            }
            if (response.status.isSuccess()) response.body<SpotifyUserProfile>() else null
        }.getOrNull()

    suspend fun getUserPlaylists(accessToken: String?): List<SpotifyPlaylist> {
        if (accessToken.isNullOrBlank()) return DemoData.playlists
        return runCatching {
            val response = client.get("$BASE_URL/me/playlists") {
                header("Authorization", "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                response.body<PlaylistsResponse>().items
            } else {
                DemoData.playlists
            }
        }.getOrDefault(DemoData.playlists)
    }

    suspend fun getSavedTracks(accessToken: String?): List<SpotifyTrack> {
        val demoFallback = DemoData.sourTracks + DemoData.gutsTracks
        if (accessToken.isNullOrBlank()) return demoFallback
        return runCatching {
            val response = client.get("$BASE_URL/me/tracks") {
                header("Authorization", "Bearer $accessToken")
            }
            if (response.status.isSuccess()) {
                response.body<SavedTracksResponse>().items.map { it.track }
            } else {
                demoFallback
            }
        }.getOrDefault(demoFallback)
    }

    suspend fun search(accessToken: String?, query: String): List<SpotifyTrack> {
        val demoResults = (DemoData.sourTracks + DemoData.gutsTracks).filter {
            it.name.contains(query, ignoreCase = true)
        }
        if (accessToken.isNullOrBlank()) return demoResults

        return runCatching {
            val response = client.get("$BASE_URL/search") {
                header("Authorization", "Bearer $accessToken")
                url {
                    parameters.append("q", query)
                    parameters.append("type", "track")
                    parameters.append("limit", "20")
                }
            }
            if (response.status.isSuccess()) {
                response.body<SearchResponse>().tracks?.items ?: demoResults
            } else {
                demoResults
            }
        }.getOrDefault(demoResults)
    }
}
