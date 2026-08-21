package com.rodrigofy.app.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.rodrigofy.app.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed interface AuthState {
    data object LoggedOut : AuthState
    data object LoggingIn : AuthState
    data class LoggedIn(val accessToken: String) : AuthState
    data class Error(val message: String) : AuthState
}

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("scope") val scope: String? = null
)

/**
 * OAuth 2.0 Authorization Code flow + PKCE for the Spotify Web API
 * (https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow).
 *
 * No client secret ever ships in this app — the whole point of PKCE is
 * that a public client doesn't need one. Tokens are persisted in
 * [EncryptedSharedPreferences] (AES-256-GCM, Android Keystore-backed) so
 * nothing sensitive ever sits in plaintext on disk.
 */
class AuthManager(private val context: Context) {

    companion object {
        private const val AUTH_ENDPOINT = "https://accounts.spotify.com/authorize"
        private const val TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token"
        private const val PREFS_NAME = "rodrigofy_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_CODE_VERIFIER = "code_verifier"
        private const val KEY_STATE = "oauth_state"

        val SCOPES = listOf(
            "user-read-private",
            "user-read-email",
            "user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "playlist-read-private",
            "user-library-read",
            "streaming"
        ).joinToString(" ")
    }

    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val httpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)?.let { token ->
            _authState.value = AuthState.LoggedIn(token)
        }
    }

    /** Step 1 — builds the Spotify authorize URL and hands back an Intent to launch it. */
    fun buildLoginIntent(): Intent {
        val verifier = PkceUtil.generateCodeVerifier()
        val challenge = PkceUtil.generateCodeChallenge(verifier)
        val state = PkceUtil.generateState()

        encryptedPrefs.edit()
            .putString(KEY_CODE_VERIFIER, verifier)
            .putString(KEY_STATE, state)
            .apply()

        val uri = AUTH_ENDPOINT.toUri().buildUpon()
            .appendQueryParameter("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("state", state)
            .appendQueryParameter("scope", SCOPES)
            .build()

        _authState.value = AuthState.LoggingIn
        return Intent(Intent.ACTION_VIEW, uri)
    }

    /** Step 2 — call from onNewIntent() when spotifyclient://callback fires. */
    suspend fun handleRedirect(intent: Intent) {
        val data: Uri = intent.data ?: return
        val returnedState = data.getQueryParameter("state")
        val savedState = encryptedPrefs.getString(KEY_STATE, null)
        val code = data.getQueryParameter("code")
        val error = data.getQueryParameter("error")

        when {
            error != null ->
                _authState.value = AuthState.Error("Spotify denied access: $error")

            returnedState == null || returnedState != savedState ->
                _authState.value = AuthState.Error("State mismatch — possible CSRF, aborting.")

            code == null ->
                _authState.value = AuthState.Error("No authorization code returned.")

            else -> exchangeCodeForToken(code)
        }
    }

    private suspend fun exchangeCodeForToken(code: String) {
        val verifier = encryptedPrefs.getString(KEY_CODE_VERIFIER, null)
        if (verifier == null) {
            _authState.value = AuthState.Error("Missing PKCE verifier — please sign in again.")
            return
        }
        try {
            val response: HttpResponse = httpClient.submitForm(
                url = TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
                    append("code_verifier", verifier)
                }
            )
            if (response.status.isSuccess()) {
                val token: TokenResponse = response.body()
                persistTokens(token)
                _authState.value = AuthState.LoggedIn(token.accessToken)
            } else {
                _authState.value = AuthState.Error("Token exchange failed (${response.status.value}).")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error during token exchange.")
        }
    }

    suspend fun refreshTokenIfNeeded(): String? {
        val refreshToken = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        return try {
            val response: HttpResponse = httpClient.submitForm(
                url = TOKEN_ENDPOINT,
                formParameters = Parameters.build {
                    append("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                }
            )
            if (response.status.isSuccess()) {
                val token: TokenResponse = response.body()
                persistTokens(token, fallbackRefreshToken = refreshToken)
                _authState.value = AuthState.LoggedIn(token.accessToken)
                token.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        encryptedPrefs.edit().clear().apply()
        _authState.value = AuthState.LoggedOut
    }

    fun currentAccessToken(): String? = encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)

    private fun persistTokens(token: TokenResponse, fallbackRefreshToken: String? = null) {
        encryptedPrefs.edit()
            .putString(KEY_ACCESS_TOKEN, token.accessToken)
            .putString(KEY_REFRESH_TOKEN, token.refreshToken ?: fallbackRefreshToken)
            .apply()
    }
}
