package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class SpotifyApiClient(private val authManager: SpotifyAuthManager) {
    private val tag = "SpotifyApiClient"

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = runBlocking { authManager.refreshAccessTokenIfNeeded() }

        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
        } else {
            original.newBuilder()
                .header("Accept", "application/json")
                .build()
        }

        var response = chain.proceed(request)

        // Handle rate limiting (429) gracefully
        if (response.code == 429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 1L
            Log.w(tag, "Rate limited by Spotify Web API, retry after $retryAfter seconds")
            try {
                Thread.sleep((retryAfter * 1000).coerceAtMost(3000))
            } catch (_: InterruptedException) {}
            response.close()
            response = chain.proceed(request)
        }

        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: SpotifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }
}
