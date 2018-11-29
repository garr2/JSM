package com.pavelbobrovko.garr.domain.repositories

import android.app.Activity
import io.reactivex.Observable

interface OAuthRepository {

    fun getOAuth2Token(clientId: String, clientSecret: String): Observable<String>
}