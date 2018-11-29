package com.pavelbobrovko.garr.data.net

import com.pavelbobrovko.garr.data.entity.auth.EmailAuthRequest
import com.pavelbobrovko.garr.data.entity.auth.EmailAuthResponse
import com.pavelbobrovko.garr.data.entity.auth.OAuthSignRequest
import com.pavelbobrovko.garr.data.entity.auth.OAuthSigninResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticationApi {

    @POST("signupNewUser")
    fun emailRegistration(@Query("key") apiKey: String, @Body req: EmailAuthRequest)
            : Observable<EmailAuthResponse>

    @POST("verifyPassword")
    fun emailSigin(@Query("key") apiKey: String, @Body req: EmailAuthRequest)
            : Observable<EmailAuthResponse>

    @POST("verifyAssertion")
    fun oAuthSign(@Query("key") apiKey: String, @Body req: OAuthSignRequest)
            : Observable<OAuthSigninResponse>

}