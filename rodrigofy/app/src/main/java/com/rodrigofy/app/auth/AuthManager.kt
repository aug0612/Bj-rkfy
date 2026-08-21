package com.example.spotifymaterial.network

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object AuthManager {
    // Spotify Client ID - REMPLACE PAR TON CLIENT ID DE LA SPOTIFY DEVELOPER DASHBOARD
    var clientId: String = "VOTRE_SPOTIFY_CLIENT_ID"
    const val REDIRECT_URI = "spotifyclient://callback"
    const val SCOPES = "user-read-private user-read-email user-library-read user-read-playback-state user-modify-playback-state streaming"

    private var codeVerifier: String = ""

    fun generateCodeVerifier(): String {
        val sr = SecureRandom()
        val code = ByteArray(32)
        sr.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(bytes, 0, bytes.size)
        val digest = md.digest()
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun launchSpotifyLogin(context: Context) {
        codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        val authUri = Uri.parse("https://accounts.spotify.com/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("scope", SCOPES)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri)
        context.startActivity(intent)
    }
}
