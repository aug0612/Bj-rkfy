package com.rodrigofy.app.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * PKCE (Proof Key for Code Exchange) helpers, RFC 7636.
 * No client secret is ever stored on-device — instead we rely on a fresh,
 * single-use verifier/challenge pair for every login attempt.
 */
object PkceUtil {

    private const val VERIFIER_BYTE_LENGTH = 64
    private const val STATE_BYTE_LENGTH = 16

    fun generateCodeVerifier(): String {
        val bytes = ByteArray(VERIFIER_BYTE_LENGTH)
        SecureRandom().nextBytes(bytes)
        return bytes.toBase64Url()
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray(Charsets.US_ASCII))
        return hash.toBase64Url()
    }

    fun generateState(): String {
        val bytes = ByteArray(STATE_BYTE_LENGTH)
        SecureRandom().nextBytes(bytes)
        return bytes.toBase64Url()
    }

    private fun ByteArray.toBase64Url(): String =
        Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}
