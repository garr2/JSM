package com.pavelbobrovko.garr.data.net

import com.pavelbobrovko.garr.data.entity.oAuth.OAuthResponse
import io.reactivex.Observable
import retrofit2.http.*

interface OAuth2Api {

    companion object {
        const val redirectUri = "urn:ietf:wg:oauth:2.0:oob"
        const val scope = "https://www.googleapis.com/auth/cloud-platform"
        const val accessType = "offline"
        const val grantType = "authorization_code"
        const val responseType = "code"
    }

    @GET("v4/auth")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getToken(@Query("code")code: String,
                 @Query("client_id")clientId: String,
                 @Query("client_secret")clientSecret: String,
                 @Query("redirect_uri")redirectUri: String,
                 @Query("grant_type")grantType: String,
                 @Query("code_verifier")codeVerifier: String): Observable<OAuthResponse>


    @POST("v2/auth")
    fun getCode(@Query("client_id")clientId: String,
                @Query("redirect_uri")redirectUri: String,
                @Query("response_type") responseType: String,
                @Query("scope")scope: String,
                @Query("access_type")accessType: String,
                @Query("code_challenge")codeChallenge: String): Observable<String>

    @POST("v4/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun refreshToren(@Query("client_id")clientId: String,
                     @Query("client_secret")clientSecret: String,
                     @Query("refresh_token") refreshToken: String,
                     @Query("grant_type")grantType: String): Observable<OAuthResponse>
}