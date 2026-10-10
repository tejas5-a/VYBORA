package com.example.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.MessageDigest
import java.security.SecureRandom

class SpotifyAuthManager(private val context: Context) {
    private val tag = "SpotifyAuth"
    private val prefs = context.getSharedPreferences("vybora_spotify_auth", Context.MODE_PRIVATE)

    companion object {
        const val REDIRECT_URI = "https://vybora-spotify-auth.vercel.app/api/callback"
        const val DEFAULT_CLIENT_ID = "c4d370e7048740d0bd4b4bb26b00b01c" // Customizable via Profile Settings
        const val AUTH_SCOPES = "user-read-private user-read-email playlist-read-private user-library-read"
    }

    private val authService: SpotifyAuthService by lazy {
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpotifyAuthService::class.java)
    }

    private val _isConnected = MutableStateFlow(hasValidToken())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _userProfile = MutableStateFlow<SpotifyUserProfile?>(null)
    val userProfile: StateFlow<SpotifyUserProfile?> = _userProfile.asStateFlow()

    fun getClientId(): String {
        return prefs.getString("custom_client_id", null) ?: DEFAULT_CLIENT_ID
    }

    fun setClientId(clientId: String) {
        prefs.edit().putString("custom_client_id", clientId.trim()).apply()
    }

    fun hasValidToken(): Boolean {
        val token = prefs.getString("access_token", null)
        val expiresAt = prefs.getLong("expires_at", 0L)
        return !token.isNullOrBlank() && System.currentTimeMillis() < expiresAt
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    /**
     * Initiates Spotify PKCE Authorization in external web browser
     */
    fun startAuthorizationFlow() {
        val codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        // Save verifier for exchange step
        prefs.edit().putString("code_verifier", codeVerifier).apply()

        val authUri = Uri.parse("https://accounts.spotify.com/authorize").buildUpon()
            .appendQueryParameter("client_id", getClientId())
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("scope", AUTH_SCOPES)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(tag, "Failed to launch Spotify Auth browser intent", e)
        }
    }

    suspend fun handleAuthCallback(uri: Uri): Result<String> {
        val error = uri.getQueryParameter("error")
        if (error != null) {
            Log.e(tag, "Spotify auth callback returned error: $error")
            return Result.failure(Exception("Spotify authorization cancelled or failed: $error"))
        }

        val code = uri.getQueryParameter("code")
            ?: return Result.failure(Exception("No authorization code in callback URL"))

        val codeVerifier = prefs.getString("code_verifier", null)
            ?: return Result.failure(Exception("No PKCE code verifier found"))

        return try {
            val response = authService.exchangeToken(
                clientId = getClientId(),
                grantType = "authorization_code",
                code = code,
                redirectUri = REDIRECT_URI,
                codeVerifier = codeVerifier
            )

            if (response.isSuccessful && response.body() != null) {
                val tokenBody = response.body()!!
                val expiresAt = System.currentTimeMillis() + (tokenBody.expiresIn * 1000L) - 60000L

                prefs.edit()
                    .putString("access_token", tokenBody.accessToken)
                    .putString("refresh_token", tokenBody.refreshToken)
                    .putLong("expires_at", expiresAt)
                    .apply()

                _isConnected.value = true
                Log.d(tag, "Spotify token exchanged successfully")
                Result.success(tokenBody.accessToken)
            } else {
                val errMsg = response.errorBody()?.string() ?: "Token exchange failed: ${response.code()}"
                Log.e(tag, "Token exchange failed: $errMsg")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception during token exchange", e)
            Result.failure(e)
        }
    }

    suspend fun refreshAccessTokenIfNeeded(): String? {
        val currentToken = getAccessToken()
        val expiresAt = prefs.getLong("expires_at", 0L)
        val refreshToken = prefs.getString("refresh_token", null)

        if (!currentToken.isNullOrBlank() && System.currentTimeMillis() < expiresAt) {
            return currentToken
        }

        if (refreshToken.isNullOrBlank()) {
            _isConnected.value = false
            return null
        }

        return try {
            val response = authService.refreshToken(
                clientId = getClientId(),
                refreshToken = refreshToken
            )
            if (response.isSuccessful && response.body() != null) {
                val tokenBody = response.body()!!
                val newExpiresAt = System.currentTimeMillis() + (tokenBody.expiresIn * 1000L) - 60000L

                prefs.edit()
                    .putString("access_token", tokenBody.accessToken)
                    .putLong("expires_at", newExpiresAt)
                    .apply()

                if (!tokenBody.refreshToken.isNullOrBlank()) {
                    prefs.edit().putString("refresh_token", tokenBody.refreshToken).apply()
                }

                _isConnected.value = true
                tokenBody.accessToken
            } else {
                _isConnected.value = false
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to refresh token", e)
            null
        }
    }

    fun disconnect() {
        prefs.edit()
            .remove("access_token")
            .remove("refresh_token")
            .remove("expires_at")
            .remove("code_verifier")
            .apply()
        _isConnected.value = false
        _userProfile.value = null
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val code = ByteArray(64)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
