package com.pavelbobrovko.garr.data.net

import android.util.Log
import com.google.gson.Gson
import com.pavelbobrovko.garr.data.entity.auth.EmailAuthRequest
import com.pavelbobrovko.garr.data.entity.auth.EmailAuthResponse
import com.pavelbobrovko.garr.data.entity.auth.OAuthSignRequest
import com.pavelbobrovko.garr.data.entity.auth.OAuthSigninResponse
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AuthienticationService(apiUrl: String) {

    private val restApi: AuthenticationApi

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

        restApi = retrofit.create(AuthenticationApi::class.java)
    }

    fun emailRegistration( req: EmailAuthRequest): Observable<EmailAuthResponse>{
        Log.d(ConstantInterface.LOG_TAG,"emailRegistration ${req.toString()}")
        return restApi.emailRegistration(RestApi.apiKey,req)
    }

    fun emailSignin(req: EmailAuthRequest): Observable<EmailAuthResponse>{
        Log.d(ConstantInterface.LOG_TAG,"emailSignIn ${req.toString()}")
        return restApi.emailSigin(RestApi.apiKey,req)
    }

    fun oAuthSignin(req: OAuthSignRequest): Observable<OAuthSigninResponse>{
        Log.d(ConstantInterface.LOG_TAG,req.toString())
        return restApi.oAuthSign(RestApi.apiKey,req)
    }


}