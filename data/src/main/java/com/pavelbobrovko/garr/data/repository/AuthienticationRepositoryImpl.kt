package com.pavelbobrovko.garr.data.repository

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.GoogleAuthUtil
import com.pavelbobrovko.garr.data.entity.auth.EmailAuthRequest
import com.pavelbobrovko.garr.data.entity.auth.OAuthSignRequest
import com.pavelbobrovko.garr.data.entity.toUser
import com.pavelbobrovko.garr.data.net.AuthienticationService
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData
import com.pavelbobrovko.garr.domain.entity.User
import com.pavelbobrovko.garr.domain.repositories.AuthenticateRepository
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import io.reactivex.Observable

class AuthienticationRepositoryImpl(val restApi: AuthienticationService): AuthenticateRepository {

    override fun registerByEmail(email: String, pass: String): Observable<RegistrationUserData> {
        return restApi.emailRegistration(EmailAuthRequest(email,pass)).map {
            it.toUser()
        }
    }

    override fun signinByEmail(email: String, pass: String): Observable<RegistrationUserData> {
        return restApi.emailSignin(EmailAuthRequest(email,pass)).map {
            it.toUser()
        }
    }

    override fun signinByGoogle(idToken: String, providerId: String, requestUri: String): Observable<RegistrationUserData> {
        Log.d(ConstantInterface.LOG_TAG,"signInByGoogle")
        val postBody = "id_token=$idToken&providerId=$providerId"

       return restApi.oAuthSignin(OAuthSignRequest(requestUri,postBody))
               .map {
                   Log.d(ConstantInterface.LOG_TAG,it.toString())
                   it.toUser()
               }
    }

    override fun getOAuth(context: Context,displayName: String, accountType: String, activity: Activity): Observable<String>{
        val account = Account(displayName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)

        /*token = GoogleAuthUtil.getToken(
                MainActivity.this,
                mGoogleApiClient.getAccountName(),
                "oauth2:" + SCOPES);*/

        return Observable.just(GoogleAuthUtil.getToken(context,account,"oauth2:" + "https://www.googleapis.com/auth/cloud-platform"))
    }
}