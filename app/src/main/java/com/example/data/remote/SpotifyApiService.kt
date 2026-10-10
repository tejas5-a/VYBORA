package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "track,artist,album,playlist",
        @Query("limit") limit: Int = 20,
        @Query("market") market: String = "US"
    ): Response<SpotifySearchResponse>

    @GET("v1/me")
    suspend fun getCurrentUser(): Response<SpotifyUserProfile>

    @GET("v1/browse/new-releases")
    suspend fun getNewReleases(
        @Query("limit") limit: Int = 10,
        @Query("country") country: String = "US"
    ): Response<SpotifySearchResponse>
}

interface SpotifyAuthService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun exchangeToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): Response<SpotifyTokenResponse>

    @FormUrlEncoded
    @POST("api/token")
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String
    ): Response<SpotifyTokenResponse>
}
