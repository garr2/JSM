package com.pavelbobrovko.garr.data.repository

import com.pavelbobrovko.garr.data.entity.oAuth.OAuthResponse
import com.pavelbobrovko.garr.data.entity.toResultString
import com.pavelbobrovko.garr.data.net.OAuth2Service
import com.pavelbobrovko.garr.data.net.RestApi
import com.pavelbobrovko.garr.domain.repositories.OAuthRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy


object OAuthRepositoryImpl: OAuthRepository {

    private val restApi: OAuth2Service = OAuth2Service(RestApi.oAuth2Url)

    private var codeVerifier = ""
    private var codeChallenge = ""
    private var accessCode = ""
    private var clientId = ""
    private var clientSecret = ""

    private var expiriesIn = -1
    private var refreshToken = ""
    private var lastTimeUpdate: Long = -1

    private var oAuth2Token = ""

    private const val grantType: String = "authorization_code"


    override fun getOAuth2Token(clientId: String, clientSecret: String)
            : Observable<String> {
        if (oAuth2Token=="" ) {

            this.clientId = clientId
            this.clientSecret = clientSecret
            codeVerifier = generateCodeVerefier(64)
            codeChallenge = codeVerifier

            return restApi.getCode(clientId, codeChallenge)
                    .flatMap{code ->
                        accessCode = code
                        getNewToken(code,clientId,clientSecret,codeVerifier)
                                .doOnNext {
                                    oAuth2Token = it.accessToken
                                    refreshToken = it.refreshToken
                                    expiriesIn = it.expiresId
                                    lastTimeUpdate = System.currentTimeMillis()
                                }
                    }.map {
                        it.toResultString()
                    }

        } else if (System.currentTimeMillis()> lastTimeUpdate + expiriesIn * 1000){
            return refreshOAuthToken()

        }else return Observable.just(oAuth2Token)

    }

    private fun generateCodeVerefier(length: Int): String{
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        var text = ""
        for (i in 0 until length){
            text += possible[Math.floor(Math.random() * possible.length).toInt()]
        }
        return text
    }

    private fun getNewToken(code:String, clientId: String,clientSecret: String,
                            codeVerifier: String): Observable<OAuthResponse>{
       return restApi.getToken(code,clientId,clientSecret,codeVerifier)
    }

    private fun refreshOAuthToken(): Observable<String>{
        return restApi.refreshToken(clientId, clientSecret, refreshToken)
                .doOnNext{
                    oAuth2Token = it.accessToken
                    refreshToken = it.refreshToken
                    expiriesIn = it.expiresId
                    lastTimeUpdate = System.currentTimeMillis()
                }
                .map {
                    it.toResultString()
                }
    }

}