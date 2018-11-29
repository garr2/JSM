package com.pavelbobrovko.garr.data.net

import com.google.gson.Gson
import com.pavelbobrovko.garr.data.entity.oAuth.OAuthResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class OAuth2Service(apiUrl: String) {

    private val restApi: OAuth2Api

    init {
        val okHttpBuilder = OkHttpClient.Builder()


        okHttpBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        val gson = Gson()

        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpBuilder.build())
                .build()

        restApi = retrofit.create(OAuth2Api::class.java)
    }

    fun getToken(code:String, clientId: String,clientSecret: String,
                 codeVerifier: String): Observable<OAuthResponse>{
       return restApi.getToken(code,clientId,clientSecret,OAuth2Api.redirectUri
               ,OAuth2Api.grantType,codeVerifier)
    }

    fun getCode(clientId: String,codeChallenge: String): Observable<String>{
        return restApi.getCode(clientId,OAuth2Api.redirectUri,OAuth2Api.responseType
                ,OAuth2Api.scope,OAuth2Api.accessType, codeChallenge)
    }

    fun refreshToken(clientId: String, clientSecret: String, refreshToken: String
                     ): Observable<OAuthResponse>{
        return restApi.refreshToren(clientId,clientSecret,refreshToken,OAuth2Api.grantType)
    }
}